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

        System.out.println("NOTE: Type anything to stop the server");
        in.nextLine();

        server.stopServer();
    }
}
