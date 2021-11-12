package edu.uci.ics.jiefengw.service.movies.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

@Path("rating")
public class TestPage_rating {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forRating(@Context HttpHeaders headers, String jsonText_rating){
        RequestModel_rating requestModel;
        ResponseModel_rating responseModel = new ResponseModel_rating(0, "");
        ObjectMapper mapper = new ObjectMapper();

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        try {
            requestModel = mapper.readValue(jsonText_rating, RequestModel_rating.class);


            try {
                //get rating, num_votes by movie_id
                float oldRating = 0;
                int num_votes = 0;
                ArrayList<String> movieList = new ArrayList<String>();
                String base_query = "SELECT DISTINCT movie.* " + //, genre.name AS 'genre'
                        "FROM movie " +
                        "WHERE movie.movie_id = ? ";
                // Create the prepared statement
                PreparedStatement base_ps = MoviesService.getCon().prepareStatement(base_query);

                base_ps.setString(1, requestModel.getMovie_id());
                // base_ps.setInt(2, requestModel.getQuantity());

                ServiceLogger.LOGGER.info("Trying selection: " + base_ps.toString());
                ResultSet rs = base_ps.executeQuery();
                ServiceLogger.LOGGER.info("selection succeeded.");

                while (rs.next()) {
                    oldRating = rs.getFloat("rating");
                    num_votes = rs.getInt("num_votes");
                    movieList.add(rs.getString("movie_id"));
                }

                if(movieList.isEmpty()){
                    //Case 211: No movies found with search parameters.
                    responseModel = new ResponseModel_rating(211, "No movies found with search parameters.");
                    ServiceLogger.LOGGER.severe("No movies found with search parameters.");
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }else{
                    //calulate new rating

                    float sum = oldRating * num_votes;
                    float newRating = (sum + requestModel.getRating()) / (num_votes + 1);
                    ServiceLogger.LOGGER.severe("old rating: " + oldRating);
                    ServiceLogger.LOGGER.severe("new rating: " + newRating);
                    ServiceLogger.LOGGER.severe("num_votes: " + num_votes);

                    //update rating and num_votes
                    try{
                        String rating_query =  "UPDATE movie " +
                                "SET movie.rating = ?, movie.num_votes = ? " +
                                "WHERE movie.movie_id = ?";

                        // Create the prepared statement
                        PreparedStatement rating_ps = MoviesService.getCon().prepareStatement(rating_query);

                        // Set the arguments
                        rating_ps.setFloat(1, newRating);
                        rating_ps.setInt(2, num_votes + 1);
                        rating_ps.setString(3, requestModel.getMovie_id());

                        // Save the query result to a ResultSet so records may be retrieved
                        ServiceLogger.LOGGER.info("Trying update: " + rating_ps.toString());
                        // code = ps_user_status.executeUpdate();
                        // code = ps_privilege_level.executeUpdate();
                        rating_ps.executeUpdate();
                        ServiceLogger.LOGGER.info("update succeeded.");
                        // ServiceLogger.LOGGER.info(query);

                    }catch(SQLException e){
                        e.printStackTrace();
                        //Case 251: Could not update rating
                        responseModel = new ResponseModel_rating(251, "Could not update rating.");
                        ServiceLogger.LOGGER.severe("Could not update rating.");
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }
                }



                //update rating and num_votes
            }catch(SQLException e){
                e.printStackTrace();
                //Case 251: Could not update rating
                responseModel = new ResponseModel_rating(251, "Could not update rating.");
                ServiceLogger.LOGGER.severe("Could not update rating.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }


        } catch (IOException e) {
            //ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_rating(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {

                resultCode = -2;
                responseModel = new ResponseModel_rating(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.info("*****************************" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_rating(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        //Case 250: Rating successfully updated.
        responseModel = new ResponseModel_rating(250, "Rating successfully updated.");
        ServiceLogger.LOGGER.severe("Rating successfully updated.");
        //return Response.status(Response.Status.OK).entity(responseModel).build();

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
