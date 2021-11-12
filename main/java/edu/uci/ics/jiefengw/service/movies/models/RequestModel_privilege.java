package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.MoviesService;
import edu.uci.ics.jiefengw.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestModel_privilege extends emailModel{
    /*
  @JsonProperty(value = "email", required = true)
  private String email;
   */
    @JsonProperty(value = "plevel", required = true)
    private int plevel;

    public RequestModel_privilege(String email, int plevel) {
        super(email);
        this.plevel = plevel;
    }
    public RequestModel_privilege() {}

    //Setter and Getter
    public int getPlevel() {return plevel;}
    public void setPlevel(int plevel) { this.plevel = plevel; }


    public boolean checkSufficientPLever() {
        boolean sufficient = true;
        try {
            String query = "SELECT plevel from user WHERE email = ?";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                int plevelInDB = resultSet.getInt("plevel");

                ServiceLogger.LOGGER.info("plevel to check:" + plevel);
                ServiceLogger.LOGGER.info("plevelInDB:" + plevelInDB);
                if(plevel >= plevelInDB) {
                    sufficient = true;
                }else{
                    sufficient = false;
                }
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        return sufficient;
    }
}
