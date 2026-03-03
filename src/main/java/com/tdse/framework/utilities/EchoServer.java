package com.tdse.framework.utilities;

import java.net.*;
import java.io.*;

/**
 * EchoServer - Listens on port 35000 and echoes every message back to the client
 * prefixed with "Respuestas: ".
 *
 * Based on: "Introducción a esquemas de nombres, redes, clientes y servicios con Java"
 * Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería
 */
public class EchoServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Mensaje:" + inputLine);
            outputLine = "Respuestas: " + inputLine;
            out.println(outputLine);
            if (outputLine.equals("Respuestas: Bye."))
                break;
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
