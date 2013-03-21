package org.elegance;

import java.io.*;
import java.net.*;

public class DServerClient {

    public DServerClient(String ServerName, String comm) {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
		BufferedReader stdIn = null;

        String fromServer;
        String fromClient;

        try {
            kkSocket = new Socket(ServerName, 7777);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));

			fromServer = in.readLine();
			System.out.println("Server : " + fromServer);

			fromClient = comm;
			out.println(fromClient);
			System.out.println("Client : " + fromClient);

			fromServer = in.readLine();
			System.out.println("Server : " + fromServer);

			out.close();
			in.close();
			stdIn.close();
			kkSocket.close();
        } catch (UnknownHostException ex) {
            System.err.println("Don't know about host: " + ex);
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Couldn't get I/O for the connection to host : " + ex);
        }
    }
}
