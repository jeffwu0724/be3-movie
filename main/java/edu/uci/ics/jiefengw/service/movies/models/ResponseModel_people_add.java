package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel_people_add {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    public ResponseModel_people_add ( Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;

    }
    public ResponseModel_people_add () {}


    @JsonProperty("resultCode")
    public Integer getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }
}
