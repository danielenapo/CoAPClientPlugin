package com.example.coapplugin;

import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PostGetClient {
    public static String request(String ip, String resource, String type){
        String response;

        Log.d("[DEB]", "starting function");
        URI uri = null; // URI parameter of the request
        try {
            uri = new URI("coap://" + ip + ":5683/" + resource);
        } catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            return response;
        }
        Log.d("[DEB]",uri.toString());
        CoapClient client = new CoapClient(uri);

        if(type=="get"){
            response=get(client);
        }
        else if (type=="post"){
            post(client);
            response=get(client);
        }
        else
            response="Error: wrong type -> "+ type;
        return response;
    }

    public static void post(CoapClient client){
        Request request = new Request(CoAP.Code.POST);
        //Set Request as Confirmable
        request.setConfirmable(true);
        try {
            CoapResponse coapResp = client.advanced(request);
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(CoapClient client) {
        String response= new String();
        try {
            Log.d("[DEB]", "Calling server");
            CoapResponse coapResponse = client.get();
            Log.d("[DEB]", "got a response");

            if (coapResponse != null) {
                response = coapResponse.getResponseText();
            } else {
                response = "No response received.";
            }

        } catch (ConnectorException | IOException e) {
            response = "Got an error: " + e;
            return response;
        }

        Log.d("[DEB]", response);
        client.shutdown();
        return response;
    }

}
