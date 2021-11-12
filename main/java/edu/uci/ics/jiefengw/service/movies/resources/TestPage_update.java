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

@Path("update")
public class TestPage_update {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forUpdate(@Context HttpHeaders headers, String jsonText_update){
        // Set the path of the endpoint we want to communicate with
        IdmConfigs configs = MoviesService.getIdmConfigs();
        String servicePath = configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath();//"http://localhost:2942/activity";
        String endpointPath = "/privilege";

        // Declare models
        RequestModel_update requestModel = new RequestModel_update();
        RequestModel_privilege requestModel_privilege = new RequestModel_privilege();
        ResponseModel_update responseModel = null;

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        //gather the email info
        requestModel_privilege.setEmail(email);
        requestModel_privilege.setPlevel(3);        //registerd users

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
                .header("email", email)
                .header("session_id", session_id)
                .header("transaction_id", transaction_id);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel_privilege, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, ResponseModel_update.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

            //not sufficient privilege
            if(responseModel.getResultCode() != 140){
                //Case 218: Could not update movie.
                responseModel = new ResponseModel_update(218, "Could not update movie.");
                ServiceLogger.LOGGER.severe("Could not update movie.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            //sufficient privilege
            else{
                requestModel = mapper.readValue(jsonText_update, RequestModel_update.class);
                ServiceLogger.LOGGER.severe("requestModel movie_id: " + requestModel.getMovie_id());

                //TODO check whether we have this movie or not, yes, update, else 211
                try{

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
                        movieList.add(rs.getString("movie_id"));
                    }

                    //Case 312: Shopping cart item does not exist.
                    if(movieList.isEmpty()){
                        //Case 211: No movies found with search parameters.
                        responseModel = new ResponseModel_update(211, "No movies found with search parameters.");
                        ServiceLogger.LOGGER.severe("No movies found with search parameters.");
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }else{
                        //Todo update
                        // Construct the query
                        String director_id = "";
                        String select_query =  "Select distinct person.person_id  from person " +
                                    "LEFT JOIN movie on person.person_id = movie.director_id " +
                                    "where person.name = ? ";
                        // Create the prepared statement
                        PreparedStatement select_ps = MoviesService.getCon().prepareStatement(select_query);
                        select_ps.setString(1, requestModel.getDirector());
                        ServiceLogger.LOGGER.info("start selection...");
                        ResultSet select_rs = select_ps.executeQuery();
                        ServiceLogger.LOGGER.info("selection succeeded.");

                        while (select_rs.next()) {
                            director_id = select_rs.getString("person_id");
                        }


                        String query =  "UPDATE movie " +
                                "SET movie.movie_id = ?, movie.title = ?, " +
                                "movie.year = ?, movie.director_id = ?, " +
                                "movie.rating = ?, movie.num_votes = ?, " +
                                "movie.budget = ?, movie.revenue = ?, " +
                                "movie.overview = ?, movie.backdrop_path = ?, " +
                                "movie.poster_path = ?, movie.hidden = ? " +
                                "WHERE movie.movie_id = ?";

                        // Create the prepared statement
                        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

                        // Set the arguments
                        ps.setString(1, requestModel.getMovie_id());
                        ps.setString(2, requestModel.getTitle());
                        ps.setInt(3, requestModel.getYear());
                        ps.setString(4, director_id);
                        ps.setFloat(5, requestModel.getRating());
                        ps.setInt(6, requestModel.getNum_votes());
                        ps.setString(7, requestModel.getBudget());
                        ps.setString(8, requestModel.getRevenue());
                        ps.setString(9, requestModel.getOverview());
                        ps.setString(10, requestModel.getBackdrop_path());
                        ps.setString(11, requestModel.getPoster_path());
                        ps.setBoolean(12, requestModel.getHidden());
                        ps.setString(13, requestModel.getMovie_id());



                        // Save the query result to a ResultSet so records may be retrieved
                        ServiceLogger.LOGGER.info("Trying update: " + ps.toString());
                        // code = ps_user_status.executeUpdate();
                        // code = ps_privilege_level.executeUpdate();
                        ps.executeUpdate();
                        ServiceLogger.LOGGER.info("update succeeded.");
                        // ServiceLogger.LOGGER.info(query);
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                    //Case 218: Could not update movie.
                    responseModel = new ResponseModel_update(218, "Could not update movie.");
                    ServiceLogger.LOGGER.severe("Could not update movie.");
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }

            }
        } catch (IOException e) {
            //ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_update(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {

                resultCode = -2;
                responseModel = new ResponseModel_update(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.info("*****************************" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_update(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        //Case 217: Movie successfully updated.
        responseModel = new ResponseModel_update(217, "Movie successfully updated.");
        ServiceLogger.LOGGER.severe("Movie successfully updated.");
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
