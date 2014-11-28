/**
 * Created by matthewmcguire on 11/24/14.
 */

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private static SSLServerSocket sslServerSocket;
    private static SSLSocket sslSocket;

    public static void main(String[] args) {
        String error = "You must enter a valid port number as a " +
                "command line argument, please restart the server " +
                "with a valid port number";
        if (args.length == 1) {
            int port;
            if ((port = validPort(args[0])) != -1) {
                try {
                    SSLServerSocketFactory sslserversocketfactory =
                            (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                    sslServerSocket =
                            (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
                    while (true) {
                        sslSocket = (SSLSocket) sslServerSocket.accept();
                        new ClientThread(sslSocket).start();
                    }
                } catch (Exception e) {
                    Logger.getLogger(Server.class.getName()).log(Level.ALL, error, e);
                    System.exit(1);
                }
            } else {
                Logger.getLogger(Server.class.getName()).log(Level.ALL, error);
                System.exit(1);
            }
        } else {
            Logger.getLogger(Server.class.getName()).log(Level.ALL, error);
            System.exit(1);
        }
    }

    /**
     * Return a valid port as an int if the string passed in is valid as a port
     * number.
     *
     * @param port
     * @return
     */
    protected static int validPort(String port) {
        int validPort = -1;
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(port);
        if (m.matches()) {
            validPort = Integer.parseInt(port);
        }
        return validPort;
    }
}

class ClientThread extends Thread {
    private SSLSocket sslSocket;
    private SSLSession sslSession;
    private BufferedReader in = null;
    private PrintWriter out = null;

    /**
     * Constructor for ClientThread, taking in the client socket connection
     * so that the appropriate data may be derived and the server may
     * communicate with the client for the duration of the threads life.
     *
     * @param acceptedConnection
     */
    public ClientThread(SSLSocket acceptedConnection) {
        this.sslSocket = acceptedConnection;
        sslSession = acceptedConnection.getSession();
    }

    /**
     * Execute the program for each client that connects to the server.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(this.sslSocket
                    .getInputStream()));
            out = new PrintWriter(this.sslSocket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.ALL, "Error in reader or writer", ex);
        }

        String[] questions = {"User Name: ", "Full Name: ", "Address: ",
                "Phone Number: ", "Email Address: ", "Would you like to add more users (yes/no): "};

        out.println("Connected...");
        for (String q : questions) {
            out.println(q);
            while (in == null) {
                if (in != null) {
                    try {
                        PrintWriter toFile = new PrintWriter(new BufferedWriter(new FileWriter("users.txt", true)));
                        synchronized (toFile) {
                            toFile.println(q + " --> " + in.readLine());
                            toFile.close();
                        }
                    } catch (IOException ioe) {
                        Logger.getLogger(ClientThread.class.getName()).log(Level.ALL, "File was not able to be written to");
                    }
                    break;
                }
            }
        }

        System.out.println("Connection Successful...\n\t"
                + "Peer Host: " + sslSession.getPeerHost() + "\n\t"
                + "Cypher Suite: " + sslSession.getCipherSuite() + "\n\t"
                + "Protocol: " + sslSession.getProtocol() + "\n\t"
                + "Session ID: " + sslSession.getId() + "\n\t"
                + "The creation time of this session is: " + sslSession.getCreationTime() + "\n\t"
                + "The last accessed time of this session is: " + sslSession.getLastAccessedTime());
    }
}