package com.aluvesqe.producingwebservice.more;

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
public class GetCleaningsForRoomEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetCleaningsForRoomEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCleaningsForRoomRequest")
    @ResponsePayload
    public GetCleaningsForRoomResponse getCleaningsForRoom(@RequestPayload GetCleaningsForRoomRequest request) {
        Assert.isTrue(request.getRoomId() > 0, "The id value must be greater than zero");

        String endPoint = "/api/json/cleanings/" + request.getRoomId() ;

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetCleaningsForRoomResponse getCleaningsForRoomResponse = new GetCleaningsForRoomResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            Cleaning cleaning = new Cleaning();
            Reservation reservation = new Reservation();
            Employee employee = new Employee();

            reservation.setId(jsonObj.getJSONObject("reservation").getInt("id"));
            employee.setId(jsonObj.getJSONObject("cleaner").getInt("id"));
            employee.setName(jsonObj.getJSONObject("cleaner").getString("name"));

            cleaning.setId(jsonObj.getInt("id"));
            cleaning.setCleaner(employee);
            cleaning.setDate(jsonObj.getString("date"));
            cleaning.setReservation(reservation);


            getCleaningsForRoomResponse.getCleaning().add(cleaning);
        }

        return getCleaningsForRoomResponse;
    }

}