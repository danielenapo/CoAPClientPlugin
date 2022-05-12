package com.example.coapplugin;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.DiscoveryResource;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

public class PostGetClient implements CoapManager{

    public static String post(String ip, String resource){
        String response= new String();
        CoapClient client;
        try{
            client=getClient(ip, resource);
        }catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            return response;
        }
        Request request = new Request(CoAP.Code.POST);
        //Set Request as Confirmable
        request.setConfirmable(true);
        try {
            CoapResponse coapResp = client.advanced(request);
            response= coapResp.getCode().text;
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
            response= "no confirmation recieved";
        }
        return response;
    }

    public static String get(String ip, String resource) {
        String response= new String();
        CoapClient client;
        try{
            client=getClient(ip, resource);
        }catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            return response;
        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String discover(String ip){
        String discoveryUri=".well-known/core";
        String response= new String();
        CoapClient client;
        try{
            client=getClient(ip, discoveryUri);
        }catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            return response;
        }
        try {
            Log.d("[DEB]", "Calling server");
            CoapResponse coapResponse = client.get();
            Log.d("[DEB]", "got a response");

            if (coapResponse != null) {
                response = coapResponse.getResponseText();
            } else {
                response = "No response received.";
            }

            if (coapResponse.getOptions().getContentFormat() == MediaTypeRegistry.APPLICATION_LINK_FORMAT) {
                response=parseDiscover(coapResponse.getResponseText());
            }
        } catch (ConnectorException | IOException e) {
            response = "Got an error: " + e;
            return response;
        }

        Log.d("[DEB]", response);
        client.shutdown();
        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String parseDiscover(String responseText){
        String returnString="";
        Set<WebLink> links = LinkFormat.parse(responseText);
        for(WebLink link : links){
            if(!link.getURI().equals("/.well-known/core")) {
                returnString += link.getURI() + "=";
                for (String key : link.getAttributes().getAttributeKeySet()) {
                    String value = link.getAttributes().getAttributeValues(key).get(0);
                    if (key.equals("if") || key.equals("title")) {
                        returnString += value + ",";
                    }
                }
                returnString += ";";
            }
        }
        return returnString.substring(0,returnString.length()-1);
    }

    public static CoapClient getClient(String ip, String resource) throws URISyntaxException{
        URI uri = null; // URI parameter of the request
        uri = new URI("coap://" + ip + ":5683/" + resource);
        Log.d("[DEB]",uri.toString());
        CoapClient client = new CoapClient(uri);
        return client;
    }

}
