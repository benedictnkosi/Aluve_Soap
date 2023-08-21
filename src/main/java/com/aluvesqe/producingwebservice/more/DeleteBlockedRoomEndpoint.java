package com.aluvesqe.producingwebservice.more;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.DeleteBlockedRoomRequest;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.RemovePaymentRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DeleteBlockedRoomEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public DeleteBlockedRoomEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteBlockedRoomRequest")
    @ResponsePayload
    public GetSimpleResponse deleteBlockedRoom(@RequestPayload DeleteBlockedRoomRequest request) {
        Assert.isTrue( request.getId() > 0, "The id may not be zero");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/blockedroom/delete/"+request.getId();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"DELETE", cookie);

        System.out.println("message: " + message);
        JSONObject jsonObj;

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        try{
            jsonObj = new JSONObject(message);
        }catch(Exception exception){
            System.out.println("exception: " + exception.getMessage());
            getSimpleResponse.setCode(0);
            getSimpleResponse.setMessage("Successfully deleted blocked room");
            return getSimpleResponse;
        }
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }
}