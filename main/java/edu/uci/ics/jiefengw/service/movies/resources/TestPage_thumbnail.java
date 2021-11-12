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

@Path("thumbnail")
public class TestPage_thumbnail {
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response forThumbnail(@Context HttpHeaders headers, String jsonText){
            RequestModel_thumbnail requestModel;
            ResponseModel_thumbnail responseModel = new ResponseModel_thumbnail(0, "", null);
            ObjectMapper mapper = new ObjectMapper();

            // Get header strings
            // If there is no header with given key, it will be null
            String email = headers.getHeaderString("email");
            ServiceLogger.LOGGER.info("email: " + email);
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            try{
                requestModel = mapper.readValue(jsonText, RequestModel_thumbnail.class);
            }catch (IOException e) {
                // Catch other exceptions here
                int resultCode;
                e.printStackTrace();

                resultCode = -1;
                responseModel = new ResponseModel_thumbnail(resultCode, "Internal Server Error",null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }

            //MySQL
            ArrayList<thumbnailModel> tempList = new ArrayList<thumbnailModel>();
            for (int i = 0; i < requestModel.getMovie_id().length; i++){
                String base_query = "SELECT DISTINCT movie.* " + //, genre.name AS 'genre'
                        "FROM movie " +
                        "LEFT JOIN person ON movie.director_id = person.person_id " +
                        "LEFT JOIN genre_in_movie ON movie.movie_id = genre_in_movie.movie_id " +
                        "LEFT JOIN genre ON genre.genre_id = genre_in_movie.genre_id " +
                        "WHERE TRUE and movie.movie_id = ?";

                StringBuilder query_builder = new StringBuilder(base_query);

                try {
                    String query_string = query_builder.toString();
                    PreparedStatement query = MoviesService.getCon().prepareStatement(query_string);
                    int w = 1;
                    ServiceLogger.LOGGER.info(requestModel.getMovie_id()[i]);
                    if(requestModel.getMovie_id()[i] != null){
                        query.setString(w, requestModel.getMovie_id()[i]);
                    }

                    ServiceLogger.LOGGER.info(query.toString());

                    ResultSet rs = query.executeQuery();
                    // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
                    // Use executeUpdate() for queries that CidHANGE the DB (returns # of rows modified as int)
                    // Use execute() for general purpose queries
                    ServiceLogger.LOGGER.info("Query succeeded.");

                    while (rs.next()) {

                        thumbnailModel newThumbNail = new thumbnailModel();
                        newThumbNail.setMovie_id(rs.getString("movie_id"));
                        newThumbNail.setTitle(rs.getString("title"));
                        newThumbNail.setBackdrop_path(rs.getString(("backdrop_path")));
                        newThumbNail.setPoster_path(rs.getString(("poster_path")));

                        tempList.add(newThumbNail);
                        ServiceLogger.LOGGER.info("Retrieved User: (" + newThumbNail.getTitle() + ")");

                    }
                    try{
                        ServiceLogger.LOGGER.info("tempList.size(): " + tempList.size());
                        ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
                        responseModel.setThumbNailList(tempList);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }catch (SQLException e) {
                    ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
                    e.printStackTrace();
                }
            }

            if( requestModel.getMovie_id().length != 0)
            {
                responseModel = new ResponseModel_thumbnail(210, "Found movie(s) with search parameters.", responseModel.getThumbNailList());
                ServiceLogger.LOGGER.severe("Found movie(s) with search parameters.");
                //return Response.status(Response.Status.OK).entity(responseModel).build();
            }else{
                responseModel = new ResponseModel_thumbnail(211, "No movies found with search parameters.", null);
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
