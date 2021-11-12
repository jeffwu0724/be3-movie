package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_people_update extends updatePersonModel{
    @JsonProperty(value = "gender_id")
    public Integer gender_id;
    @JsonProperty(value = "deathday")
    public String deathday;
    @JsonProperty(value = "biography")
    public String biography;
    @JsonProperty(value = "birthplace")
    public String birthplace;

    public RequestModel_people_update() {}
    @JsonCreator
    public RequestModel_people_update(@JsonProperty(value = "person_id", required = true) Integer person_id,
                             @JsonProperty(value = "name", required = true) String name,
                             @JsonProperty(value = "birthday") String birthday,
                             @JsonProperty(value = "popularity") Float popularity,
                             @JsonProperty(value = "profile_path") String profile_path,
                             @JsonProperty(value = "gender_id") Integer gender_id,
                             @JsonProperty(value = "deathday") String deathday,
                             @JsonProperty(value = "biography") String biography,
                             @JsonProperty(value = "birthplace") String birthplace
    ) {
        // this.movieS = movieS;
        super(person_id,name,birthday,popularity,profile_path);
        this.gender_id = gender_id;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
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

    /*
     this.gender_id = gender_id;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
     */
    @JsonProperty("gender_id")
    public Integer getGender_id(){ return gender_id; }
    @JsonProperty("gender_id")
    public void setGender_id(Integer gender_id){ this.gender_id = gender_id; }

    @JsonProperty("deathday")
    public String getDeathday(){ return deathday; }
    @JsonProperty("deathday")
    public void setDeathday(String deathday){ this.deathday = deathday; }

    @JsonProperty("biography")
    public String getBiography(){ return biography; }
    @JsonProperty("biography")
    public void setBiography(String biography){ this.biography = biography; }

    @JsonProperty("birthplace")
    public String getBirthplace(){ return birthplace; }
    @JsonProperty("birthplace")
    public void setBirthplace(String profile_pabirthplace){ this.birthplace = birthplace; }
}
