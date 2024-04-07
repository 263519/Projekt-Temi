package com.example.temiv3;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import  java.util.Map;
import  java.util.List;

public class ListItem {


    Connection connection;
    String ConnectionResult="";
    Boolean isSuucess = false;


      public List<Map<String,String>>getlist()
     {
         List<Map<String,String>> data = null;
         data = new ArrayList<Map<String,String>>();

         try {
             ConnectionHelper connectionHelper = new ConnectionHelper();
             connection = connectionHelper.connectionclass();
             if (connection != null) {
                 String sqlinsert = "select * from Garage1";
                 Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(sqlinsert);
                 while (rs.next()){
                     Map<String,String> dtname = new HashMap<String ,String>();


                     dtname.put("locList",rs.getString("Location"));
                     dtname.put("desList",rs.getString("Description"));
                     data.add(dtname);
                 }
                 ConnectionResult="Success";
                 isSuucess=true;
                 connection.close();
             }else{
                 ConnectionResult="Failed";
             }
         } catch (Exception exception) {
             Log.e("Error", exception.getMessage());
         }

        return data;
      }


    public void addLocation(String location) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                // Sprawdź, czy lokalizacja już istnieje w bazie danych
                String checkQuery = "SELECT COUNT(*) AS count FROM Garage1 WHERE Location = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, location);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt("count");
                if (count > 0) {
                    Log.d("Add Location", "Location already exists in the database");
                    connection.close();
                    return; // Lokalizacja już istnieje, nie dodawaj ponownie
                }

                // Dodaj lokalizację do bazy danych
                String sqlInsert = "INSERT INTO Garage1 (Location) VALUES (?)";
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                statement.setString(1, location);
                int rowsAffected = statement.executeUpdate();
                connection.close();
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }

    }



    public String getDescriptionByLocation(String location) {
        String description = null;
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                String sqlQuery = "SELECT Description FROM Garage1 WHERE Location = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, location);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    description = resultSet.getString("Description");
                }
                connection.close();
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
        return description;
    }


}
