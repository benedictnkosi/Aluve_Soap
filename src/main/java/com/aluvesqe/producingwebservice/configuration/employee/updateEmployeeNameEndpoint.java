package com.aluvesqe.producingwebservice.configuration.employee;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.UpdateEmployeeNameRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class updateEmployeeNameEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public updateEmployeeNameEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateEmployeeNameRequest")
    @ResponsePayload
    public GetSimpleResponse updateEmployeeName(@RequestPayload UpdateEmployeeNameRequest request) {
        Assert.isTrue(request.getName() != null && request.getName().length() > 0, "The name must not be null");
        Assert.isTrue(request.getId() > 0, "The id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/employee/update/" + request.getId() + "/" + request.getName();
        System.out.println("endpoint is : " + endPoint);

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"PUT", cookie);

        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));

        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }
}