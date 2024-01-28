package pl.gruszm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        LicenseClientAPI clientAPI = new LicenseClientAPI();

        clientAPI.start("127.0.0.1", 2222);
        clientAPI.setLicence("Admin", generateKey("Admin"));

        clientAPI.getLicenceToken();

        Scanner in = new Scanner(System.in);

        in.nextLine();

        in.close();

        clientAPI.stop();
        System.out.println("Client stopped");
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
