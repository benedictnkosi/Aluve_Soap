package com.aluvesqe.producingwebservice.reservations;


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
public class GetReservationEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetReservationEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getReservationRequest")
    @ResponsePayload
    public GetReservationResponse getReservation(@RequestPayload GetReservationRequest request) {
        Assert.isTrue(request.getReservationId() > 0, "The reservation id value must be greater than zero");

        String endPoint = "/no_auth/reservation/"+request.getReservationId();

        //call the rest service
        System.out.println("URL is " + Properties.getURL());
        RestHelper restHelper =  new RestHelper(Properties.getURL());

        String message = restHelper.callRest(endPoint ,"GET", "no-auth");
        System.out.println("Message: " + message);

        JSONArray array = new JSONArray(message);
        GetReservationResponse getReservationResponse = new GetReservationResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            ResultMessage resultMessage =  new ResultMessage();
            Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));


            Guest guest =  new Guest();
            Room room =  new Room();
            guest.setId(jsonObj.getInt("guest_id"));
            guest.setName(jsonObj.getString("guest_name"));
            guest.setPhoneNumber(jsonObj.getString("guest_phone_number"));

            room.setRoomId(jsonObj.getInt("room_id"));
            room.setRoomName(jsonObj.getString("room_name"));

            resultMessage.setCode(jsonObj.getInt("result_code"));
            getReservationResponse.setId(jsonObj.getInt("id"));
            getReservationResponse.setCheckIn(jsonObj.getString("check_in"));
            getReservationResponse.setCheckOut(jsonObj.getString("check_out"));
            getReservationResponse.setStatus(jsonObj.getInt("status"));
            getReservationResponse.setCheckInStatus(jsonObj.getString("check_in_status"));
            getReservationResponse.setCheckInTime(jsonObj.getString("check_in_time"));
            getReservationResponse.setGuest(guest);
            getReservationResponse.setRoom(room);
            if(jsonObj.isNull("checked_in_time")) {
                getReservationResponse.setCheckedInTime("null");
            }else{
                getReservationResponse.setCheckedInTime(jsonObj.getString("checked_in_time"));
            }
            getReservationResponse.setCheckOutTime((String) jsonObj.get("check_out_time"));
            getReservationResponse.setTotalPaid(jsonObj.getInt("total_paid"));

        }

        return getReservationResponse;
    }

}