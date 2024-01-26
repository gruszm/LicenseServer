package pl.gruszm;

import com.google.gson.Gson;

import java.io.*;
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
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             PrintWriter writer = new PrintWriter(output, true))
        {
            String line = reader.readLine();
            Request request = gson.fromJson(line, Request.class);

            Response response = handleRequest(request);
            writer.println(gson.toJson(response));
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

        String generatedKey = generateKey(request.getLicenceUserName());

        if (!generatedKey.equals(request.getLicenceKey()))
        {
            return new Response(request.getLicenceUserName(), false, "Invalid license key", null);
        }

        Date expiryDate = calculateExpiryDate(licenseInfo.getValidationTime());

        return new Response(request.getLicenceUserName(), true, null, expiryDate);
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