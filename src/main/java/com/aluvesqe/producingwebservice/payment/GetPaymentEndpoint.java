package com.aluvesqe.producingwebservice.payment;


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
public class GetPaymentEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetPaymentEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPaymentRequest")
    @ResponsePayload
    public GetPaymentResponse getPayment(@RequestPayload GetPaymentRequest request) {
        Assert.isTrue(request.getId() > 0, "The id value must be greater than zero");

        String endPoint = "/api/json/payment/"+request.getId();

        //call the rest service
        System.out.println("URL is " + Properties.getURL());
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //call the rest service
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);

        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        GetPaymentResponse getPaymentResponse = new GetPaymentResponse();

        if(!jsonObj.isNull("result_message")){
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        }


        getPaymentResponse.setId(jsonObj.getInt("id"));
        getPaymentResponse.setAmount(jsonObj.getInt("amount"));
        getPaymentResponse.setChannel(jsonObj.getString("channel"));
        getPaymentResponse.setDate(jsonObj.getString("date"));
        getPaymentResponse.setDiscount(jsonObj.getBoolean("discount"));
        getPaymentResponse.setReference(jsonObj.getString("reference"));

        return getPaymentResponse;
    }

}