package pl.gruszm;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ClientHandler extends Thread
{
    private Socket socket;
    private Map<String, LicenseInfo> licenses;
    private Gson gson;

    public ClientHandler(Socket socket, Map<String, LicenseInfo> licenses, Gson gson)
    {
        this.socket = socket;
        this.licenses = licenses;
        this.gson = gson;
    }

    @Override
    public void run()
    {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            String line = reader.readLine();
            Request request = gson.fromJson(line, Request.class);

            System.out.println("Received a request from: " + request.getLicenceUserName());

            Response response = handleRequest(request);

            if (response != null)
            {
                writer.println(gson.toJson(response));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Response handleRequest(Request request)
    {
        LicenseInfo licenseInfo = licenses.get(request.getLicenceUserName());

        if (licenseInfo == null)
        {
            return new Response(request.getLicenceUserName(), false, "No license found for user", null);
        }

        if (request.getLicenceKey().equals("STOP") && licenseInfo != null)
        {
            licenseInfo.setUsed(false);
            return null;
        }

        String generatedKey = generateKey(request.getLicenceUserName());

        if (!generatedKey.equals(request.getLicenceKey()))
        {
            return new Response(request.getLicenceUserName(), false, "Invalid license key", null);
        }

        if (licenseInfo.getValidationTime() == 0)
        {
            licenseInfo.setUsed(true);
            Date expiryDate = calculateExpiryDate(licenseInfo.getValidationTime() + 3600L * 24L * 365L); // add 100 years
            licenseInfo.setExpiryTime(expiryDate);

            return new Response(request.getLicenceUserName(), true, null, expiryDate);
        }
        if (!licenseInfo.isUsed() || (licenseInfo.getExpiryTime() != null && new Date().after(licenseInfo.getExpiryTime())))
        {
            licenseInfo.setUsed(true);
            Date expiryDate = calculateExpiryDate(licenseInfo.getValidationTime());
            licenseInfo.setExpiryTime(expiryDate);

            return new Response(request.getLicenceUserName(), true, null, expiryDate);
        }
        else
        {
            return new Response(request.getLicenceUserName(), false, "Licence is already in use", null);
        }
    }

    public static String generateKey(String username)
    {
        MessageDigest md;

        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

        byte messageDigest[] = md.digest(username.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : messageDigest)
        {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }

    private Date calculateExpiryDate(long validationTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, (int) validationTime);
        return calendar.getTime();
    }
}