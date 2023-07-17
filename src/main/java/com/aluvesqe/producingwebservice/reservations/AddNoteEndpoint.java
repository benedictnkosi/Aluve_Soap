package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.AddNoteRequest;
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
public class AddNoteEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public AddNoteEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addNoteRequest")
    @ResponsePayload
    public GetSimpleResponse addNote(@RequestPayload AddNoteRequest request) {
        Assert.isTrue(request.getReservationId() > 0, "The reservation id value must be greater than zero");
        Assert.isTrue(request.getNote()  != null && request.getNote().length() > 0, "The note must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/json/note/add";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        JSONObject body =new JSONObject();
        body.put("id", request.getReservationId());
        body.put("note",request.getNote());

        String message = restHelper.callRestWithJsonBody(endPoint ,"POST", headers, body.toString());
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setId(String.valueOf(jsonObj.getInt("id")));

        return getSimpleResponse;
    }









}