package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.models.ResponseModel_search;

import java.util.ArrayList;

public class ResponseModel_browse {

    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movies")
    ArrayList<movieModel> movieList= new ArrayList<movieModel>();


    public ResponseModel_browse( Integer resultCode, String message, ArrayList<movieModel> movieList) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieList = movieList;
    }
    public ResponseModel_browse() {}


    @JsonProperty("resultCode")
    public Integer getResultCode(){
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage(){
        return message;
    }

    @JsonProperty("movies")
    public ArrayList<movieModel> getMovieList(){
        return movieList;
    }
    @JsonProperty("movies")
    public void setMovieList(ArrayList<movieModel> movieList){
        this.movieList = movieList;
    }
}
