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

@Path("people")
public class TestPage_people_add {
    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forPeopleAdd(@Context HttpHeaders headers, String jsonText_people_add){
        // Set the path of the endpoint we want to communicate with
        IdmConfigs configs = MoviesService.getIdmConfigs();
        String servicePath = configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath();//"http://localhost:2942/activity";
        String endpointPath = "/privilege";

        // Declare models
        RequestModel_people_add requestModel = new RequestModel_people_add();
        RequestModel_privilege requestModel_privilege = new RequestModel_privilege();
        ResponseModel_people_add responseModel = null;

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
            responseModel = mapper.readValue(jsonText, ResponseModel_people_add.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");

            //not sufficient privilege
            if(responseModel.getResultCode() != 140){
                //Case 224: Could not update person.
                responseModel = new ResponseModel_people_add(224, "Could not update person.");
                ServiceLogger.LOGGER.severe("Could not update person.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            //sufficient privilege
            else{
                requestModel = mapper.readValue(jsonText_people_add, RequestModel_people_add.class);
                ServiceLogger.LOGGER.severe("requestModel person_id: " + requestModel.getPerson_id());

                //TODO check whether we have this movie or not, yes, update, else 211
                try{

                    ArrayList<String> personList = new ArrayList<String>();
                    String base_query = "SELECT DISTINCT person.* " + //, genre.name AS 'genre'
                            "FROM person " +
                            "WHERE person.person_id = ? ";
                    // Create the prepared statement
                    PreparedStatement base_ps = MoviesService.getCon().prepareStatement(base_query);

                    base_ps.setInt(1, requestModel.getPerson_id());
                    // base_ps.setInt(2, requestModel.getQuantity());

                    ServiceLogger.LOGGER.info("Trying selection: " + base_ps.toString());
                    ResultSet rs = base_ps.executeQuery();
                    ServiceLogger.LOGGER.info("selection succeeded.");

                    while (rs.next()) {
                        personList.add(rs.getString("person_id"));
                    }


                    if(!personList.isEmpty()){
                        //Case 226: Person ID already in use.
                        responseModel = new ResponseModel_people_add(226, "Person ID already in use.");
                        ServiceLogger.LOGGER.severe("Person ID already in use.");
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    }else{

                        String query =  "INSERT INTO person (person_id, name, gender_id, birthday, deathday, biography, popularity, profile_path)" +
                                " VALUE (?, ?, ?, ?, ?, ?,?, ?)";

                        // Create the prepared statement
                        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

                        // Set the arguments
                        ps.setInt(1, requestModel.getPerson_id());
                        ps.setString(2, requestModel.getName());
                        ps.setInt(3, requestModel.getGender_id());
                        ps.setString(4, requestModel.getBirthday());
                        ps.setString(5, requestModel.getDeathday());
                        ps.setString(6, requestModel.getBiography());
                        ps.setFloat(7, requestModel.getPopularity());
                        ps.setString(8, requestModel.getProfile_path());


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
                    //Case 227: Could not add person.
                    responseModel = new ResponseModel_people_add(227, "Could not add person.");
                    ServiceLogger.LOGGER.severe("Could not add person.");
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }

            }
        } catch (IOException e) {
            //ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_people_add(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {

                resultCode = -2;
                responseModel = new ResponseModel_people_add(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.info("*****************************" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_people_add(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        //Case 228: Person successfully added.
        responseModel = new ResponseModel_people_add(228, "Person successfully added.");
        ServiceLogger.LOGGER.severe("Person successfully added.");
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
