package com.aluvesqe.producingwebservice.configuration.employee;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.CreateEmployeeRequest;
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
public class CreateEmployeeEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public CreateEmployeeEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createEmployeeRequest")
    @ResponsePayload
    public GetSimpleResponse CreateEmployee(@RequestPayload CreateEmployeeRequest request) {
        Assert.isTrue(request.getGender() != null && request.getGender().length() > 0, "The gender must not be null");
        Assert.isTrue(request.getName() != null && request.getName().length() > 0, "The name must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/createemployee";

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        //header
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        //body form data
        Map<String, String> data =  new HashMap<>();
        data.put("name", String.valueOf(request.getName()));
        data.put("gender",request.getGender());

        String message = restHelper.postWithFormData(endPoint ,data, headers);
        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        getSimpleResponse.setId(String.valueOf(jsonObj.getInt("employee_id")));
        return getSimpleResponse;
    }









}