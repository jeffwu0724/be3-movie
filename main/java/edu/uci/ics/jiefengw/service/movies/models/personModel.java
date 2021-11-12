package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL
public class personModel {

    @JsonProperty(value = "person_id", required = true)
    public Integer person_id;
    @JsonProperty(value = "name", required = true)
    public String name;

    public personModel() {}
    @JsonCreator
    public personModel(@JsonProperty(value = "person_id", required = true) Integer person_id,
                      @JsonProperty(value = "name", required = true) String name) {
        // this.movieS = movieS;
        this.person_id = person_id;
        this.name = name;
    }

    @JsonProperty("person_id")
    public Integer getPerson_id(){ return person_id; }
    @JsonProperty("person_id")
    public void setPerson_id(Integer person_id){ this.person_id = person_id; }

    @JsonProperty("name")
    public String getName(){ return name; }
    @JsonProperty("name")
    public void setName(String name){ this.name = name; }

    @Override
    public String toString() {
        return "people{ " +
                "person_id='" + person_id +
                ", name='" + name + '\'' +
                '}';
    }
}
