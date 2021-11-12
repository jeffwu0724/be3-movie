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

@Path("people")
public class TestPage_people {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forPeople(@Context HttpHeaders headers,
                              @QueryParam("name") String name,
                              @QueryParam("title") String title,
                              @QueryParam("year") Integer year,
                              @QueryParam("director") String director,
                              @QueryParam("genre") String genre,
                              @QueryParam("hidden") Boolean hidden,
                              @QueryParam("limit") @DefaultValue("10") Integer limit,
                              @QueryParam("offset") @DefaultValue("0") Integer offset,
                              @QueryParam("orderby") @DefaultValue("title") String orderby,
                              @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        if (limit != null && limit != 10 && limit != 25 && limit != 50 && limit != 100){
            limit = 10;
        }
        if (orderby != null && !orderby.equals("title") && !orderby.equals("rating") && !orderby.equals("year")){
            orderby = "title";
        }
        if (direction != null && !direction.equals("asc") && !direction.equals("desc")){
            direction = "asc";
        }
        if (offset != null && offset % limit != 0){
            offset = 0;
        }


        // Set the path of the endpoint we want to communicate with
        IdmConfigs configs = MoviesService.getIdmConfigs();

        ServiceLogger.LOGGER.info(configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath());
        String servicePath = configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath();//;//"http://localhost:12345/api/idm"
        String endpointPath = "/privilege";

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Declare models
        //RequestModel_search requestSearch = new RequestModel_search(); // "AdminLevel@uci.edu", 5
        RequestModel_privilege requestPrivilege = new RequestModel_privilege();
        ResponseModel_people responseModel = null;

        //try to see if it is privilege sufficient or not
        boolean privilegeSatisfied = false;
        if (email == null) {
            privilegeSatisfied = false;
        }

        requestPrivilege.setEmail(email);
        requestPrivilege.setPlevel(4);

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestPrivilege, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        //ResponseModel_privilege responsePrivilege = response.readEntity(ResponseModel_privilege.class);

        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, ResponseModel_people.class);

            ServiceLogger.LOGGER.info("responsePrivilege.getResultCode() " + responseModel.getResultCode());
            if (responseModel.getResultCode() == 140) {
                privilegeSatisfied = true;
            }

            /*
            SELECT DISTINCT person.name AS 'name', movie.* FROM movie
    LEFT JOIN person_in_movie ON movie.movie_id = person_in_movie.movie_id
    LEFT JOIN person ON person_in_movie.person_id = person.person_id
    LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id
    LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id
WHERE TRUE and person.name = 'William Bryant'
             */
            //MySQL
            String base_query = "SELECT DISTINCT person.name AS 'name', person.name AS 'director', movie.* " + //, genre.name AS 'genre'
                    "FROM movie " +
                    "LEFT JOIN person_in_movie ON movie.movie_id = person_in_movie.movie_id " +
                    "LEFT JOIN person ON person_in_movie.person_id = person.person_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "WHERE TRUE ";

            StringBuilder query_builder = new StringBuilder(base_query);

            if(name != null) {
                query_builder.append(" AND person.name LIKE ?");
            }


            String secondary_orderby = "rating";
            String secondary_direction = "desc";
            if(orderby.equals("title")) {
                secondary_orderby = "rating";
                secondary_direction = "desc";
            }
            if(orderby.equals("rating")) {
                secondary_orderby = "title";
                secondary_direction = "asc";
            }
            if(orderby.equals("year")) {
                secondary_orderby = "rating";
                secondary_direction = "desc";
            }
            query_builder.append(String.format(" ORDER BY %S %S, %S %S",
                    orderby,direction,
                    secondary_orderby, secondary_direction));

            query_builder.append(String.format(" LIMIT %d", limit));

            try {
                String query_string = query_builder.toString();
                PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);
                int i = 1;

                if(name != null){
                    query.setString(i,name);

                }

                ArrayList<movieModel> tempList = new ArrayList<movieModel>();
                ResultSet rs = query.executeQuery();
                // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
                // Use executeUpdate() for queries that CidHANGE the DB (returns # of rows modified as int)
                // Use execute() for general purpose queries
                ServiceLogger.LOGGER.info("Query succeeded.");



                while (rs.next()) {
                    //ServiceLogger.LOGGER.info("In the loop!!!!!!!!!!");
                    movieModel newMovie = new movieModel();
                    newMovie.setMovie_id(rs.getString("movie_id"));
                    newMovie.setTitle(rs.getString("title"));
                    newMovie.setYear(rs.getInt("year"));
                    newMovie.setDirector(rs.getString("director"));
                    newMovie.setRating(rs.getFloat("rating"));
                    newMovie.setBackdrop_path(rs.getString(("backdrop_path")));
                    newMovie.setPoster_path(rs.getString(("poster_path")));
                    if(privilegeSatisfied == false) {   // not satisfied
                        newMovie.setHidden(null);
                        //query_builder.append(" AND hidden = null");
                    }else{              //satisfied
                        newMovie.setHidden(false);
                    }
                    //newMovie.setHidden(rs.getBoolean("hidden"));
                    tempList.add(newMovie);
                    ServiceLogger.LOGGER.info("Retrieved User: (" + newMovie.getTitle() + " " + newMovie.getHidden() + " )");

                }
                responseModel.setMovieList(tempList);
            }catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
                e.printStackTrace();
            }

            ServiceLogger.LOGGER.info("privilegeSatisfied is: " + privilegeSatisfied);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            e.printStackTrace();
        }



        ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getMovieList().size());
        if( responseModel.getMovieList().size() != 0)
        {
            responseModel = new ResponseModel_people(210, "Found movie(s) with search parameters.", responseModel.getMovieList());
            ServiceLogger.LOGGER.severe("Found movie(s) with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }else{
            responseModel = new ResponseModel_people(211, "No movies found with search parameters.", null);
            ServiceLogger.LOGGER.severe("No movies found with search parameters.");
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
