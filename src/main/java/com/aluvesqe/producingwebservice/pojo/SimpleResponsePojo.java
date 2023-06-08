package com.aluvesqe.producingwebservice.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.guides.gs_producing_web_service.GetSimpleResponse;

public class SimpleResponsePojo {
    int result_code;
    String result_message;

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getResult_message() {
        return result_message;
    }

    public void setResult_message(String result_message) {
        this.result_message = result_message;
    }

    public GetSimpleResponse createSimpleResponse(String message){
        GetSimpleResponse response = new GetSimpleResponse();
        SimpleResponsePojo simpleResponsePojo = null;
        try {
            simpleResponsePojo = new ObjectMapper().readValue(message, SimpleResponsePojo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setCode(1);
        response.setMessage(simpleResponsePojo.getResult_message());
        return response;
    }
}
