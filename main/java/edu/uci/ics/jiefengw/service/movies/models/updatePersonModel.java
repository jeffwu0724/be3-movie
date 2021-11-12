package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL
public class updatePersonModel {

    @JsonProperty(value = "person_id", required = true)
    public Integer person_id;
    @JsonProperty(value = "name", required = true)
    public String name;
    @JsonProperty(value = "birthday")
    public String birthday;
    @JsonProperty(value = "popularity")
    public Float popularity;
    @JsonProperty(value = "profile_path")
    public String profile_path;

    public updatePersonModel() {}
    @JsonCreator
    public updatePersonModel(@JsonProperty(value = "person_id", required = true) Integer person_id,
                             @JsonProperty(value = "name", required = true) String name,
                             @JsonProperty(value = "birthday") String birthday,
                             @JsonProperty(value = "popularity") Float popularity,
                             @JsonProperty(value = "profile_path") String profile_path
    ) {
        // this.movieS = movieS;
        this.person_id = person_id;
        this.name = name;
        this.birthday = birthday;
        this.popularity = popularity;
        this.profile_path = profile_path;
    }

    @JsonProperty("person_id")
    public Integer getPerson_id(){ return person_id; }
    @JsonProperty("person_id")
    public void setPerson_id(Integer person_id){ this.person_id = person_id; }

    @JsonProperty("name")
    public String getName(){ return name; }
    @JsonProperty("name")
    public void setName(String name){ this.name = name; }


    @JsonProperty("birthday")
    public String getBirthday(){ return birthday; }
    @JsonProperty("birthday")
    public void setBirthday(String birthday){ this.birthday = birthday; }

    @JsonProperty("popularity")
    public Float getPopularity(){ return popularity; }
    @JsonProperty("popularity")
    public void setPopularity(Float popularity){ this.popularity = popularity; }

    @JsonProperty("profile_path")
    public String getProfile_path(){ return profile_path; }
    @JsonProperty("profile_path")
    public void setProfile_path(String profile_path){ this.profile_path = profile_path; }


    @Override
    public String toString() {
        return "people{ " +
                "person_id='" + person_id +
                ", name='" + name + '\'' +
                ", birthday='" + birthday  + '\'' +
                ", popularity='" + popularity  + '\'' +
                ", profile_path='" + profile_path  + '\'' +
                '}';
    }
}
