package com.aluvesqe.producingwebservice;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final  class Properties {
    private Properties() {
        // No need to instantiate the class, we can hide its constructor
    }

    public static final String BASE_URL = "http://localhost";
    public static final String ETE_BASE_URL = "https://ete.hotelrunner.co.za";
    public static final String STAGING_BASE_URL = "https://staging.hotelrunner.co.za";

    public static String getURL(){
        try {
            String ipAddress = Inet4Address.getLocalHost().getHostAddress();
            System.out.println("my ip address is " + ipAddress );
            switch (ipAddress) {
                case "23.254.227.49":
                    return ETE_BASE_URL;
                case "23.254.227.42":
                    return STAGING_BASE_URL;
                default:
                    return BASE_URL;
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
