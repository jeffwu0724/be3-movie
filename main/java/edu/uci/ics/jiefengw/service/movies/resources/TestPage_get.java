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

@Path("get/{movie_id:.*}")
public class TestPage_get {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forBrowse(@Context HttpHeaders headers,
                              @PathParam("movie_id") String movie_id,
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
        ResponseModel_get responseModel = null;

        //list for storing the token keyword
        ServiceLogger.LOGGER.info("movie_id: " + movie_id);


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
            responseModel = mapper.readValue(jsonText, ResponseModel_get.class);

            ServiceLogger.LOGGER.info("responsePrivilege.getResultCode() " + responseModel.getResultCode());
            if (responseModel.getResultCode() == 140) {
                privilegeSatisfied = true;
            }

            /*
            SELECT distinct movie.*, person.name AS 'director', genre.name as 'genre', person.name as 'name', person_in_movie.person_id as 'person_id'
            FROM movie
            LEFT OUTER JOIN person ON movie.director_id = person.person_id
            LEFT OUTER JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id
            LEFT OUTER JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id
            LEFT OUTER JOIN genre ON genre.genre_id = genre_in_movie.genre_id
            WHERE TRUE and movie.movie_id = 'tt0003740';
             */
            //MySQL
            //, person.name AS 'director', genre.genre_id as 'genre_id', genre.name as 'genre_name', person.name as 'person_name', person_in_movie.person_id as 'person_id'
            String base_query = "SELECT DISTINCT movie.*, person.name AS 'director' " + //, genre.name AS 'genre'
                    "FROM movie " +
                    "LEFT JOIN person ON movie.director_id = person.person_id " +
                    "LEFT JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "WHERE TRUE AND movie.movie_id = ?";
            StringBuilder query_builder = new StringBuilder(base_query);

            String base_query_genre = "SELECT DISTINCT movie.*, person.name AS 'director', genre.genre_id as 'genre_id', genre.name as 'genre_name' " + //, genre.name AS 'genre'
                    "FROM movie " +
                    "LEFT JOIN person ON movie.director_id = person.person_id " +
                    "LEFT JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "WHERE TRUE AND movie.movie_id = ?";
            StringBuilder query_builder_genre = new StringBuilder(base_query_genre);

            /*

            SELECT DISTINCT movie.*, (select person.name from movie

    LEFT JOIN person ON movie.director_id = person.person_id
    WHERE TRUE AND movie.movie_id =  'tt0463034'
    ) AS 'director', person.name as 'person_name', person_in_movie.person_id as 'person_id'
                    FROM movie

                    LEFT JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id
                    LEFT JOIN person ON person_in_movie.person_id = person.person_id
                    LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id
                    LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id
                    WHERE TRUE AND movie.movie_id =  'tt0463034'

                    "SELECT DISTINCT movie.*, person.name AS 'director', person.name as 'person_name', person_in_movie.person_id as 'person_id' " + //, genre.name AS 'genre'
                    "FROM movie " +
                    "LEFT JOIN person ON movie.director_id = person.person_id " +
                    "LEFT JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "WHERE TRUE AND movie.movie_id = ?";
             */

            String base_query_person = "SELECT DISTINCT movie.*, " +
                    "(select person.name from movie " +
                    "LEFT JOIN person ON movie.director_id = person.person_id " +
                    "WHERE TRUE AND movie.movie_id = ? " +
                    ") AS 'director', person.name as 'person_name', person_in_movie.person_id as 'person_id' FROM movie " +
                    "LEFT JOIN person_in_movie on movie.movie_id =person_in_movie.movie_id " +
                    "LEFT JOIN person ON person_in_movie.person_id = person.person_id " +
                    "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                    "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                    "WHERE TRUE AND movie.movie_id = ? ";

            StringBuilder query_builder_person = new StringBuilder(base_query_person);



            try {
                String query_string = query_builder.toString();
                PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);
                int i = 1;
                if(movie_id != null){
                    query.setString(i,movie_id);

                }
                ArrayList<updateMovieModel> tempListForMovie = new ArrayList<updateMovieModel>();

                //Set<genreModel> setForGenre = new HashSet<genreModel>();
               //Set<personModel> setForPerson = new HashSet<personModel>();
                ResultSet rs = query.executeQuery();
                // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
                // Use executeUpdate() for queries that CidHANGE the DB (returns # of rows modified as int)
                // Use execute() for general purpose queries
                ServiceLogger.LOGGER.info("Query succeeded.");



                while (rs.next()) {
                    //ServiceLogger.LOGGER.info("In the loop!!!!!!!!!!");
                    updateMovieModel newMovie = new updateMovieModel();
                    ArrayList<genreModel> tempListForGenre = new ArrayList<genreModel>();
                    ArrayList<personModel> tempListForPerson = new ArrayList<personModel>();

                    ArrayList<Integer> listOfGenreID = new ArrayList<Integer>();
                    ArrayList<Integer> listOfPersonID = new ArrayList<Integer>();

                    newMovie.setMovie_id(rs.getString("movie_id"));
                    newMovie.setTitle(rs.getString("title"));
                    newMovie.setYear(rs.getInt("year"));
                    newMovie.setDirector(rs.getString("director"));
                    newMovie.setRating(rs.getFloat("rating"));
                    newMovie.setNum_votes(rs.getInt("num_votes"));
                    newMovie.setBudget(rs.getString("budget"));
                    newMovie.setRevenue(rs.getString("revenue"));
                    newMovie.setOverview(rs.getString("overview"));
                    newMovie.setBackdrop_path(rs.getString("backdrop_path"));
                    newMovie.setPoster_path(rs.getString("poster_path"));
                    if(privilegeSatisfied == false) {   // not satisfied
                        //hidden = null;
                        newMovie.setHidden(null);
                        //query_builder.append(" AND hidden = null");
                    }else{              //satisfied
                        // hidden = false;
                        newMovie.setHidden(false);
                    }

                    //try to get genre
                    String query_string_genre = query_builder_genre.toString();
                    PreparedStatement query_genre = MoviesService.getCon().prepareStatement(query_string_genre);
                    int j = 1;
                    if(movie_id != null){
                        query_genre.setString(j,movie_id);

                    }
                    ResultSet rs_genre = query_genre.executeQuery();
                    ServiceLogger.LOGGER.info("Query succeeded.");

                    while(rs_genre.next()){
                        genreModel newGenre = new genreModel();
                        newGenre.setGenre_id(rs_genre.getInt("genre_id"));
                        newGenre.setName(rs_genre.getString("genre_name"));
                        tempListForGenre.add(newGenre);

                    }
                    newMovie.setGenreList(tempListForGenre);


                    //try to get people
                    String query_string_person = query_builder_person.toString();
                    PreparedStatement query_person = MoviesService.getCon().prepareStatement(query_string_person);
                    int w = 1;
                    if(movie_id != null){
                        query_person.setString(w,movie_id);
                        query_person.setString(w+1,movie_id);

                    }
                    ResultSet rs_person = query_person.executeQuery();
                    ServiceLogger.LOGGER.info("Query succeeded.");

                    while(rs_person.next()){
                        personModel newPeople = new personModel();
                        newPeople.setPerson_id(rs_person.getInt("person_id"));
                        newPeople.setName(rs_person.getString("person_name"));
                        tempListForPerson.add(newPeople);

                    }
                    newMovie.setPeopleList(tempListForPerson);



                    //newPeople.setPerson_id(rs.getInt("person_id"));
                    //newPeople.setName(rs.getString("person_name"));

                    /*
                    if(!listOfGenreID.contains(newGenre.getGenre_id())){
                        tempListForGenre.add(newGenre);
                    }
                    if(!listOfPersonID.contains(newPeople.getPerson_id())){
                        tempListForPerson.add(newPeople);
                    }

                     listOfGenreID.add(newGenre.getGenre_id());
                    listOfPersonID.add(newPeople.getPerson_id());
                     */







                   //

                    /*
                    if(!tempListForGenre.contains(newGenre)){
                        ServiceLogger.LOGGER.info("Adding genre list " + newGenre);
                        tempListForGenre.add(newGenre);
                    }
                    if(!tempListForPerson.contains(newPeople)){
                        ServiceLogger.LOGGER.info("Adding person list " + newPeople);
                        tempListForPerson.add(newPeople);
                    }
                     setForGenre.add(newGenre);
                    ServiceLogger.LOGGER.info("genre_id: " + newGenre.getGenre_id() + " genre_name: " + newGenre.getName());

                    setForPerson.add(newPeople);
                    ServiceLogger.LOGGER.info("person_id: " + newPeople.getPerson_id() + " people_name: " + newPeople.getName());

                    tempListForGenre.addAll(setForGenre);
                    tempListForPerson.addAll(setForPerson);

                    for (int j = 1; j < tempListForGenre.size(); j++){
                        if(tempListForGenre.get(j).getGenre_id() == newGenre.getGenre_id()){
                            break;
                        }
                        if(tempListForGenre.get(j).getGenre_id() != newGenre.getGenre_id() && j == tempListForGenre.size() - 1){
                            newMovie.setGenreList(tempListForGenre);
                        }
                    }

                    for (int j = 1; j < tempListForPerson.size(); j++){
                        if(tempListForPerson.get(j).getPerson_id() == newPeople.getPerson_id()){
                            break;
                        }
                        if(tempListForPerson.get(j).getPerson_id() != newPeople.getPerson_id() && j == tempListForPerson.size() - 1){
                            newMovie.setPeopleList(tempListForPerson);
                        }
                    }
                     */



                   // newMovie.setPeopleList(tempListForPerson);

                    tempListForMovie.add(newMovie);
                    responseModel.setUpdateMovie(newMovie);
                    ServiceLogger.LOGGER.info("Retrieved User: (" + newMovie.getTitle() + " " + newMovie.getHidden() + " )");
                   // ServiceLogger.LOGGER.info("Retrieved User: (" + newMovie.getGenresList() + " " + newMovie.getPeopleList() + " )");


                }
               //ServiceLogger.LOGGER.info("genre set size: " + setForGenre.size());
               // ServiceLogger.LOGGER.info("person set size: " + setForPerson.size());
               // ServiceLogger.LOGGER.info("genre arraylist size: " + tempListForGenre.size());
               // ServiceLogger.LOGGER.info("person arraylist size: " + tempListForPerson.size());
                ServiceLogger.LOGGER.info("movie arraylist size: " + tempListForMovie.size());



                ServiceLogger.LOGGER.info("added to responseModel");
               // ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getUpdateMovie().size());

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




       // ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getUpdateMovie().size());
        if( responseModel.getUpdateMovie() != null)
        {
            responseModel = new ResponseModel_get(210, "Found movie(s) with search parameters.", responseModel.getUpdateMovie());
            ServiceLogger.LOGGER.severe("Found movie(s) with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }else{
            responseModel = new ResponseModel_get(211, "No movies found with search parameters.", null);
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
