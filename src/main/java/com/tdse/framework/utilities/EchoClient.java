package com.tdse.framework.utilities;

import java.io.*;
import java.net.*;

/**
 * EchoClient - Connects to the EchoServer and sends lines from standard input.
 *
 * Based on: "Introducción a esquemas de nombres, redes, clientes y servicios con Java"
 * Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería
 */
public class EchoClient {
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("127.0.0.1", 35000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host!");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: localhost.");
            System.exit(1);
        }

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        while ((userInput = stdin.readLine()) != null) {
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
        }

        out.close();
        in.close();
        stdin.close();
        echoSocket.close();
    }
}
