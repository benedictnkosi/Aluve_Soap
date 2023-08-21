package com.aluvesqe.producingwebservice.more;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.BlockRoomRequest;
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
public class BlockRoomEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public BlockRoomEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "blockRoomRequest")
    @ResponsePayload
    public GetSimpleResponse blockRoom(@RequestPayload BlockRoomRequest request) {
        Assert.isTrue(request.getNote() != null && request.getNote().length() > 0, "The note must not be null");
        Assert.isTrue(request.getStartDate() != null && request.getStartDate().length() > 0, "The start date must not be null");
        Assert.isTrue(request.getEndDate() != null && request.getEndDate().length() > 0, "The end date must not be null");
        Assert.isTrue(request.getRoomId() > 0, "The room id must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/json/blockroom";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        JSONObject body =new JSONObject();
        body.put("room_id", String.valueOf(request.getRoomId()));
        body.put("start_date",String.valueOf(request.getStartDate()));
        body.put("end_date",String.valueOf(request.getEndDate()));
        body.put("note",String.valueOf(request.getNote()));

        String message = restHelper.callRestWithJsonBody(endPoint ,"POST", headers, body.toString());
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