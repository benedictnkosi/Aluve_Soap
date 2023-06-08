package com.aluvesqe.producingwebservice.configuration.addons;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.UpdateAddonRequest;
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
public class updateAddOnEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public updateAddOnEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateAddonRequest")
    @ResponsePayload
    public GetSimpleResponse updateAddOn(@RequestPayload UpdateAddonRequest request) {
        Assert.isTrue(request.getField() != null && request.getField().length() > 0, "The field must not be null");
        Assert.isTrue(request.getValue() != null && request.getValue().length() > 0, "The value must not be null");

        Assert.isTrue(request.getId() > 0, "The id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/addon/update";
        System.out.println("endpoint is : " + endPoint);

        Map<String, String> data =  new HashMap<>();
        data.put("id", String.valueOf(request.getId()));
        data.put("field",request.getField());
        data.put("value",request.getValue());
        data.put("soap_call","true");

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");

        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        String message = restHelper.postWithFormData(endPoint ,data, headers);

        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));

        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }
}