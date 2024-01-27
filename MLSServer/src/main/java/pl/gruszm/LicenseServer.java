package pl.gruszm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LicenseServer implements Runnable
{
    private int port;
    private Map<String, LicenseInfo> licenses;
    private Gson gson;
    private ServerSocket serverSocket;
    private ScheduledExecutorService licenseExpiryService;

    public LicenseServer(int port)
    {
        this.port = port;
        licenses = new ConcurrentHashMap<>();
        gson = new Gson();
        loadLicenses();
        licenseExpiryService = Executors.newScheduledThreadPool(1);
        scheduleLicenseExpiryCheck();
    }

    private void scheduleLicenseExpiryCheck()
    {
        licenseExpiryService.scheduleAtFixedRate(() ->
        {
            Date now = new Date();

            licenses.values().forEach(license ->
            {
                if (license.getExpiryTime() != null && now.after(license.getExpiryTime()) && license.isUsed())
                {
                    license.setUsed(false);
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);

            while (true)
            {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, licenses, gson).start();
            }
        }
        catch (SocketException e)
        {
            System.out.println("Server stopped");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadLicenses()
    {
        try (Reader reader = new FileReader("licenses.json"))
        {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            JsonArray licensesArray = jsonObject.getAsJsonArray("payload");

            for (JsonElement elem : licensesArray)
            {
                LicenseInfo license = new Gson().fromJson(elem, LicenseInfo.class);
                licenses.put(license.getLicenceUserName(), license);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void stopServer()
    {
        licenseExpiryService.shutdown();

        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Map<String, LicenseInfo> getLicenses()
    {
        return licenses;
    }
}