package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Tells Jackson to ignore all fields with value of NULL
public class getPersonModel {
    /*
     PERSON_ID (int, required)

     NAME (string, required)

     GENDER (string, optional)

     BIRTHDAY (string, optional)

     DEATHDAY (string, optional)

     BIOGRAPHY (string, optional)

     BIRTHPLACE (string, optional)

     POPULARITY (float, optional)

     PROFILE_PATH (string, optional)

     */

    @JsonProperty(value = "person_id", required = true)
    public Integer person_id;
    @JsonProperty(value = "name", required = true)
    public String name;
    @JsonProperty(value = "gender")
    public String gender;
    @JsonProperty(value = "birthday")
    public String birthday;
    @JsonProperty(value = "deathday")
    public String deathday;
    @JsonProperty(value = "biography")
    public String biography;
    @JsonProperty(value = "birthplace")
    public String birthplace;
    @JsonProperty(value = "popularity")
    public Float popularity;
    @JsonProperty(value = "profile_path")
    public String profile_path;

    public getPersonModel() {}
    @JsonCreator
    public getPersonModel(@JsonProperty(value = "person_id", required = true) Integer person_id,
                             @JsonProperty(value = "name", required = true) String name,
                             @JsonProperty(value = "gender") String gender,
                             @JsonProperty(value = "birthday") String birthday,
                             @JsonProperty(value = "deathday") String deathday,
                             @JsonProperty(value = "biography") String biography,
                             @JsonProperty(value = "birthplace") String birthplace,
                             @JsonProperty(value = "popularity") Float popularity,
                             @JsonProperty(value = "profile_path") String profile_path
    ) {
        // this.movieS = movieS;
        this.person_id = person_id;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
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

    @JsonProperty("gender")
    public String getGender(){ return gender; }
    @JsonProperty("gender")
    public void setGender(String gender){ this.gender = gender; }

    @JsonProperty("birthday")
    public String getBirthday(){ return birthday; }
    @JsonProperty("birthday")
    public void setBirthday(String birthday){ this.birthday = birthday; }

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
    public void setBirthplace(String birthplace){ this.birthplace = birthplace; }

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
