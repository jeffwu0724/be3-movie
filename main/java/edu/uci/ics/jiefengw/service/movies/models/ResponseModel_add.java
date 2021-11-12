package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel_add {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movie_id", required = true)
    private String movie_id;

    public ResponseModel_add( Integer resultCode, String message, String movie_id) {
        this.resultCode = resultCode;
        this.message = message;
        this.movie_id = movie_id;

    }
    public ResponseModel_add() {}


    @JsonProperty("resultCode")
    public Integer getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("movie_id")
    public String getMovie_id(){
        return movie_id;
    }
    @JsonProperty("movie_id")
    public void setMovie_id(String movie_id){
        this.movie_id = movie_id;
    }

}
