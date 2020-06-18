package org.thethingsnetwork.samples.mqtt;

import org.json.JSONObject;
import org.thethingsnetwork.data.common.Connection;
import org.thethingsnetwork.data.common.messages.ActivationMessage;
import org.thethingsnetwork.data.common.messages.DataMessage;
import org.thethingsnetwork.data.common.messages.UplinkMessage;
import org.thethingsnetwork.data.mqtt.Client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class App
{

    public static void main(String[] args)
    {
        String region = "eu";
        String appId = "co2_sensor_stenden";
        String accessKey = "ttn-account-v2.J5ws5KGhK9jVP5p56HfG1VyLka8PecrVTtIsam6MpWA";
        String apiUrl = "https://loradashboardapi.herokuapp.com/add_measurement";

        try
        {
            Client client = new Client(region, appId, accessKey);

            class Response {

                private String message;

                public Response(String _message) {
                    message = _message;
                }
            }

//            client.onMessage(null, "co2", (String _devId, DataMessage _data) -> {
//                try {
//                    RawMessage message = (RawMessage) _data;
//                    // Toggle the LED
////                    DownlinkMessage response = new DownlinkMessage(0, new Response(!message.asBoolean()));
//                    DownlinkMessage response = new DownlinkMessage(0, new Response(message.asString()));
//
//                    /**
//                     * If you don't have an encoder payload function:
//                     * client.send(_devId, new Response(0, message.asBoolean() ? new byte[]{0x00} : new byte[]{0x01}));
//                     */
//                    System.out.println("Sending: " + _data.toString());
//                    client.send(_devId, response);
//                } catch (Exception ex) {
//                    System.out.println("Response failed: " + ex.getMessage());
//                }
//            });

            client.onMessage((String devId, DataMessage data) -> {

                try
                {
                    UplinkMessage message = (UplinkMessage) data;
                    String jsonMessage = new JSONObject(message.getPayloadFields()).toString();
                    System.out.println("Message json: " + (jsonMessage));

                    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");// GET POST PUT DELETE
                    conn.setRequestProperty("Content-Type", "application/json");
                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonMessage.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                        System.out.println("Message sent to DB");
                    }catch (Exception out) {
                        System.out.println("Something went wrong with sending the message!");
                    }
                    System.out.println(conn.getResponseCode()); // 200 - 299 (200)


                }catch (Exception ex) {
                    System.out.println("Cry in the bathroom: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            client.onActivation((String _devId, ActivationMessage _data) -> System.out.println("Activation: " + _devId + ", data: " + _data.getDevAddr()));

            client.onError((Throwable _error) -> System.err.println("error: " + _error.getMessage()));

            client.onConnected((Connection _client) -> System.out.println("connected !"));

            client.start();

        } catch (URISyntaxException ex)
        {
            ex.printStackTrace();
            System.out.println("");
            System.out.println(ex.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

}