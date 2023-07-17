package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.DeleteEmployeeRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.RemoveReservationAddOnRequest;
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
public class removeReservationAddOnEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public removeReservationAddOnEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeReservationAddOnRequest")
    @ResponsePayload
    public GetSimpleResponse removeReservationAddOn(@RequestPayload RemoveReservationAddOnRequest request) {
        Assert.isTrue(request.getReservationAddonId() > 0, "The id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/json/reservation_addon/delete";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");

        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        JSONObject body =new JSONObject();
        body.put("add_on_id", request.getReservationAddonId());

        String message = restHelper.callRestWithJsonBody(endPoint ,"REMOVE", headers, body.toString());
        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        JSONObject jsonObj;
        try{
            jsonObj = new JSONObject(message);
        }catch(Exception exception){
            getSimpleResponse.setCode(0);
            getSimpleResponse.setMessage("Successfully deleted add-on");
            return getSimpleResponse;
        }
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }









}