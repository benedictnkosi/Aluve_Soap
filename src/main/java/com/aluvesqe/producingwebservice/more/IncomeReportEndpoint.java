package com.aluvesqe.producingwebservice.more;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetIncomeRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class IncomeReportEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public IncomeReportEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getIncomeRequest")
    @ResponsePayload
    public GetSimpleResponse getIncome(@RequestPayload GetIncomeRequest request) {
        Assert.isTrue(request.getStartDate()  != null && request.getStartDate().length() > 0, "The start date must not be null");
        Assert.isTrue(request.getEndDate()  != null && request.getEndDate().length() > 0, "The end date must not be null");
        Assert.isTrue(request.getChannel()  != null && request.getChannel().length() > 0, "The channel must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/payment/total/cash/"+request.getStartDate()+"/"+request.getEndDate()+"/" + request.getChannel();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);

        JSONObject jsonObj = new JSONObject(message);
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }









}