package pl.gruszm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        int port; // for example: 2222
        String username;

        try
        {
            System.out.print("Please enter the port: ");
            port = in.nextInt();

            if (port <= 0 || port > 65535)
            {
                throw new InputMismatchException();
            }

            // clear the buffer
            in.nextLine();

            System.out.println("Please enter the username (md5 will be generated from it)");
            username = in.nextLine();
        }
        catch (InputMismatchException e)
        {
            System.out.println("The port must be a positive number not greater than 65535!");
            in.close();
            return;
        }

        LicenseClientAPI clientAPI = new LicenseClientAPI();

        clientAPI.start("127.0.0.1", port);
        clientAPI.setLicence(username, generateKey(username));

        clientAPI.getLicenceToken();

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
