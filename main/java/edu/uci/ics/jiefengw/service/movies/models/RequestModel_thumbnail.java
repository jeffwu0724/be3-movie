package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_thumbnail {
    @JsonProperty(value = "movie_ids", required = true)
    private String[] movie_id;

    @JsonCreator
    public RequestModel_thumbnail(  @JsonProperty(value = "movie_ids", required = true) String[] movie_id) {
        this.movie_id = movie_id;
    }
    public RequestModel_thumbnail() {}

    @JsonProperty("movie_ids")
    public String[] getMovie_id() {
        return movie_id;
    }
}
