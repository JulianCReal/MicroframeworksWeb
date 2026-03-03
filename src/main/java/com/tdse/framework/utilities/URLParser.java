package com.tdse.framework.utilities;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URLParser - Demonstrates parsing a URL into its components using java.net.URL.
 *
 * Based on: "Introducción a esquemas de nombres, redes, clientes y servicios con Java"
 * Luis Daniel Benavides Navarro, Escuela Colombiana de Ingeniería
 */
public class URLParser {
    public static void main(String[] args) throws MalformedURLException {
        URL myurl = new URL("http://is.esculaing.edu.co:7654/respuestas/respuestas.txt?val=7&t=3#pubs");
        System.out.println("Protocol:  " + myurl.getProtocol());
        System.out.println("Host:      " + myurl.getHost());
        System.out.println("Authority: " + myurl.getAuthority());
        System.out.println("Port:      " + myurl.getPort());
        System.out.println("Path:      " + myurl.getPath());
        System.out.println("Query:     " + myurl.getQuery());
        System.out.println("File:      " + myurl.getFile());
        System.out.println("Ref:       " + myurl.getRef());
    }
}
