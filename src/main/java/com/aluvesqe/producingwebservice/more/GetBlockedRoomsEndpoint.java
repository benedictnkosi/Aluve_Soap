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
public class GetBlockedRoomsEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetBlockedRoomsEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBlockedRoomsRequest")
    @ResponsePayload
    public GetBlockedRoomsResponse GetBlockedRooms(@RequestPayload GetBlockedRoomsRequest request) {
        String endPoint = "/api/json/blockedroom/get";

        //call the rest service
        System.out.println("URL is " + Properties.getURL());
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String username = request.getAuthentication().getUsername();
        String password = request.getAuthentication().getPassword();

        String cookie = restHelper.login(username, password);
        Assert.notNull(cookie, "Failed to authenticate user");
        String message = restHelper.callRest(endPoint ,"GET", cookie);
        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetBlockedRoomsResponse getBlockedRoomsResponse = new GetBlockedRoomsResponse();

        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            Room room =  new Room();
            BlockedRoom blockedRoom = new BlockedRoom();


            room.setRoomId(jsonObj.getJSONObject("room").getInt("id"));
            room.setRoomName(jsonObj.getJSONObject("room").getString("name"));
            room.setDescription(jsonObj.getJSONObject("room").getString("description"));

            blockedRoom.setId(jsonObj.getInt("id"));
            blockedRoom.setRoom(room);
            blockedRoom.setCreatedDate(jsonObj.getString("created_date"));
            blockedRoom.setFromDate(jsonObj.getString("from_date"));
            blockedRoom.setToDate(jsonObj.getString("to_date"));
            blockedRoom.setNote(jsonObj.getString("comment"));

            getBlockedRoomsResponse.getBlockedRoom().add(blockedRoom);
        }

        return getBlockedRoomsResponse;
    }

}