package com.aluvesqe.producingwebservice.configuration.room;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.SimpleIdName;
import io.spring.guides.gs_producing_web_service.CreateRoomRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class CreateRoomEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public CreateRoomEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createRoomRequest")
    @ResponsePayload
    public GetSimpleResponse createRoom(@RequestPayload CreateRoomRequest request) {
        Assert.isTrue(request.getRoomName() != null && request.getRoomName().length() > 0, "The room name must not be null");
        Assert.isTrue(request.getDescription()  != null && request.getDescription().length() > 0, "The description must not be null");
        Assert.isTrue(request.getKidsPolicy()  != null && request.getKidsPolicy().length() > 0, "The kids policy must not be null");
        Assert.isTrue(request.getAmenities()  != null && request.getAmenities().length() > 0, "The amenities must not be null");

        Assert.notNull(request.getBed(), "The bed must not be null");
        Assert.isTrue(request.getRoomPrice() > 0, "The room price value must be greater than zero");
        Assert.isTrue(request.getRoomSleeps() > 0, "The sleeps value must be greater than zero");
        Assert.isTrue(request.getRoomStatus() > 0, "The status value must be greater than zero");
        Assert.isTrue(request.getRoomSize() > 0, "The size value must be greater than zero");
        Assert.isTrue(request.getStairs() > 0, "The stairs value must be greater than zero");
        Assert.isTrue(request.getTv().getId() > 0, "The tv value must be greater than zero");


        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/createroom";

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
        data.put("room_name",request.getRoomName());

        data.put("room_price",String.valueOf(request.getRoomPrice()));
        data.put("room_sleeps",String.valueOf(request.getRoomSleeps()));
        data.put("room_status",String.valueOf(request.getRoomStatus()));
        data.put("linked_room",String.valueOf(request.getLinkedRoom()));
        data.put("room_size",String.valueOf(request.getRoomSize()));
        data.put("bed",request.getBed());
        data.put("stairs",String.valueOf(request.getStairs()));
        data.put("tv",String.valueOf(request.getTv().getId()));
        data.put("description",request.getDescription());
        data.put("kids_policy",request.getKidsPolicy());
        data.put("amenities",request.getAmenities());

        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setId(String.valueOf(jsonObj.getInt("room_id")));
        return getSimpleResponse;
    }









}