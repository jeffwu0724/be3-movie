package edu.uci.ics.jiefengw.service.movies.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jiefengw.service.movies.configs.IdmConfigs;
import edu.uci.ics.jiefengw.service.movies.logger.ServiceLogger;
import edu.uci.ics.jiefengw.service.movies.*;
import edu.uci.ics.jiefengw.service.movies.models.RequestModel_search;
import edu.uci.ics.jiefengw.service.movies.models.*;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Path("people")
public class TestPage_people_get {
    @Path("get/{person_id:.*}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forBrowse(@Context HttpHeaders headers,
                              @PathParam("person_id") String person_id
    ) {

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Declare models
        //RequestModel_search requestSearch = new RequestModel_search(); // "AdminLevel@uci.edu", 5
        // RequestModel_privilege requestPrivilege = new RequestModel_privilege();
        ResponseModel_people_get responseModel = new ResponseModel_people_get(0,"", null);

        ServiceLogger.LOGGER.info("here！ " );
        //MySQL
        String base_query = "SELECT DISTINCT gender.gender_name AS 'gender', person.* " + //, genre.name AS 'genre'
                "FROM person " +
                " LEFT JOIN gender ON gender.gender_id = person.gender_id " +
                "LEFT JOIN person_in_movie ON person.person_id = person_in_movie.person_id " +
                "LEFT JOIN movie ON person_in_movie.movie_id = movie.movie_id " +
                "WHERE TRUE AND person.person_id = ?";

        StringBuilder query_builder = new StringBuilder(base_query);



        try {
            String query_string = query_builder.toString();
            PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);

            int i = 1;
            if(person_id != null){
                query.setString(i, person_id );
            }

            ServiceLogger.LOGGER.info(query.toString());
            //ArrayList<getPersonModel> tempList = new ArrayList<getPersonModel>();
            ResultSet rs = query.executeQuery();
            // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
            // Use executeUpdate() for queries that CidHANGE the DB (returns # of rows modified as int)
            // Use execute() for general purpose queries
            ServiceLogger.LOGGER.info("Query succeeded.");

            try{
                while (rs.next()) {
                    //ServiceLogger.LOGGER.info("In the loop!!!!!!!!!!");
                    getPersonModel newPerson = new getPersonModel();
                    newPerson.setPerson_id(rs.getInt("person_id"));
                    newPerson.setName(rs.getString("name"));
                    newPerson.setGender(rs.getString("gender"));
                    newPerson.setBirthday(rs.getString("birthday"));
                    newPerson.setDeathday(rs.getString("deathday"));
                    newPerson.setBiography(rs.getString("biography"));
                    newPerson.setBirthplace(rs.getString("birthplace"));
                    newPerson.setPopularity(rs.getFloat("popularity"));
                    newPerson.setProfile_path(rs.getString(("profile_path")));
                    ServiceLogger.LOGGER.info("Retrieved User: (" + newPerson.getName() + " )");
                    //newMovie.setHidden(rs.getBoolean("hidden"));
                    //tempList.add(newPerson);
                    responseModel.setPersonList(newPerson);
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }

        ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
       /*
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            e.printStackTrace();
        }
        */



       // ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getPersonList().size());
        if( responseModel.getPersonList() != null)
        {
            responseModel = new ResponseModel_people_get(212, "Found people with search parameters.", responseModel.getPersonList());
            ServiceLogger.LOGGER.severe("Found people with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }else{
            responseModel = new ResponseModel_people_get(213, "No people found with search parameters.", null);
            ServiceLogger.LOGGER.severe("No people found with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        // Do work with data contained in response model
        // Return a response with same headers
        Response.ResponseBuilder builder;

        builder = Response.status(Response.Status.OK).entity(responseModel);

        // Pass along headers
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);

        // Return the response
        return builder.build();
    }
}
