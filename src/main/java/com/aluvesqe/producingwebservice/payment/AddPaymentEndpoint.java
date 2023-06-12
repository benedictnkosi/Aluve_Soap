package com.aluvesqe.producingwebservice.payment;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.AddPaymentRequest;
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
public class AddPaymentEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public AddPaymentEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addPaymentRequest")
    @ResponsePayload
    public GetSimpleResponse addPayment(@RequestPayload AddPaymentRequest request) {
        Assert.isTrue(request.getChannel() != null && request.getChannel().length() > 0, "The channel must not be null");
        Assert.isTrue(request.getReference()  != null && request.getReference().length() > 0, "The reference must not be null");

        Assert.isTrue(request.getId() > 0, "The ID value must be greater than zero");
        Assert.isTrue(request.getAmount() > 0, "The amount value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/payment/add";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("id", String.valueOf(request.getId()));
        data.put("amount",String.valueOf(request.getAmount()));
        data.put("channel",String.valueOf(request.getChannel()));
        data.put("reference",String.valueOf(request.getReference()));


        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));

        if(!jsonObj.isNull("result_message")){
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        }

        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setId(String.valueOf(jsonObj.getInt("payment_id")));
        return getSimpleResponse;
    }









}