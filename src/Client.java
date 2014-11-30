/**
 * Created by matthewmcguire on 11/24/14.
 */

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static BufferedReader reader = null;//Read user input
    private static PrintStream outClient = null;//Send user input to server
    private static BufferedReader inClient = null;//Read response from server

    public static void main(String[] args) {
        String error = "Please ensure the ip/dns address entered is correct, as" +
                " well as the port number and restart the client.";

        if (args.length == 2) {
            try {
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(args[0], Integer.parseInt(args[1]));
                sslsocket.setKeepAlive(true);
                outClient = new PrintStream(sslsocket.getOutputStream(), true);
                inClient = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                reader = new BufferedReader(new InputStreamReader(System.in));


                String fromServer, fromUser;
                while ((fromServer = inClient.readLine()) != null) {
                    System.out.println(fromServer);

                    fromUser = reader.readLine();
                    if (fromServer != null) {
                        outClient.println(fromUser);
                        if ((fromServer.equals("Would you like to add more users (yes/no): "))
                                && (!fromUser.equals("yes"))) {
                            break;
                        }
                    }
                }

               /*
               User has decided to end the program
                */
                outClient.close();
                inClient.close();
                sslsocket.close();
            } catch (IOException ioe) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, error);
                System.exit(1);
            }
        } else {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, error);
            System.exit(1);
        }
    }
}
