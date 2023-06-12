package com.aluvesqe.producingwebservice.configuration.room;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
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
public class AddChannelEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public AddChannelEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addChannelRequest")
    @ResponsePayload
    public GetSimpleResponse AddChannel(@RequestPayload AddChannelRequest request) {
        Assert.isTrue(request.getUrl() != null && request.getUrl().length() > 0, "The url must not be null");
        Assert.isTrue(request.getRoomId() > 0, "The room id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/ical/add";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("room_id", String.valueOf(request.getRoomId()));
        data.put("url",request.getUrl());

        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setId(String.valueOf(jsonObj.getInt("id")));
        return getSimpleResponse;
    }









}