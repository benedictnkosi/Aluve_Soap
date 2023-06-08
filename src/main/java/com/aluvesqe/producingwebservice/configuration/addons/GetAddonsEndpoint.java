package com.aluvesqe.producingwebservice.configuration.addons;


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
public class GetAddonsEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetAddonsEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAddonsRequest")
    @ResponsePayload
    public GetAddonsResponse GetAddons(@RequestPayload GetAddonsRequest request) {
        String endPoint = "/api/json/addons";

        //call the rest service

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);

        System.out.println("Message: " + message);
        GetAddonsResponse getAddonsResponse = new GetAddonsResponse();
        JSONArray array = new JSONArray(message);

        for(int i=0; i < array.length(); i++)
        {
            JSONObject jsonObj = array.getJSONObject(i);
            ResultMessage resultMessage =  new ResultMessage();
            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            getAddonsResponse.setResultsMessage(resultMessage);

            Addon addon = new Addon();
            addon.setId(jsonObj.getInt("id"));
            addon.setName(jsonObj.getString("name"));
            addon.setStatus(jsonObj.getString("status"));
            addon.setPrice(jsonObj.getString("price"));
            getAddonsResponse.getAddon().add(addon);
            resultMessage.setCode(0);
        }

        return getAddonsResponse;
    }
}