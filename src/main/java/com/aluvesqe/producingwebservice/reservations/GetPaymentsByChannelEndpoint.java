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

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class GetPaymentsByChannelEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetPaymentsByChannelEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPaymentsByChannelRequest")
    @ResponsePayload
    public GetPaymentsByChannelResponse GetPaymentsByChannel(@RequestPayload GetPaymentsByChannelRequest request) {
        Assert.isTrue(request.getChannel()  != null && request.getChannel().length() > 0, "The ichannel can not be null");
        Assert.isTrue(request.getEndDate()  != null && request.getEndDate().length() > 0, "The end date can not be null");
        Assert.isTrue(request.getStartDate()  != null && request.getStartDate().length() > 0, "The start date can not be null");
        Assert.isTrue(request.getGroup()  != null && request.getGroup().length() > 0, "The group can not be null");

        String endPoint = "/api/json/payment/total/transactions";

        //call the rest service
        System.out.println("URL is " + Properties.getURL());
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);


        JSONObject body =new JSONObject();
        body.put("start_date", request.getStartDate());
        body.put("end_date", request.getEndDate());
        body.put("channel", request.getChannel());
        body.put("group", request.getGroup());

        body.put("soap_call", "true");


        String message = restHelper.callRestWithJsonBody(endPoint ,"POST", headers, body.toString());

        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetPaymentsByChannelResponse getPaymentsByChannelResponse = new GetPaymentsByChannelResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            if(request.getGroup().toLowerCase().contentEquals("true")){
                GroupedPayment groupedPayment = new GroupedPayment();
                groupedPayment.setAmount(jsonObj.getString("amount"));
                groupedPayment.setDate(jsonObj.getString("date"));
                getPaymentsByChannelResponse.getGroupedPayment().add(groupedPayment);
            }else{
                Payment payment = new Payment();
                payment.setId(jsonObj.getInt("id"));
                payment.setReservationId(jsonObj.getInt("reservation_id"));
                payment.setAmount(jsonObj.getDouble("amount"));
                payment.setChannel(jsonObj.getString("channel"));
                payment.setDate(jsonObj.getString("date"));
                payment.setDiscount(jsonObj.getString("discount"));
                payment.setReference(jsonObj.getString("reference"));
                getPaymentsByChannelResponse.getPayment().add(payment);
            }

        }
        return getPaymentsByChannelResponse;
    }

}