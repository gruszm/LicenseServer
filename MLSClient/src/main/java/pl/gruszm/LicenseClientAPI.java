package pl.gruszm;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class LicenseClientAPI
{
    private String serverAddress;
    private int serverPort;
    private String licenceUserName;
    private String licenceKey;
    private Response currentToken;
    private Timer tokenRenewalTimer;
    private Gson gson;

    public LicenseClientAPI()
    {
        gson = new Gson();
        tokenRenewalTimer = new Timer();
    }

    public void start(String serverAddress, int serverPort)
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void setLicence(String licenceUserName, String licenceKey)
    {
        this.licenceUserName = licenceUserName;
        this.licenceKey = licenceKey;
    }

    public synchronized Response getLicenceToken()
    {
        if (currentToken == null || !currentToken.isValid())
        {
            requestLicenceToken();
        }

        return currentToken;
    }

    private void requestLicenceToken()
    {
        new Thread(() ->
        {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
            {

                Request request = new Request(licenceUserName, licenceKey);
                out.println(gson.toJson(request));

                String jsonResponse = in.readLine();
                updateToken(gson.fromJson(jsonResponse, Response.class));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                currentToken = new Response(licenceUserName, false, "Connection error", null);
            }
        }).start();
    }

    private void updateToken(Response newToken)
    {
        if (newToken != null && newToken.isLicenseValid())
        {
            currentToken = newToken;
            scheduleTokenRenewal(newToken.getExpiryTime());
        }
        else
        {
            currentToken = newToken;
        }
    }

    private void scheduleTokenRenewal(long expiryTime)
    {
        tokenRenewalTimer.cancel();
        tokenRenewalTimer = new Timer();
        tokenRenewalTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                requestLicenceToken();
            }
        }, expiryTime - System.currentTimeMillis());
    }

    public void stop()
    {
        tokenRenewalTimer.cancel();
        currentToken = null;
        licenceUserName = null;
        licenceKey = null;
        serverAddress = null;
        serverPort = 0;
    }
}
