package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.models.movieModel;
import java.util.ArrayList;

public class ResponseModel_people_get {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "person")
    getPersonModel  personList;


    public ResponseModel_people_get( Integer resultCode, String message, getPersonModel personList) {
        this.resultCode = resultCode;
        this.message = message;
        this.personList = personList;
    }
    public ResponseModel_people_get() {}

    @JsonProperty("resultCode")
    public Integer getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("person")
    public getPersonModel getPersonList(){ return personList; }
    @JsonProperty("person")
    public void setPersonList(getPersonModel personList){
        this.personList = personList;
    }
}
