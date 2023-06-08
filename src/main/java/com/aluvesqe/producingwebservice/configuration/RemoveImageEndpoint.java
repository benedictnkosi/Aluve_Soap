package com.aluvesqe.producingwebservice.configuration;


import com.aluvesqe.producingwebservice.Properties;
import com.aluvesqe.producingwebservice.utils.RestHelper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;
import io.spring.guides.gs_producing_web_service.MarkImageDefaultRequest;
import io.spring.guides.gs_producing_web_service.RemoveImageRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class RemoveImageEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public RemoveImageEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "removeImageRequest")
    @ResponsePayload
    public GetSimpleResponse removeImage(@RequestPayload RemoveImageRequest request) {
        Assert.isTrue(request.getImageName() != null && request.getImageName().length() > 0, "The image name must not be null");

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String endPoint = "/api/configuration/removeimage/" + request.getImageName();

        //Login
        RestHelper restHelper =  new RestHelper(Properties.getURL());
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"DELETE", cookie);

        System.out.println("message: " + message);
        JSONObject jsonObj = new JSONObject(message.replace("[","").replace("]",""));
        Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

        GetSimpleResponse getSimpleResponse = new GetSimpleResponse();
        getSimpleResponse.setCode(jsonObj.getInt("result_code"));
        getSimpleResponse.setMessage(jsonObj.getString("result_message"));
        return getSimpleResponse;
    }
}