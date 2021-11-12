package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_search {
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "year")
    private Integer year;
    @JsonProperty(value = "director")
    private String director;
    @JsonProperty(value = "genre")
    private String genre;
    @JsonProperty(value = "hidden")  //hidden movies are only viewable by plevel 4 or lower;  this data value should be excluded from results for plevel 5
    private boolean hidden;
    @JsonProperty(value = "limit")  //number of results displayed; 10 (default), 25, 50, or 100
    private Integer limit;
    @JsonProperty(value = "offset") //for result pagination; 0 (default) or positive multiple of the limit
    private Integer offset;
    @JsonProperty(value = "orderby")    //sorting parameter; "title" (default) or "rating" or “year”
    private String orderby;
    @JsonProperty(value = "direction")  //sorting direction; "asc" (default) or "desc"
    private String direction;

    @JsonCreator
    public RequestModel_search( @JsonProperty(value = "title") String title,
                                @JsonProperty(value = "year") Integer year,
                                @JsonProperty(value = "director") String director,
                                @JsonProperty(value = "genre") String genre,
                                @JsonProperty(value = "hidden") boolean hidden,
                                @JsonProperty(value = "limit") Integer limit,
                                @JsonProperty(value = "offset") Integer offset,
                                @JsonProperty(value = "orderby") String orderby,
                                @JsonProperty(value = "direction") String direction) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.hidden = hidden;
        this.limit = limit;
        this.offset= offset;
        this.orderby = orderby;
        this.direction= direction;
    }
    public RequestModel_search() {}

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("director")
    public String getDirector() {
        return director;
    }

    @JsonProperty("genre")
    public String getGenre() {
        return genre;
    }

    @JsonProperty("hidden")
    public boolean getHidden() {
        return hidden;
    }

    @JsonProperty("limit")
    public Integer getLimit() {
        return limit;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty("orderby")
    public String getOrderby() {
        return orderby;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }
}
