package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel_privilege {
        @JsonProperty(value = "resultCode", required = true)
        private int resultCode;
        @JsonProperty(value = "message", required = true)
        private String message;
        @JsonProperty(value = "movieList")
        ArrayList<movieModel> movieList= new ArrayList<movieModel>();

        public ResponseModel_privilege(int resultCode, String message,  ArrayList<movieModel> movieList) {

            this.resultCode = resultCode;
            this.message = message;
            this.movieList = movieList;
        }
        public ResponseModel_privilege() { }

        @JsonProperty("resultCode")
        public int getResultCode() {
            return resultCode;
        }

        @JsonProperty("message")
        public String getMessage() {
            return message;
        }

        @JsonProperty("movieList")
        public ArrayList<movieModel> getMovieList(){
            return movieList;
        }
        @JsonProperty("movieList")
        public void setMovieList(ArrayList<movieModel> movieList){
            this.movieList = movieList;
        }
}
