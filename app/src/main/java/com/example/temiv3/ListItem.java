package com.example.temiv3;

import static com.example.temiv3.MainActivity.TAG;

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
                 String sqlinsert = "select * from Garage";
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
                String checkQuery = "SELECT COUNT(*) AS count FROM Garage WHERE Location = ?";
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
                String sqlInsert = "INSERT INTO Garage (Location) VALUES (?)";
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                statement.setString(1, location);
                int rowsAffected = statement.executeUpdate();
                connection.close();
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }

    }
    public void deleteLocation(String location) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                // Sprawdź, czy lokalizacja istnieje w bazie danych
                String checkQuery = "SELECT COUNT(*) AS count FROM Garage WHERE Location = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, location);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt("count");
                if (count == 0) {
                    Log.d("Delete Location", "Location does not exist in the database");
                    connection.close();
                    return; // Lokalizacja nie istnieje, nie ma potrzeby usuwać
                }

                // Usuń lokalizację z bazy danych
                String deleteQuery = "DELETE FROM Garage WHERE Location = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setString(1, location);
                int rowsAffected = deleteStatement.executeUpdate();
                Log.d("Delete Location", "Location deleted successfully");
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
                String sqlQuery = "SELECT Description FROM Garage WHERE Location = ?";
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

    public void updateDescriptionInDatabase(String location, String newDescription) {
        Log.d(TAG,"Tu");
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            Connection connection = connectionHelper.connectionclass();
            if (connection != null) {
                String sqlUpdate = "UPDATE Garage SET Description = ? WHERE Location = ?";
                PreparedStatement statement = connection.prepareStatement(sqlUpdate);
                statement.setString(1, newDescription);
                statement.setString(2, location);
                int rowsAffected = statement.executeUpdate();
                Log.d("Update Database", rowsAffected + " rows affected");
                connection.close();
            }else{
                Log.d(TAG,"NIe ma");
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
    }



}
