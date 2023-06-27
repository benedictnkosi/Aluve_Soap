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
public class GetReservationNotesEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    public GetReservationNotesEndpoint() {

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getReservationNotesRequest")
    @ResponsePayload
    public GetReservationNotesResponse GetReservationNotes(@RequestPayload GetReservationNotesRequest request) {
        Assert.isTrue(request.getReservationId() > 0, "The reservation id value must be greater than zero");

        String endPoint = "/api/json/notes/reservation/"+request.getReservationId();

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
        GetReservationNotesResponse getReservationNotesResponse = new GetReservationNotesResponse();
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);

            if(!jsonObj.isNull("result_message")){
                Assert.isTrue(jsonObj.getInt("result_code") == 0, jsonObj.getString("result_message"));
            }

            Note note = new Note();
            note.setId(jsonObj.getInt("id"));
            note.setDate(jsonObj.getString("date"));
            note.setNote(jsonObj.getString("note"));
            getReservationNotesResponse.getNote().add(note);
        }
        return getReservationNotesResponse;
    }

}