package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_add {
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

    @JsonProperty(value = "num_votes")
    public Integer num_votes;
    @JsonProperty(value = "budget")
    public String budget;

    @JsonProperty(value = "revenue")
    public String revenue;
    @JsonProperty(value = "overview")
    public String overview;

    public RequestModel_add(   @JsonProperty(value = "title", required = true) String title,
                               @JsonProperty(value = "year", required = true) Integer year,
                               @JsonProperty(value = "director", required = true) String director,
                               @JsonProperty(value = "rating", required = true) float rating,
                               @JsonProperty(value = "backdrop_path") String backdrop_path,
                               @JsonProperty(value = "poster_path") String poster_path,
                               @JsonProperty(value = "hidden") Boolean hidden,
                               @JsonProperty(value = "num_votes") Integer num_votes,
                               @JsonProperty(value = "budget") String budget,
                               @JsonProperty(value = "revenue") String revenue,
                               @JsonProperty(value = "overview") String overview){
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.backdrop_path = backdrop_path;
        this.poster_path = poster_path;
        this.hidden = hidden;
        this.num_votes = num_votes;
        this.budget = budget;
        this.revenue = revenue;
        this.overview = overview;
    }
    public RequestModel_add(){}

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

    @JsonProperty("num_votes")
    public Integer getNum_votes(){ return num_votes; }
    @JsonProperty("num_votes")
    public void setNum_votes(Integer num_votes){ this.num_votes = num_votes; }

    @JsonProperty("budget")
    public String getBudget(){ return budget; }
    @JsonProperty("budget")
    public void setBudget(String budget){ this.budget = budget; }

    @JsonProperty("revenue")
    public String getRevenue(){ return revenue; }
    @JsonProperty("revenue")
    public void setRevenue(String revenue){ this.revenue = revenue; }

    @JsonProperty("overview")
    public String getOverview(){ return overview; }
    @JsonProperty("overview")
    public void setOverview(String overview){ this.overview = overview; }
}
