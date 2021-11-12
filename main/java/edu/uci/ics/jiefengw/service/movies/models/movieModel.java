package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL
public class movieModel {

    @JsonProperty(value = "movie_id", required = true)
    public String movie_id;
    @JsonProperty(value = "title", required = true)
    public String title;
    @JsonProperty(value = "year", required = true)
    public Integer year;
    @JsonProperty(value = "director", required = true)
    public String director;
    @JsonProperty(value = "rating", required = true)
    public float rating;
    @JsonProperty(value = "backdrop_path")
    public String backdrop_path;
    @JsonProperty(value = "poster_path")
    public String poster_path;
    @JsonProperty(value = "hidden")
    public Boolean hidden;

    public movieModel() {}
    @JsonCreator
    public movieModel(   //@JsonProperty(value = "movieS", required = true) movieModel movieS,
                         @JsonProperty(value = "movie_id", required = true) String movie_id,
                         @JsonProperty(value = "title", required = true) String title,
                         @JsonProperty(value = "year", required = true) Integer year,
                         @JsonProperty(value = "director", required = true) String director,
                         @JsonProperty(value = "rating", required = true) float rating,
                         @JsonProperty(value = "backdrop_path") String backdrop_path,
                         @JsonProperty(value = "poster_path") String poster_path,
                         @JsonProperty(value = "hidden") Boolean hidden) {
       // this.movieS = movieS;
        this.movie_id = movie_id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
        this.hidden = hidden;
    }


    /*
        "movie_id":"tt4154796",
        "title": "Avengers: Endgame",
        "year": 2019",
        "director": "Joe Russo",
        "rating": 8.6,
        "backdrop_path": "/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg",
        "poster_path": "/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
        "hidden": false

        @JsonProperty("movieS")
        public movieModel[] getmovieS() {
        return movieS;
    }

     */



    @JsonProperty("movie_id")
    public String getMovie_id(){ return movie_id; }
    @JsonProperty("movie_id")
    public void setMovie_id(String movie_id){ this.movie_id = movie_id; }

    @JsonProperty("title")
    public String getTitle(){ return title; }
    @JsonProperty("title")
    public void setTitle(String title){ this.title = title; }

    @JsonProperty("year")
    public Integer getYear(){ return year; }
    @JsonProperty("year")
    public void setYear(Integer year){ this.year = year; }

    @JsonProperty("director")
    public String getDirector(){ return director; }
    @JsonProperty("director")
    public void setDirector(String director){ this.director = director; }

    @JsonProperty("rating")
    public float getRating(){ return rating; }
    @JsonProperty("rating")
    public void setRating(float rating){ this.rating = rating; }

    @JsonProperty("backdrop_path")
    public String getBackdrop_path(){ return backdrop_path; }
    @JsonProperty("backdrop_path")
    public void setBackdrop_path(String backdrop_path){ this.backdrop_path = backdrop_path; }

    @JsonProperty("poster_path")
    public String getPoster_path(){ return poster_path; }
    @JsonProperty("poster_path")
    public void setPoster_path(String poster_path){ this.poster_path = poster_path; }

    @JsonProperty("hidden")
    public Boolean getHidden(){ return hidden; }
    @JsonProperty("hidden")
    public void setHidden(Boolean hidden){ this.hidden = hidden; }

    @Override
     public String toString() {
     return "movie{" +
             "movie_id='" + movie_id + '\'' +
             ", title='" + title + '\'' +
             ", year=" + year +
             ", director='" + director + '\'' +
             ", rating=" + rating +
             ", backdrop_path='" + backdrop_path + '\'' +
             ", poster_path='" + poster_path + '\'' +
             ", hidden=" + hidden +
             '}';
    }
}
