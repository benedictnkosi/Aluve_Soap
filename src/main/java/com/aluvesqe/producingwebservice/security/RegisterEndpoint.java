package com.aluvesqe.producingwebservice.security;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.RegisterRequest;
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
public class RegisterEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public RegisterEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registerRequest")
    @ResponsePayload
    public GetSimpleResponse register(@RequestPayload RegisterRequest request) {
        String endPoint = "/register";
        Assert.isTrue(request.getUsername()  != null && request.getUsername().length() > 0, "The username must not be null");
        Assert.isTrue(request.getPassword()  != null && request.getPassword().length() > 0, "The password must not be null");
        Assert.isTrue(request.getConfirmPassword()  != null && request.getConfirmPassword().length() > 0, "The confirm password must not be null");

        RestHelper restHelper =  new RestHelper(Properties.getURL());

        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("public_api", "1");

        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("_username", String.valueOf(request.getUsername()));
        data.put("_role",request.getRole());
        data.put("_password",request.getPassword());
        data.put("_confirm_password",request.getConfirmPassword());

        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        if(message.contains("Successfully registered, Please sign in")){
            getSimpleResponse.setMessage("Successfully registered, Please sign in");
            getSimpleResponse.setCode(0);
        }else{
            getSimpleResponse.setMessage("Failed to register user, Please try again");
            getSimpleResponse.setCode(1);
        }



        return getSimpleResponse;
    }









}