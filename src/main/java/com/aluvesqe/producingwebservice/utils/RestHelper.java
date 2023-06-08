package com.aluvesqe.producingwebservice.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RestHelper {
    String baseUrl;

    public RestHelper(String url) {
        this.baseUrl = url;
    }


    public String callRest(String endPoint, String callType, String cookie) {
        URL url;
        HttpURLConnection con;
        try {

            //check if user logged in
            if (cookie == null) {
                return "{\"result_message\":\"Failed to login\",\"result_code\":1}";
            }

            url = new URL(baseUrl + endPoint);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(callType);
            con.setRequestProperty("Cookie", cookie);
            con.setDoInput(true);

            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();
            return content.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String login(String username, String password) {
        try {

            // Set header
            Map<String, String> headers = new HashMap<>();
            System.out.println("base url is " + baseUrl);
            HttpPostMultipart multipart = new HttpPostMultipart(baseUrl + "/login", "utf-8", headers);
            // Add form field
            multipart.addFormField("_username", username);
            multipart.addFormField("_password", password);
            // Add file

            //System.out.println(response);
            String cookie = multipart.sendLoginRequest();
            //go to admin to set session values
            this.callRest("/admin/", "GET", cookie);
            return cookie;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String postWithFormData(String endPoint, Map<String, String> data, Map<String, String> headers) {
        try {
            String cookie = headers.get("Cookie");

            //check if user logged in
            if (cookie == null && headers.get("public_api") == null) {
                return "{\"result_message\":\"Failed to login\",\"result_code\":1}";
            }

            HttpPostMultipart multipart = new HttpPostMultipart(baseUrl + endPoint, "utf-8", headers);
            // Add form field
            for (Map.Entry<String, String> entry : data.entrySet()) {
                multipart.addFormField(entry.getKey(), entry.getValue());
            }

            //System.out.println(response);
            return multipart.sendPostRequest();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
