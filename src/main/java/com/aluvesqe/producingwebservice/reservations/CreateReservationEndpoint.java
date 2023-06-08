package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.CreateReservationRequest;
import io.spring.guides.gs_producing_web_service.CreateReservationResponse;
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
public class CreateReservationEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public CreateReservationEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createReservationRequest")
    @ResponsePayload
    public CreateReservationResponse CreateReservation(@RequestPayload CreateReservationRequest request) {
        Assert.isTrue(request.getCheckOutDate() != null && request.getCheckOutDate().length() > 0, "The check out date must not be null");
        Assert.isTrue(request.getCheckInDate()  != null && request.getCheckInDate().length() > 0, "The check in date must not be null");
        Assert.isTrue(request.getDate()  != null && request.getDate().length() > 0, "The date must not be null");
        Assert.isTrue(request.getEmail()  != null && request.getEmail().length() > 0, "The email must not be null");
        Assert.isTrue(request.getPhoneNumber()  != null && request.getPhoneNumber().length() > 0, "The phone number must not be null");
        Assert.isTrue(request.getSmoking()  != null && request.getSmoking().length() > 0, "The smoking must not be null");

        Assert.isTrue(request.getAdultGuests() > 0, "The adult guests value must be greater than zero");
        Assert.isTrue(request.getChildGuests() > -1, "The child guests value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        String endPoint = "/no_auth/reservations/create";
        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("room_ids", String.valueOf(request.getRoomIds()));
        data.put("name",request.getName());
        data.put("phone_number",String.valueOf(request.getPhoneNumber()));
        data.put("email",String.valueOf(request.getEmail()));
        data.put("adult_guests",String.valueOf(request.getAdultGuests()));
        data.put("child_guests",String.valueOf(request.getChildGuests()));
        data.put("check_in_date",String.valueOf(request.getCheckInDate()));
        data.put("check_out_date",request.getCheckOutDate());
        data.put("date",String.valueOf(request.getDate()));
        data.put("smoking",String.valueOf(request.getSmoking()));

        String message = restHelper.postWithFormData(endPoint ,data, headers);

        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        CreateReservationResponse createReservationResponse = new CreateReservationResponse();
        createReservationResponse.setResultCode(jsonObj.getInt("result_code"));
        createReservationResponse.setResultMessage(jsonObj.getString("result_message"));
        if(jsonObj.getInt("result_code") == 0){
            createReservationResponse.setReservationId(String.valueOf(jsonObj.getString("reservation_id")));
        }
        return createReservationResponse;

    }









}