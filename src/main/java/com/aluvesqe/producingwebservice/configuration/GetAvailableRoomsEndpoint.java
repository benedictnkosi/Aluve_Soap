package com.aluvesqe.producingwebservice.configuration;


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
public class GetAvailableRoomsEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetAvailableRoomsEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAvailableRoomsRequest")
    @ResponsePayload
    public GetRoomsResponse getAvailableRooms(@RequestPayload GetAvailableRoomsRequest request) {
        Assert.isTrue(request.getCheckInDate() != null && request.getCheckInDate().length() > 0, "The check in date must not be null");
        Assert.isTrue(request.getCheckOutDate() != null && request.getCheckOutDate().length() > 0, "The check out date must not be null");
        Assert.isTrue(request.getPropertyGuid() != null && request.getPropertyGuid().length() > 0, "The property guid must not be null");
        Assert.isTrue(request.getKids() > -1, "The kids value must be greater than zero");

        String endPoint = "/no_auth/availableroomsjson/"+request.getCheckInDate()+"/"+request.getCheckOutDate()+"/"+request.getPropertyGuid()+"/" + request.getKids();

        //call the rest service
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        //call the rest service
        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);

       // System.out.println("Message: " + message);
        GetRoomsResponse getAllRoomsResponse = new GetRoomsResponse();
        JSONArray array = new JSONArray(message);
        for(int i=0; i < array.length(); i++)
        {
            JSONObject jsonObj = array.getJSONObject(i);
            ResultMessage resultMessage =  new ResultMessage();
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));

            Room room = new Room();
            room.setRoomId(jsonObj.getInt("id"));
            room.setRoomName(jsonObj.getString("name"));
            Price price = new Price();
            price.setCurrency("ZAR");
            price.setAmount(Double.parseDouble(jsonObj.getString("price")));
            room.setRoomPrice(price);
            room.setRoomStatus(jsonObj.getInt("status"));
            room.setRoomSleeps(jsonObj.getInt("sleeps"));
            room.setDescription(jsonObj.getString("description"));
            room.setBed(jsonObj.getString("beds"));
            room.setStairs(jsonObj.getInt("stairs"));
            room.setLinkedRoom(jsonObj.getInt("linked_room"));
            room.setRoomSize(jsonObj.getInt("room_size"));
            SimpleIdName simpleIdName = new SimpleIdName();
            simpleIdName.setName(jsonObj.getString("tv_name"));
            simpleIdName.setId(jsonObj.getInt("tv"));
            room.setTv(simpleIdName);
            room.setExportLink(jsonObj.getString("export_link"));
            getAllRoomsResponse.getRoom().add(room);
            resultMessage.setCode(0);
            getAllRoomsResponse.setResultsMessage(resultMessage);
        }



        return getAllRoomsResponse;
    }
}