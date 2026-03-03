package com.tdse.framework.utilities;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * URLReader - Reads headers and body of an HTTP response from a URL.
 *
 * Based on: "Introducción a esquemas de nombres, redes, clientes y servicios con Java"
 * Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería
 */
public class URLReader {

    public static void main(String[] args) throws Exception {

        String site = "http://www.google.com/";
        URL siteURL = new URL(site);

        URLConnection urlConnection = siteURL.openConnection();
        Map<String, List<String>> headers = urlConnection.getHeaderFields();
        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();

        for (Map.Entry<String, List<String>> entry : entrySet) {
            String headerName = entry.getKey();
            if (headerName != null) { System.out.print(headerName + ":"); }
            List<String> headerValues = entry.getValue();
            for (String value : headerValues) {
                System.out.print(value);
            }
            System.out.println("");
        }

        System.out.println("-------message-body------");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}
