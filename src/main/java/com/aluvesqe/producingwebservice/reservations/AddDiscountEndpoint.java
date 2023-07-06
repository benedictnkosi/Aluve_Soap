package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.AddDiscountRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.UpdateGuestIdNumberRequest;
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
public class AddDiscountEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public AddDiscountEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addDiscountRequest")
    @ResponsePayload
    public GetSimpleResponse addDiscount(@RequestPayload AddDiscountRequest request) {
        Assert.isTrue(request.getId() > 0, "The reservation id value must be greater than zero");
        Assert.isTrue(request.getAmount() > 0, "The amount value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/json/discount/add";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body json data
        JSONObject body =new JSONObject();
        body.put("id", String.valueOf(request.getId()));
        body.put("amount",request.getAmount());

        String message = restHelper.callRestWithJsonBody(endPoint ,"POST", headers, body.toString());
        JSONObject jsonObj = new JSONObject(message);

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        return getSimpleResponse;
    }









}