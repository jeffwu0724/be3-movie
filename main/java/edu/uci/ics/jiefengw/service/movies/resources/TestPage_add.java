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

@Path("add")
public class TestPage_add {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forAdd(@Context HttpHeaders headers, String jsonText_add){
        // Set the path of the endpoint we want to communicate with
        IdmConfigs configs = MoviesService.getIdmConfigs();
        String servicePath = configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath();//"http://localhost:2942/activity";
        String endpointPath = "/privilege";

        // Declare models
        RequestModel_add requestModel = new RequestModel_add();
        RequestModel_privilege requestModel_privilege = new RequestModel_privilege();
        ResponseModel_add responseModel = new ResponseModel_add(0,"","");

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
            responseModel = mapper.readValue(jsonText, ResponseModel_add.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

            //not sufficient privilege
            if(responseModel.getResultCode() != 140){
                //Case 215: Case 215: Could not add movie.
                responseModel = new ResponseModel_add(215, "Case 215: Could not add movie.",responseModel.getMovie_id());
                ServiceLogger.LOGGER.severe("Case 215: Could not add movie.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            //sufficient privilege
            else{
                requestModel = mapper.readValue(jsonText_add, RequestModel_add.class);
                ServiceLogger.LOGGER.severe("requestModel title: " + requestModel.getTitle());


                try{

                    ArrayList<String> movieList = new ArrayList<String>();
                    String base_query = "SELECT DISTINCT movie.* " + //, genre.name AS 'genre'
                            "FROM movie " +
                            "WHERE movie.title = ? and movie.year = ?";
                    // Create the prepared statement
                    PreparedStatement base_ps = MoviesService.getCon().prepareStatement(base_query);

                    base_ps.setString(1, requestModel.getTitle());
                    base_ps.setInt(2, requestModel.getYear());

                    ServiceLogger.LOGGER.info("Trying selection: " + base_ps.toString());
                    ResultSet rs = base_ps.executeQuery();
                    ServiceLogger.LOGGER.info("selection succeeded.");

                    while (rs.next()) {
                        movieList.add(rs.getString("title"));
                    }

                    //Case 312: Shopping cart item does not exist.
                    if(!movieList.isEmpty()){
                        //Case 216: Movie already exists
                        responseModel = new ResponseModel_add(216, "Movie already exists.",responseModel.getMovie_id());
                        ServiceLogger.LOGGER.severe("Movie already exists.");
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }else{
                        //Todo add
                        //check any movie start with "cs"
                        ArrayList<String> movieIDList = new ArrayList<String>();
                        String check_cs_query = "SELECT DISTINCT movie.* " + //, genre.name AS 'genre'
                                "FROM movie " +
                                "WHERE movie.movie_id like 'cs%' ";
                        // Create the prepared statement
                        PreparedStatement check_cs_ps = MoviesService.getCon().prepareStatement(check_cs_query);

                        ServiceLogger.LOGGER.info("Trying selection: " + check_cs_ps.toString());
                        ResultSet check_cs_rs = check_cs_ps.executeQuery();
                        ServiceLogger.LOGGER.info("selection succeeded.");

                        while (check_cs_rs.next()) {
                            movieIDList.add(check_cs_rs.getString("movie_id"));
                        }
                        ServiceLogger.LOGGER.info("movieIDList size: " +  movieIDList.size());

                        // Construct the query
                        int currentID = 0;
                        String newMovie_id = "";
                        if(!movieIDList.isEmpty()){
                            String lastMovie_id = movieIDList.get(movieIDList.size() - 1);
                            ServiceLogger.LOGGER.info(" currentID: " +  currentID);
                            currentID = Integer.parseInt(lastMovie_id.substring(2)) + 1;
                            ServiceLogger.LOGGER.info(" currentID: " +  currentID);
                            newMovie_id = "cs000000" + currentID;
                            ServiceLogger.LOGGER.info("newMovie_id: " +  newMovie_id);
                            responseModel.setMovie_id(newMovie_id);
                        }else
                        {
                            newMovie_id = "cs0000001";
                            responseModel.setMovie_id(newMovie_id);
                        }
                        //check whether we have the director
                        ArrayList<String> directorList = new ArrayList<String>();
                        String director_query = "SELECT DISTINCT person.* " +
                                "FROM person " +
                                "WHERE person.name = ? ";
                        // Create the prepared statement
                        PreparedStatement director_ps = MoviesService.getCon().prepareStatement(director_query);

                        director_ps.setString(1, requestModel.getDirector());
                        // base_ps.setInt(2, requestModel.getQuantity());

                        ServiceLogger.LOGGER.info("Trying selection: " + director_ps.toString());
                        ResultSet director_rs = director_ps.executeQuery();
                        ServiceLogger.LOGGER.info("selection succeeded.");

                        while (director_rs.next()) {
                            directorList.add(director_rs.getString("person_id"));
                        }

                        if(directorList.isEmpty()){
                            //SELECT COUNT(*) FROM person
                            int totalNum = 0;
                            String number_of_person_query =  "SELECT COUNT(*) FROM person";
                            // Create the prepared statement
                            PreparedStatement number_of_person_ps = MoviesService.getCon().prepareStatement(number_of_person_query);

                            //number_of_person_ps.setString(1, requestModel.getDirector());
                            // base_ps.setInt(2, requestModel.getQuantity());

                            ServiceLogger.LOGGER.info("Trying selection: " + number_of_person_ps.toString());
                            ResultSet number_of_person_rs = number_of_person_ps.executeQuery();
                            ServiceLogger.LOGGER.info("selection succeeded.");

                            while (number_of_person_rs.next()) {
                                totalNum = number_of_person_rs.getInt("COUNT(*)");
                            }

                            //int new_person_id = (totalNum + 1);
                            //String id = new_person_id.toString();
                            //insert name into person
                            String insert_query =  "Insert into person (person_id, name)" +
                                    "VAlUE(?, ?) ";

                            // Create the prepared statement
                            PreparedStatement insert_ps = MoviesService.getCon().prepareStatement(insert_query);

                            insert_ps.setString(1, String.valueOf(totalNum + 1) );
                            insert_ps.setString(2, requestModel.getDirector());

                            ServiceLogger.LOGGER.info("start insertion...");
                            insert_ps.executeUpdate(); //ps.executeUpdate();
                            ServiceLogger.LOGGER.info("insertion succeeded.");

                        }

                        //get the id from person
                        String director_id = "";
                        String get_id_query = "SELECT DISTINCT person.* " + //, genre.name AS 'genre'
                                "FROM person " +
                                "WHERE person.name = ? ";
                        // Create the prepared statement
                        PreparedStatement get_id_ps = MoviesService.getCon().prepareStatement(get_id_query);

                        get_id_ps.setString(1, requestModel.getDirector());
                        // base_ps.setInt(2, requestModel.getQuantity());

                        ServiceLogger.LOGGER.info("Trying selection: " + get_id_ps.toString());
                        ResultSet get_id_rs = get_id_ps.executeQuery();
                        ServiceLogger.LOGGER.info("selection succeeded.");

                        while (get_id_rs.next()) {
                            director_id = get_id_rs.getString("person_id");
                        }




                        String query =  "INSERT into movie (movie_id, title, year, director_id, rating, num_votes, budget, revenue, overview, backdrop_path, poster_path, hidden) " +
                                "VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        // Create the prepared statement
                        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
                        ServiceLogger.LOGGER.info("SQL: " + ps);

                        // Set the arguments
                        ps.setString(1, newMovie_id);
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




                        // Save the query result to a ResultSet so records may be retrieved
                        ServiceLogger.LOGGER.info("Trying insertion: " + ps.toString());
                        // code = ps_user_status.executeUpdate();
                        // code = ps_privilege_level.executeUpdate();
                        ps.executeUpdate();
                        ServiceLogger.LOGGER.info("insertion succeeded.");
                        // ServiceLogger.LOGGER.info(query);
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                    //Case 215: Case 215: Could not add movie.
                    responseModel = new ResponseModel_add(215, "Case 215: Could not add movie.",responseModel.getMovie_id());
                    ServiceLogger.LOGGER.severe("Case 215: Could not add movie.");
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }

            }
        } catch (IOException e) {
            //ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_add(resultCode, "JSON Parse Exception",responseModel.getMovie_id());
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {

                resultCode = -2;
                responseModel = new ResponseModel_add(resultCode, "JSON Mapping Exception",responseModel.getMovie_id());
                ServiceLogger.LOGGER.info("*****************************" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_add(resultCode, "Internal Server Error",responseModel.getMovie_id());
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        //Case 214: Movie successfully added.
        responseModel = new ResponseModel_add(214, "Case 214: Movie successfully added.",responseModel.getMovie_id());
        ServiceLogger.LOGGER.severe("Case 214: Movie successfully added.");
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
