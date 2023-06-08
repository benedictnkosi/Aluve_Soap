package com.aluvesqe.producingwebservice.reservations;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.pojo.SimpleResponsePojo;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetGuestByPhoneNumberRequest;
import io.spring.guides.gs_producing_web_service.GetGuestByPhoneNumberResponse;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.ResultMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class GetGuestByPhoneNumberEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetGuestByPhoneNumberEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getGuestByPhoneNumberRequest")
    @ResponsePayload
    public GetGuestByPhoneNumberResponse GetGuestByPhoneNumber(@RequestPayload GetGuestByPhoneNumberRequest request) {
        Assert.isTrue(request.getPhoneNumber()  != null && request.getPhoneNumber().length() > 0, "The phone number must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/guests/"+request.getPhoneNumber();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        System.out.println("Cookie: " + cookie);


        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);

        JSONObject jsonObj = new JSONObject(message);
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetGuestByPhoneNumberResponse getGuestByPhoneNumberResponse = new GetGuestByPhoneNumberResponse();
        ResultMessage resultMessage =  new ResultMessage();
        resultMessage.setCode(jsonObj.getInt("result_code"));
        getGuestByPhoneNumberResponse.setResultsMessage(resultMessage);
        getGuestByPhoneNumberResponse.setId(jsonObj.getInt("id"));
        getGuestByPhoneNumberResponse.setName(jsonObj.getString("name"));
        getGuestByPhoneNumberResponse.setImageId(jsonObj.getString("image_id"));
        getGuestByPhoneNumberResponse.setPhoneNumber(jsonObj.getString("phone_number"));
        getGuestByPhoneNumberResponse.setEmail(jsonObj.getString("email"));
        getGuestByPhoneNumberResponse.setState(jsonObj.getString("state"));
        getGuestByPhoneNumberResponse.setComments(jsonObj.getString("comments"));
        getGuestByPhoneNumberResponse.setIdNumber(jsonObj.getString("id_number"));
        return getGuestByPhoneNumberResponse;
    }









}