package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_browse {
    /*
    limit (int, optional): number of results displayed; 10 (default), 25, 50, or 100

offset (int, optional): for result pagination; 0 (default) or positive multiple of the limit

orderby (string, optional): sorting parameter; “title” (default), “year”, “rating”

direction (string, optional): sorting direction; "asc" (default) or "desc"

     */
    @JsonProperty(value = "limit")
    private Integer limit;
    @JsonProperty(value = "offset")
    private Integer offset;
    @JsonProperty(value = "orderby")
    private String orderby;
    @JsonProperty(value = "direction")
    private String direction;

    @JsonCreator
    public RequestModel_browse(  @JsonProperty(value = "limit")  Integer limit,
                                 @JsonProperty(value = "offset") Integer offset,
                                 @JsonProperty(value = "orderby") String orderby,
                                 @JsonProperty(value = "direction") String direction) {

        this.limit = limit;
        this.offset= offset;
        this.orderby = orderby;
        this.direction= direction;
    }
    public RequestModel_browse() { }

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
