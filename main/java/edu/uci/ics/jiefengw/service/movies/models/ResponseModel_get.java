package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.models.updateMovieModel;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel_get {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movie")
    //ArrayList<updateMovieModel> updateMovieList= new ArrayList<updateMovieModel>();
    updateMovieModel updateMovie;

    public ResponseModel_get(int resultCode, String message, updateMovieModel updateMovie) {

        this.resultCode = resultCode;
        this.message = message;
        this.updateMovie = updateMovie;
    }
    public ResponseModel_get() { }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("movie") //updateMovieList
    public updateMovieModel getUpdateMovie(){
        return updateMovie;
    }
    @JsonProperty("movie")
    public void setUpdateMovie(updateMovieModel updateMovie){
        this.updateMovie = updateMovie;
    }

    /*
     @JsonProperty("movies")
    public ArrayList<movieModel> getMovieList(){
        return movieList;
    }
    @JsonProperty("movies")
    public void setMovieList(ArrayList<movieModel> movieList){
        this.movieList = movieList;
    }
     */
}
