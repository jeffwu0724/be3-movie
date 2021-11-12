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
public class TestPage_people_search {
    @Path("search")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forPeopleSearch(@Context HttpHeaders headers,
                                    @QueryParam("name") String name,
                                    @QueryParam("birthday") String birthday,
                                    @QueryParam("movie_title") String movie_title,
                                    @QueryParam("year") Integer year,
                                    @QueryParam("director") String director,
                                    @QueryParam("genre") String genre,
                                    @QueryParam("hidden") Boolean hidden,
                                    @QueryParam("limit") @DefaultValue("10") Integer limit,
                                    @QueryParam("offset") @DefaultValue("0") Integer offset,
                                    @QueryParam("orderby") @DefaultValue("name") String orderby,
                                    @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        if (limit != null && limit != 10 && limit != 25 && limit != 50 && limit != 100){
            limit = 10;
        }
        if (orderby != null && !orderby.equals("name") && !orderby.equals("birthday") && !orderby.equals("popularity")){
            orderby = "name";
        }
        if (direction != null && !direction.equals("asc") && !direction.equals("desc")){
            direction = "asc";
        }
        if (offset != null && offset % limit != 0){
            offset = 0;
        }

        // Get header strings
        // If there is no header with given key, it will be null
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        // Declare models
        //RequestModel_search requestSearch = new RequestModel_search(); // "AdminLevel@uci.edu", 5
       // RequestModel_privilege requestPrivilege = new RequestModel_privilege();
        ResponseModel_people_search responseModel = new ResponseModel_people_search(0,"", null);


        //try{
            //ObjectMapper mapper = new ObjectMapper();
           // String jsonText = response.readEntity(String.class);
           // responseModel = mapper.readValue(jsonText, ResponseModel_people_search.class);

            ServiceLogger.LOGGER.info("here！ " );
            //MySQL
            String base_query = "SELECT DISTINCT person.* " + //, genre.name AS 'genre'
                    "FROM person " +
                    "LEFT JOIN person_in_movie ON person.person_id = person_in_movie.person_id " +
                    "LEFT JOIN movie ON person_in_movie.movie_id = movie.movie_id " +
                    "WHERE TRUE ";

            StringBuilder query_builder = new StringBuilder(base_query);
            if(movie_title != null) {
                query_builder.append(" AND movie.title LIKE ?");
            }
            if(birthday != null) {
                query_builder.append(" AND person.birthday = ?");
            }
            if(name != null) {
                query_builder.append(" AND person.name LIKE ?");
            }


            String secondary_orderby = "popularity";
            String secondary_direction = "desc";
            if(orderby.equals("name")) {
                secondary_orderby = "popularity";
                secondary_direction = "desc";
            }
            if(orderby.equals("birthday")) {
                secondary_orderby = "popularity";
                secondary_direction = "desc";
            }
            if(orderby.equals("popularity")) {
                secondary_orderby = "name";
                secondary_direction = "asc";
            }
            query_builder.append(String.format(" ORDER BY %S %S, %S %S",
                    orderby,direction,
                    secondary_orderby, secondary_direction));

            query_builder.append(String.format(" LIMIT %d", limit));
            query_builder.append(String.format(" OFFSET %d", offset));

            try {
                String query_string = query_builder.toString();
                PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);

                /*
                 if(movie_title != null) {
                query_builder.append(" AND movie.title LIKE ?");
            }
            if(birthday != null) {
                query_builder.append(" AND person.birthday = ?");
            }
            if(name != null) {
                query_builder.append(" AND person.name LIKE ?");
            }
                 */
                int i = 1;
                if(movie_title != null){
                    String newTitle = "%" + movie_title + "%";
                    query.setString(i,newTitle);
                    i += 1;
                }
                if(birthday != null){
                    query.setString(i, birthday);
                    i += 1;
                }
                if(name != null){
                    String newName = "%" + name + "%";
                    query.setString(i,newName);
                    i += 1;
                }

                ServiceLogger.LOGGER.info(query.toString());
                ArrayList<updatePersonModel> tempList = new ArrayList<updatePersonModel>();
                ResultSet rs = query.executeQuery();
                // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
                // Use executeUpdate() for queries that CidHANGE the DB (returns # of rows modified as int)
                // Use execute() for general purpose queries
                ServiceLogger.LOGGER.info("Query succeeded.");

                /*
                 this.person_id = person_id;
        this.name = name;
        this.birthday = birthday;
        this.popularity = popularity;
        this.profile_path = profile_path;
                 */
                try{
                    while (rs.next()) {
                        //ServiceLogger.LOGGER.info("In the loop!!!!!!!!!!");
                        updatePersonModel newPeople = new updatePersonModel();
                        newPeople.setPerson_id(rs.getInt("person_id"));
                        newPeople.setName(rs.getString("name"));
                        newPeople.setBirthday(rs.getString("birthday"));
                        newPeople.setPopularity(rs.getFloat("popularity"));
                        newPeople.setProfile_path(rs.getString(("profile_path")));
                        ServiceLogger.LOGGER.info("Retrieved User: (" + newPeople.getName() + " )");
                        //newMovie.setHidden(rs.getBoolean("hidden"));
                        tempList.add(newPeople);
                    }
                    responseModel.setPeopleList(tempList);
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



        ServiceLogger.LOGGER.info("arraylist size: " + responseModel.getPeopleList().size());
        if( responseModel.getPeopleList().size() != 0)
        {
            responseModel = new ResponseModel_people_search(212, "Found people with search parameters.", responseModel.getPeopleList());
            ServiceLogger.LOGGER.severe("Found people with search parameters.");
            //return Response.status(Response.Status.OK).entity(responseModel).build();
        }else{
            responseModel = new ResponseModel_people_search(213, "No people found with search parameters.", null);
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
