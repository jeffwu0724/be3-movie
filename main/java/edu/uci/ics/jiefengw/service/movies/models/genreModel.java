package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL
public class genreModel {

    @JsonProperty(value = "genre_id", required = true)
    public Integer genre_id;
    @JsonProperty(value = "name", required = true)
    public String name;

    public genreModel() {}
    @JsonCreator
    public genreModel(@JsonProperty(value = "genre_id", required = true) Integer genre_id,
                      @JsonProperty(value = "name", required = true) String name) {
        // this.movieS = movieS;
        this.genre_id = genre_id;
        this.name = name;
    }

    @JsonProperty("genre_id")
    public Integer getGenre_id(){ return genre_id; }
    @JsonProperty("genre_id")
    public void setGenre_id(Integer genre_id){ this.genre_id = genre_id; }

    @JsonProperty("name")
    public String getName(){ return name; }
    @JsonProperty("name")
    public void setName(String name){ this.name = name; }

    @Override
    public String toString() {
        return "genres{ " +
                "genre_id='" + genre_id +
                ", name='" + name + '\'' +
                '}';
    }
}
