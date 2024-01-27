package pl.gruszm;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        int port; // for example: 2222

        try
        {
            System.out.print("Please enter the port: ");
            port = in.nextInt();

            if (port <= 0 || port > 65535)
            {
                throw new InputMismatchException();
            }
        }
        catch (InputMismatchException e)
        {
            System.out.println("The port must be a positive number not greater than 65535!");
            in.close();
            return;
        }

        // clear the buffer
        in.nextLine();

        LicenseServer server = new LicenseServer(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        System.out.println("NOTE: Type break to stop the server");
        System.out.println("NOTE: or print to show all licenses");

        while (true)
        {
            String s = in.nextLine();

            if (s.equals("break"))
            {
                break;
            }
            else if (s.equals("print"))
            {
                server.getLicenses().values().forEach(client ->
                {
                    System.out.println("NUL: " + client.getLicenceUserName());
                    System.out.println("CW: " + client.getValidationTime() + " seconds");
                    System.out.println();
                });
            }
        }

        server.stopServer();
        in.close();
    }
}
