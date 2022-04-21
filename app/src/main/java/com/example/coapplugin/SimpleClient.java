package com.example.coapplugin;

import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;;
import org.eclipse.californium.elements.exception.ConnectorException;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class SimpleClient {

    private static final SimpleClient pluginInstance=new SimpleClient();
    public static SimpleClient getInstance(){
        Log.d("[DEB]", "getting instance");
        return pluginInstance;
    }

    private String response;

    public String getResponse(String ip, String resource) {
        URI uri = null; // URI parameter of the request
        try {
            uri = new URI("coap://" + ip + ":5683/" + resource);
        } catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            System.exit(-1);
        }
        Log.d("[DEB]",uri.toString());
        CoapClient client = new CoapClient(uri);

        try {
            Log.d("[DEB]", "Calling server");
            CoapResponse coapResponse = client.get();
            Log.d("[DEB]","got a response");

            if (coapResponse != null) {
                response= coapResponse.getResponseText();
            } else {
                response= "No response received.";
            }

        } catch (ConnectorException | IOException e) {
            response= "Got an error: " + e;
        }

        Log.d("[DEB]",response);
        client.shutdown();
        return response;
    }
}