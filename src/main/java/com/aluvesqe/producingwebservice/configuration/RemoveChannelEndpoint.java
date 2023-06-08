package com.aluvesqe.producingwebservice.configuration;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.RemoveChannelRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import io.spring.guides.gs_producing_web_service.AddChannelRequest;

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class RemoveChannelEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public RemoveChannelEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeChannelRequest")
    @ResponsePayload
    public GetSimpleResponse RemoveChannel(@RequestPayload RemoveChannelRequest request) {
        Assert.isTrue(request.getChannelId() > 0, "The channel id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/ical/remove/" + request.getChannelId();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");

        String message = restHelper.callRest(endPoint ,"DELETE", cookie);
        System.out.println("message: " + message);
        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        JSONObject jsonObj;
        try{
            jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        }catch(Exception exception){
            System.out.println("exception: " + exception.getMessage());
            getSimpleResponse.setCode(0);
            getSimpleResponse.setMessage("Successfully removed ical");
            return getSimpleResponse;
        }
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }









}