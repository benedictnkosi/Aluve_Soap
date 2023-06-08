package com.aluvesqe.producingwebservice.configuration.employee;


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
public class GetEmployeeEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetEmployeeEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEmployeeRequest")
    @ResponsePayload
    public GetEmployeeResponse GetEmployee(@RequestPayload GetEmployeeRequest request) {
        Assert.isTrue(request.getId() > 0, "The room id value must be greater than zero");

        String endPoint = "/api/json/employee/"+request.getId();

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);

        System.out.println("Message: " + message);

        JSONObject jsonObj = new JSONObject(message);

        ResultMessage resultMessage =  new ResultMessage();
        if(!jsonObj.isNull("result_message")){
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        }

        GetEmployeeResponse getEmployeeResponse = new GetEmployeeResponse();
        getEmployeeResponse.setResultsMessage(resultMessage);
        Employee employee = new Employee();
        employee.setId(jsonObj.getInt("id"));
        employee.setName(jsonObj.getString("name"));
        employee.setStatus(jsonObj.getString("status"));
        employee.setGender(jsonObj.getString("gender"));
        getEmployeeResponse.setEmployee(employee);

        return getEmployeeResponse;
    }

}