package org.thethingsnetwork.samples.mqtt;

import org.thethingsnetwork.data.common.Connection;
import org.thethingsnetwork.data.mqtt.Client;

import java.net.URISyntaxException;

public class App
{

    public static void main(String[] args)
    {
        String region = "eu";
        String appId = "co2_sensor_stenden";
        String accessKey = "ttn-account-v2.J5ws5KGhK9jVP5p56HfG1VyLka8PecrVTtIsam6MpWA";

        try
        {
            Client client = new Client(region, appId, accessKey);
            client.onError((Throwable _error) -> System.err.println("error: " + _error.getMessage()));

            client.onConnected((Connection _client) -> System.out.println("connected!"));

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