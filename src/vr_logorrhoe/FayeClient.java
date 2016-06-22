//package com.voicerepublic.restream;

import java.text.MessageFormat;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;

import java.util.Map;
import java.util.HashMap;

public class FayeClient {

    public void attach() {
        // Handshake
        String url = "http://localhost:9292/faye";

        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        LongPollingTransport transport = new LongPollingTransport(null, httpClient);

        BayeuxClient client = new BayeuxClient(url, transport);
        client.handshake();
        client.waitFor(1000, BayeuxClient.State.CONNECTED);

        // Subscription to channels
        ClientSessionChannel channel = client.getChannel("/foo");
        channel.subscribe(new ClientSessionChannel.MessageListener() {
                public void onMessage(ClientSessionChannel channel, Message message) {
                    // Handle the message
                }
            });

        // Publishing to channels
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bar", "baz");
        channel.publish(data);

        // Disconnecting
        client.disconnect();
        client.waitFor(1000, BayeuxClient.State.DISCONNECTED);
    }

    public static void main(String[] args) {
        FayeClient fayeClient = new FayeClient();
        fayeClient.attach();
    }
}
