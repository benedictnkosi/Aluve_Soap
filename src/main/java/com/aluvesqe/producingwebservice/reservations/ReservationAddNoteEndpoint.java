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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Endpoint
public class ReservationAddNoteEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public ReservationAddNoteEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addNoteRequest")
    @ResponsePayload
    public GetSimpleResponse addNote(@RequestPayload AddNoteRequest request) {
        Assert.isTrue(request.getReservationId() > 0, "The reservation id value must be greater than zero");
        Assert.isTrue(request.getNote()  != null && request.getNote().length() > 0, "The note must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/note/add";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("id", String.valueOf(request.getReservationId()));
        data.put("note",request.getNote());

        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }









}