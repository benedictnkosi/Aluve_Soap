package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.UpdateReservationDatesRequest;
import io.spring.guides.gs_producing_web_service.UpdateReservationRoomRequest;
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
public class UpdateReservationDatesEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public UpdateReservationDatesEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateReservationDatesRequest")
    @ResponsePayload
    public GetSimpleResponse updateGuestIdNumber(@RequestPayload UpdateReservationDatesRequest request) {
        Assert.isTrue(request.getId() > 0, "The reservation id value must be greater than zero");
        Assert.isTrue(request.getCheckInDate()  != null && request.getCheckInDate().length() > 0, "The check-in value must not be null");
        Assert.isTrue(request.getCheckOutDate()  != null && request.getCheckOutDate().length() > 0, "The check-out value must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/json/reservations/update/dates";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body json data
        JSONObject body =new JSONObject();
        body.put("id", request.getId());
        body.put("check_in_date",request.getCheckInDate());
        body.put("check_out_date",request.getCheckOutDate());

        String message = restHelper.callRestWithJsonBody(endPoint ,"PUT", headers, body.toString());
        JSONObject jsonObj = new JSONObject(message);

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        return getSimpleResponse;
    }









}