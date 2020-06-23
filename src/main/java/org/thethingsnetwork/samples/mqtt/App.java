package org.thethingsnetwork.samples.mqtt;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

@Controller
@SpringBootApplication
public class App
{
    public static void main(String[] args)
    {
        String region = "eu";
        String appId = "co2_sensor_stenden";
        String accessKey = "ttn-account-v2.J5ws5KGhK9jVP5p56HfG1VyLka8PecrVTtIsam6MpWA"; // Top-Secret
        String apiUrl = "https://loradashboardapi.herokuapp.com/add_measurement";

        try
        {
            Client client = new Client(region, appId, accessKey);

            client.onMessage((String devId, DataMessage data) -> {

                try
                {
                    UplinkMessage message = (UplinkMessage) data;
                    int co2 = (int)message.getPayloadFields().get("co2");
                    double temperature = (double)message.getPayloadFields().get("temperature");
                    double humidity = (double)message.getPayloadFields().get("humidity");
                    int tvoc = (int)message.getPayloadFields().get("tvoc"); // hydrocarbons
                    String DateTime = java.time.LocalDateTime.now().toString();
                    JSONObject Obj = new JSONObject();
                    Obj.put("datetime", DateTime);
                    Obj.put("air_pressure", 0); // Node doesn't support air pressure
                    Obj.put("humidity", humidity);
                    Obj.put("temperature", temperature);
                    Obj.put("hydrocarbons", tvoc);
                    Obj.put("carbon_dioxide", co2);
                    int nodeId = Integer.parseInt(devId);
                    Obj.put("nodeID", nodeId);
                    String jsonMessage = Obj.toString();
                    System.out.println("Message json: " + (jsonMessage));

                    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");// GET POST PUT DELETE
                    conn.setRequestProperty("Content-Type", "application/json");
                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonMessage.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                        System.out.println("Message sent to API");
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

            client.onConnected((Connection _client) -> System.out.println("connected!"));

            client.start();

        } catch (URISyntaxException ex)
        {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        SpringApplication.run(App.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return "Server is running!";
    }
}