package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_rating {

    @JsonProperty(value = "movie_id", required = true)
    public String movie_id;
    @JsonProperty(value = "rating", required = true)
    public float rating;


    public RequestModel_rating(@JsonProperty(value = "movie_id", required = true) String movie_id,
                               @JsonProperty(value = "rating", required = true) float rating
                               ){
        this.movie_id = movie_id;
        this.rating = rating;
    }

    @JsonProperty("movie_id")
    public String getMovie_id(){ return movie_id; }
    @JsonProperty("movie_id")
    public void setMovie_id(String movie_id){ this.movie_id = movie_id; }

    @JsonProperty("rating")
    public float getRating(){ return rating; }
    @JsonProperty("rating")
    public void setRating(float rating){ this.rating = rating; }
}
