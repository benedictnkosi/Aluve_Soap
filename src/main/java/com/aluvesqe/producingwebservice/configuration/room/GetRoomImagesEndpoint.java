package com.aluvesqe.producingwebservice.configuration.room;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.spring.guides.gs_producing_web_service.*;
import io.spring.guides.gs_producing_web_service.Image;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Endpoint
public class GetRoomImagesEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetRoomImagesEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRoomImagesRequest")
    @ResponsePayload
    public GetRoomImagesResponse getRoomImages(@RequestPayload GetRoomImagesRequest request) {
        Assert.isTrue(request.getRoomId() > 0, "The room id value must be greater than zero");

        String endPoint = "/api/configuration/room/images/"+request.getRoomId();

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //call the rest service
        System.out.println("URL is " + Properties.getURL());

        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);
        JSONArray array = new JSONArray();
        try{
            array = new JSONArray(message);
        }catch(JSONException exception){
            Assert.notEmpty(array.toList(), "Failed to get room images");
        }
        
        GetRoomImagesResponse getRoomImagesResponse = new GetRoomImagesResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            ResultMessage resultMessage =  new ResultMessage();
            resultMessage.setCode(0);
            getRoomImagesResponse.setResultsMessage(resultMessage);
            Image image = new Image();
            image.setName(jsonObj.getString("name"));
            image.setSize(jsonObj.getString("size"));
            image.setStatus(jsonObj.getString("status"));
            getRoomImagesResponse.getImage().add(image);
        }

        return getRoomImagesResponse;
    }

}