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
public class GetReservationsEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetReservationsEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getReservationsRequest")
    @ResponsePayload
    public GetReservationsResponse getReservations(@RequestPayload GetReservationsRequest request) {
        Assert.isTrue(request.getPeriod() != null && request.getPeriod().length() > 0, "The gender must not be null");

        String endPoint = "/api/reservations_json/"+request.getPeriod();

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
        GetReservationsResponse getReservationsResponse = new GetReservationsResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            Guest guest =  new Guest();
            Room room =  new Room();
            Reservation reservation = new Reservation();
            guest.setId(jsonObj.getJSONObject("guest").getInt("id"));
            guest.setName(jsonObj.getJSONObject("guest").getString("name"));
            guest.setPhoneNumber(jsonObj.getJSONObject("guest").getString("phone_number"));

            room.setRoomId(jsonObj.getJSONObject("room").getInt("id"));
            room.setRoomName(jsonObj.getJSONObject("room").getString("name"));

            reservation.setId(jsonObj.getInt("id"));
            reservation.setCheckIn(jsonObj.getString("check_in"));
            reservation.setCheckOut(jsonObj.getString("check_out"));
            reservation.setStatus(jsonObj.getJSONObject("status").getInt("id"));
            reservation.setCheckInStatus(jsonObj.getString("check_in_status"));
            reservation.setCheckInTime(jsonObj.getString("check_in_time"));
            reservation.setGuest(guest);
            reservation.setRoom(room);
            if(jsonObj.isNull("checked_in_time")) {
                reservation.setCheckedInTime("null");
            }else{
                reservation.setCheckedInTime(jsonObj.getString("checked_in_time"));
            }
            reservation.setCheckOutTime((String) jsonObj.get("check_out_time"));
            getReservationsResponse.getReservation().add(reservation);
        }

        return getReservationsResponse;
    }

}