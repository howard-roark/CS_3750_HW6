/**
 * Created by matthewmcguire on 11/24/14.
 */

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

    private static BufferedReader reader = null;//Read user input
    private static PrintStream outClient = null;//Send user input to server
    private static BufferedReader inClient = null;//Read response from server
    private static String toSend;
    private static String response = "";
    private static boolean closed = false;

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

                //Start new thread to listen for response from server without clogging main thread
                new Thread(new Client()).start();

                /*
                Infinite loop to respond to the server
                 */
                while (true) {
                    toSend = reader.readLine();
                    if (toSend.length() != 0) {
                        outClient.println(toSend);
                        if ((response.equals("Would you like to add more users (yes/no): ")) &&
                                (!toSend.equals("yes"))) {
                            break;
                        }
                    }
                }

               /*
               User has decided to end the program
                */
                closed = true;
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

    @Override
    public void run() {
        /*
        If the connection is not closed continue to listen for output from the server.
         */
        while (!closed) {
            try {
                response = inClient.readLine();
                if (response != null && response.length() != 0) {
                    System.out.println(response);
                    outClient.print(reader.readLine());
                }
            } catch (IOException ioe) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Error reading response from server: " + ioe);
                System.exit(1);
            }
        }
    }
}
