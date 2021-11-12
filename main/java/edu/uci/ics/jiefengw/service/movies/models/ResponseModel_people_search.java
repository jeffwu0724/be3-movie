package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.models.movieModel;
import java.util.ArrayList;

public class ResponseModel_people_search {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "people")
    ArrayList<updatePersonModel> peopleList= new ArrayList<updatePersonModel>();


    public ResponseModel_people_search( Integer resultCode, String message, ArrayList<updatePersonModel> peopleList) {
        this.resultCode = resultCode;
        this.message = message;
        this.peopleList = peopleList;
    }
    public ResponseModel_people_search() {}

    @JsonProperty("resultCode")
    public Integer getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("people")
    public ArrayList<updatePersonModel> getPeopleList(){ return peopleList; }
    @JsonProperty("people")
    public void setPeopleList(ArrayList<updatePersonModel> peopleList){
        this.peopleList = peopleList;
    }
}
