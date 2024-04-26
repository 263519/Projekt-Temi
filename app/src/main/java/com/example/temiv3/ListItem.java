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


    public boolean addLocation(String location,String description) {
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
                    return false; // Lokalizacja już istnieje, nie dodawaj ponownie
                }

                // Dodaj lokalizację do bazy danych
                //String sqlInsert = "INSERT INTO Garage (Location) VALUES (?)";
                String sqlInsert = "INSERT INTO Garage (Location, Description) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                statement.setString(1, location);
                statement.setString(2, description);
                int rowsAffected = statement.executeUpdate();

                connection.close();
                return true;
            }
        } catch (Exception exception) {

            Log.e("Error", exception.getMessage());
            return false;
        }
        return false;
    }

    public void saveMapUrltoDatabase(String MapName,String filepath) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                // Sprawdź, czy lokalizacja już istnieje w bazie danych
                String checkQuery = "SELECT COUNT(*) AS count FROM SavedMaps WHERE MapName = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, MapName);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt("count");
                if (count > 0) {
                    Log.d("ReSave map", "Location already exists in the database");

                    // Aktualizuj istniejący rekord
                    String updateQuery = "UPDATE SavedMaps SET filepath = ? WHERE MapName = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, filepath);
                    updateStatement.setString(2, MapName);
                    int rowsAffected = updateStatement.executeUpdate();

                    Log.d("ReSave map", "Existing map updated");
                } else {
                    // Dodaj nowy rekord do bazy danych
                    String sqlInsert = "INSERT INTO SavedMaps (MapName, filepath) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sqlInsert);
                    statement.setString(1, MapName);
                    statement.setString(2, filepath);
                    int rowsAffected = statement.executeUpdate();

                    Log.d("Save map", "New map added to the database");
                }

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


    public List<String> getMapFilePathsFromDatabase() {
        List<String> filePaths = new ArrayList<>();
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                String query = "SELECT FilePath FROM SavedMaps";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String filePath = resultSet.getString("FilePath");
                    filePaths.add(filePath);
                }
                connection.close();
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
        return filePaths;
    }



}
