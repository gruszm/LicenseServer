package pl.gruszm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main
{
    public static void main(String[] args)
    {
        LicenseClientAPI clientAPI = new LicenseClientAPI();

        clientAPI.start("127.0.0.1", 2222);
        clientAPI.setLicence("Radek", generateKey("Radek"));

        Response response = clientAPI.getLicenceToken();
        if (response != null)
        {
            if (response.isLicenseValid())
            {
                System.out.println("Otrzymano token licencji. Ważny do: " + response.getExpired());
            }
            else
            {
                System.out.println("Nie udało się uzyskać tokenu licencji. Powód: " + response.getDescription());
            }
        }
        else
        {
            System.out.println("Nie otrzymano odpowiedzi od serwera.");
        }

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        clientAPI.stop();
        System.out.println("Klient został zatrzymany.");
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
}
