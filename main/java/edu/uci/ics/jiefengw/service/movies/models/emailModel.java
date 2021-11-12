package edu.uci.ics.jiefengw.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.movies.MoviesService;
import edu.uci.ics.jiefengw.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class emailModel {
    @JsonProperty(value = "email", required = true)
    protected String email;

    public emailModel(String email)
    {
        this.email = email;
    }
    public emailModel() {}

    //Getter and Setter
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    //check email valid or not
    public boolean checkEmailValid(){
        ServiceLogger.LOGGER.info("检查邮箱是否符合！！！");
        ServiceLogger.LOGGER.info(email);
        String emailRegex = "^[a-zA-Z0-9]+@(.+)\\.(.+)$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        ServiceLogger.LOGGER.info("邮箱符合情况： " + String.valueOf(pat.matcher(email).matches()));
        return pat.matcher(email).matches();
    }

    //check email is duplicated or not
    public boolean checkDuplicatedEmail(){
        int status = 0;
        try {
            String query = "SELECT email from user WHERE email = ?";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                status = 1;
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        if (status == 1){
            ServiceLogger.LOGGER.info("not null!!!!");
            return false;
        }else{
            ServiceLogger.LOGGER.info("null!!!!");
            return true;
        }
    }

    //check whether the user is in the data base or not
    public boolean checkUserExist() {
        int status = 0;
        try {
            String query = "SELECT email from user WHERE email = ?";
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                String emailInDB = resultSet.getString("email");
                ServiceLogger.LOGGER.info("有！！！！！");
                ServiceLogger.LOGGER.info(email);
                ServiceLogger.LOGGER.info(emailInDB);
                if(email.equals(emailInDB)) {
                    ServiceLogger.LOGGER.info("也有！！！！！");
                    status = 1;
                }
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        if (status == 1){
            ServiceLogger.LOGGER.info("email match");
            return true;
        }else{
            ServiceLogger.LOGGER.info("email not match !!!!");
            return false;
        }
    }
}
