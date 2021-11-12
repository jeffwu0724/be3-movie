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

@Path("browse/{phrase:.*}")
public class TestPage_browse {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forBrowse(@Context HttpHeaders headers,
                              @PathParam("phrase") String phrase,
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
        RequestModel_browse requestBrowse = new RequestModel_browse(); // "AdminLevel@uci.edu", 5
        RequestModel_privilege requestPrivilege = new RequestModel_privilege();
        ResponseModel_browse responseModel = null;

        //list for storing the token keyword
        ServiceLogger.LOGGER.info("phrase: " + phrase);
        String[] tokens = phrase.split(","); //[\s,]+
        for(int i = 0; i < tokens.length; i++){
            ServiceLogger.LOGGER.info("tokens: " + tokens[i]);
        }

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
            responseModel = mapper.readValue(jsonText, ResponseModel_browse.class);

            ServiceLogger.LOGGER.info("responsePrivilege.getResultCode() " + responseModel.getResultCode());
            if (responseModel.getResultCode() == 140) {
                privilegeSatisfied = true;
            }

        //MySQL
        if(phrase != null) {
            String base_query = "SELECT * FROM (SELECT distinct movie.*, person.name AS 'director' FROM movie " +
                    "LEFT JOIN  person ON movie.director_id = person.person_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "LEFT JOIN keyword_in_movie ON movie.movie_id = keyword_in_movie.movie_id " +
                    "LEFT JOIN keyword ON keyword.keyword_id = keyword_in_movie.keyword_id " +
                    "WHERE TRUE AND keyword.name = '" + tokens[0] + "') AS P0";

            StringBuilder query_builder = new StringBuilder(base_query);

            if(tokens.length > 1){  //if we have more than one token
                for (int w = 1; w < tokens.length; w++){
                    query_builder.append(" JOIN (SELECT distinct movie.*, person.name AS 'director' FROM movie " +
                            "LEFT JOIN  person ON movie.director_id = person.person_id " +
                            "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                            "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                            "LEFT JOIN keyword_in_movie ON movie.movie_id = keyword_in_movie.movie_id " +
                            "LEFT JOIN keyword ON keyword.keyword_id = keyword_in_movie.keyword_id " +
                            "WHERE TRUE AND keyword.name = '" + tokens[w] + "') AS P" + w);
                }
                query_builder.append(" WHERE P0.movie_id = P1.movie_id");
            }

            //try to add the table that with the same id
            if(tokens.length > 2) {  //if we have more than two token
                for (int w = 2; w < tokens.length; w++){
                    query_builder.append(" and P" + (w - 1)+ ".movie_id = P" + w + ".movie_id" );
                }
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
            query_builder.append(String.format(" ORDER BY p0.%S %S, p0.%S %S",
                    orderby,direction,
                    secondary_orderby, secondary_direction));

            query_builder.append(String.format(" LIMIT %d", limit));
            query_builder.append(String.format(" OFFSET %d", offset));


            try {
                String query_string = query_builder.toString();
                PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);
                int i = 1;
                if(title != null){
                    String newTitle = "%" + title + "%";
                    query.setString(i,newTitle);
                    i += 1;
                }
                if(year != null){
                    query.setInt(i, year);
                    i += 1;
                }
                if(director != null){
                    query.setString(i,director);
                    i += 1;
                }
                if(genre != null){
                    query.setString(i,genre);
                    i += 1;
                }
                /*
                if(phrase != null){
                    for (int j = 0; j < tokens.length; j++) {
                        query.setString(i, tokens[j]);
                        i += 1;
                    }

                }
                 */
                ServiceLogger.LOGGER.info(String.valueOf(query_builder));

                ArrayList<movieModel> tempList = new ArrayList<movieModel>();

                ResultSet rs = query.executeQuery();
                // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
                // Use executeUpdate() for queries that CHANGE the DB (returns # of rows modified as int)
                // Use execute() for general purpose queries
                ServiceLogger.LOGGER.info("Query succeeded.");

                while (rs.next()) {
                    ServiceLogger.LOGGER.info("In the loop!!!!!!!!!!");
                    movieModel newMovie = new movieModel();
                    newMovie.setMovie_id(rs.getString("movie_id"));
                    newMovie.setTitle(rs.getString("title"));
                    newMovie.setYear(rs.getInt("year"));
                    newMovie.setDirector(rs.getString("director"));
                    newMovie.setRating(rs.getFloat("rating"));
                    newMovie.setBackdrop_path(rs.getString(("backdrop_path")));
                    newMovie.setPoster_path(rs.getString(("poster_path")));
                    if(privilegeSatisfied == false) {   // not satisfied
                        //hidden = null;
                        newMovie.setHidden(null);
                        //query_builder.append(" AND hidden = null");
                    }else{              //satisfied
                        // hidden = false;
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
        }





       /*
        if(title != null) {
            query_builder.append(" AND title LIKE ?");
        }
        if(year != null) {
            query_builder.append(" AND year = ?");
        }
        if(director != null) {
            query_builder.append(" AND person.name LIKE ?");
        }
        if(genre != null) {
            query_builder.append(" AND genre.name LIKE ?");
        }
        */
            //use to expend the major query





        } catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
            e.printStackTrace();
        }



        ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getMovieList().size());
        if( responseModel.getMovieList().size() != 0)
        {
            responseModel = new ResponseModel_browse(210, "Found movie(s) with search parameters.", responseModel.getMovieList());
            ServiceLogger.LOGGER.severe("Found movie(s) with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }else{
            responseModel = new ResponseModel_browse(211, "No movies found with search parameters.", null);
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
