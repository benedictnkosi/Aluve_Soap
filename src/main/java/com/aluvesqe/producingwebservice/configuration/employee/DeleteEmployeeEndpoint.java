package com.aluvesqe.producingwebservice.configuration.employee;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.DeleteEmployeeRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DeleteEmployeeEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public DeleteEmployeeEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteEmployeeRequest")
    @ResponsePayload
    public GetSimpleResponse deleteEmployee(@RequestPayload DeleteEmployeeRequest request) {
        Assert.isTrue(request.getId() > 0, "The id value must be greater than zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/admin_api/employee/delete/" + request.getId();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");

        String message = restHelper.callRest(endPoint ,"DELETE", cookie);
        System.out.println("message: " + message);
        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        JSONObject jsonObj;
        try{
            jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        }catch(Exception exception){
            System.out.println("exception: " + exception.getMessage());
            getSimpleResponse.setCode(0);
            getSimpleResponse.setMessage("Successfully deleted employee");
            return getSimpleResponse;
        }
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }









}