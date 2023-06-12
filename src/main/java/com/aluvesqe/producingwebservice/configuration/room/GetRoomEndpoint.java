package com.aluvesqe.producingwebservice.configuration.room;


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
public class GetRoomEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetRoomEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getRoomRequest")
    @ResponsePayload
    public GetRoomResponse getRoomNumber(@RequestPayload GetRoomRequest request) {
        Assert.isTrue(request.getRoomId() > 0, "The room id value must be greater than zero");

        String endPoint = "/no_auth/rooms/"+request.getRoomId();

        //call the rest service
        System.out.println("URL is " + Properties.getURL());
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String message = restHelper.callRest(endPoint ,"GET", "no-auth");
        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetRoomResponse getRoomResponse = new GetRoomResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            ResultMessage resultMessage =  new ResultMessage();
            if(jsonObj.getInt("result_code") != 0){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            resultMessage.setCode(jsonObj.getInt("result_code"));
            getRoomResponse.setResultsMessage(resultMessage);
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
            getRoomResponse.setRoom(room);
        }

        return getRoomResponse;
    }

}