package com.aluvesqe.producingwebservice.reservations;

import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class GetCleaningsForTodayEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetCleaningsForTodayEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCleaningsForTodayRequest")
    @ResponsePayload
    public GetCleaningsForTodayResponse getCleaningsForToday(@RequestPayload GetCleaningsForTodayRequest request) {

        String endPoint = "/api/json/outstandingcleanings/today";

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetCleaningsForTodayResponse getCleaningsForTodayResponse = new GetCleaningsForTodayResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            OutstandingCleaning outstandingCleaning =  new OutstandingCleaning();
            outstandingCleaning.setRoomName(jsonObj.getString("name"));
            outstandingCleaning.setReason(jsonObj.getString("reason"));

            getCleaningsForTodayResponse.getOutstandingCleaning().add(outstandingCleaning);
        }

        return getCleaningsForTodayResponse;
    }

}