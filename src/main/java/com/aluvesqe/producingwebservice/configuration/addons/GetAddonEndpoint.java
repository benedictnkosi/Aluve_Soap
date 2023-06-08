package com.aluvesqe.producingwebservice.configuration.addons;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class GetAddonEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetAddonEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAddonRequest")
    @ResponsePayload
    public GetAddonResponse GetAddon(@RequestPayload GetAddonRequest request) {
        Assert.isTrue(request.getId() > 0, "The id value must be greater than zero");

        String endPoint = "/api/addon/"+request.getId();

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);

        System.out.println("Message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));

        ResultMessage resultMessage =  new ResultMessage();
        if(!jsonObj.isNull("result_message")){
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
        }

        GetAddonResponse getAddonResponse = new GetAddonResponse();
        getAddonResponse.setResultsMessage(resultMessage);
        Addon addon = new Addon();
        addon.setId(jsonObj.getInt("id"));
        addon.setName(jsonObj.getString("name"));
        addon.setStatus(jsonObj.getString("status"));
        addon.setPrice(jsonObj.getString("price"));
        getAddonResponse.setAddon(addon);

        return getAddonResponse;
    }

}