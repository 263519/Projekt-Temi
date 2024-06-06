package com.example.temiv3;

import static com.example.temiv3.MainActivity.TAG;



/*
import android.content.Context;
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

    public interface DescriptionCallback {
        void onDescriptionReceived(String description);
    }

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


    public List<Map<String,String>>getCurrentMapList()
    {
        List<Map<String,String>> data = null;
        data = new ArrayList<Map<String,String>>();


        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if (connection != null) {
                // Znajdź MapID na podstawie MapName w tabeli SavedMaps
                String mapQuery = "SELECT MapID FROM SavedMaps WHERE MapName = ?";
                PreparedStatement mapStatement = connection.prepareStatement(mapQuery);
                mapStatement.setString(1, MainActivity.MapName);
                ResultSet mapResultSet = mapStatement.executeQuery();

                if (mapResultSet.next()) {
                    int mapID = mapResultSet.getInt("MapID");

                    // Pobierz dane z tabeli Garage na podstawie MapID
                    String sqlSelect = "SELECT Location, Description FROM Garage WHERE MapID = ?";
                    PreparedStatement statement = connection.prepareStatement(sqlSelect);
                    statement.setInt(1, mapID);
                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        Map<String, String> dtname = new HashMap<>();
                        dtname.put("locList", rs.getString("Location"));
                        dtname.put("desList", rs.getString("Description"));
                        data.add(dtname);
                    }

                    ConnectionResult = "Success";
                    isSuucess = true;
                } else {
                    ConnectionResult = "MapName not found";
                    isSuucess = false;
                }

                connection.close();
            } else {
                ConnectionResult = "Failed";
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }

        return data;
    }


//    public boolean addLocation(String location,String description) {
//        try {
//            ConnectionHelper connectionHelper = new ConnectionHelper();
//            connection = connectionHelper.connectionclass();
//            if (connection != null) {
//                // Sprawdź, czy lokalizacja już istnieje w bazie danych
//                String checkQuery = "SELECT COUNT(*) AS count FROM Garage WHERE Location = ?";
//                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
//                checkStatement.setString(1, location);
//                ResultSet resultSet = checkStatement.executeQuery();
//                resultSet.next();
//                int count = resultSet.getInt("count");
//                if (count > 0) {
//                    Log.d("Add Location", "Location already exists in the database");
//                    connection.close();
//                    return false; // Lokalizacja już istnieje, nie dodawaj ponownie
//                }
//
//                // Dodaj lokalizację do bazy danych
//                //String sqlInsert = "INSERT INTO Garage (Location) VALUES (?)";
//                String sqlInsert = "INSERT INTO Garage (Location, Description) VALUES (?, ?)";
//                PreparedStatement statement = connection.prepareStatement(sqlInsert);
//                statement.setString(1, location);
//                statement.setString(2, description);
//                int rowsAffected = statement.executeUpdate();
//
//                connection.close();
//                return true;
//            }
//        } catch (Exception exception) {
//
//            Log.e("Error", exception.getMessage());
//            return false;
//        }
//        return false;
//    }



    public boolean addLocation(String location,String description,String mapName) {
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

                // Znajdz MapID po mapName w  SavedMaps
                String mapQuery = "SELECT MapID FROM SavedMaps WHERE MapName = ?";
                PreparedStatement mapStatement = connection.prepareStatement(mapQuery);
                mapStatement.setString(1, mapName);
                ResultSet mapResultSet = mapStatement.executeQuery();

                if (mapResultSet.next()) {
                    int mapID = mapResultSet.getInt("MapID");

                    // Dodaj lokalizację do bazy danych
                    String sqlInsert = "INSERT INTO Garage (Location, Description, MapID) VALUES (?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sqlInsert);
                    statement.setString(1, location);
                    statement.setString(2, description);
                    statement.setInt(3, mapID);

                    int rowsAffected = statement.executeUpdate();

                    connection.close();
                    return rowsAffected > 0;
                } else {
                    Log.d("Add Location", "MapName not found in the database");
                    connection.close();
                    return false;
                }
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



}*/



import android.os.AsyncTask;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import okhttp3.Request;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListItem {


    public boolean addLocation(final String location, final String description, final String mapName) {
        final OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("location", location)
                .add("mapName", mapName);

        // Sprawdź, czy description nie jest null
        if (description != null) {
            formBuilder.add("description", description);
        }

        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/addlocation.php")
                .post(formBody)
                .build();

        final boolean[] result = new boolean[1];

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("AddLocation", "Response: " + responseBody);
                        result[0] = "Location added successfully".equals(responseBody);
                    } else {
                        Log.e("AddLocation", "Error: " + response.code() + " " + response.message());
                        result[0] = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result[0] = false;
                }
            }
        });

        thread.start();

        try {
            thread.join(); // Czekaj na zakończenie wątku
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result[0];
    }

    public String getDescriptionByLocation(String location) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("location", location)
                .build();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/getdescriptionbylocation.php")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return "Error: " + response.code() + " " + response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Network error";
        }

    }


    public void updateDescriptionInDatabase(String location, String newDescription) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("location", location)
                .add("newDescription", newDescription)
                .build();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/updatedescription.php")
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("UpdateDatabase", responseBody + " rows affected");
                    } else {
                        Log.e("UpdateDatabase", "Error: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("UpdateDatabase", "Network error: " + e.getMessage());
                }
            }
        }).start();
    }


    public List<String> getMapFilePathsFromDatabase() {
        OkHttpClient client = new OkHttpClient();
        List<String> filePaths = new ArrayList<>();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/getmapfilepaths.php")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // Podziel odpowiedź na pojedyncze ścieżki za pomocą przecinków
                String[] pathsArray = responseBody.split(",");
                // Dodaj każdą ścieżkę do listy
                Collections.addAll(filePaths, pathsArray);
            } else {
                Log.e("GetMapFilePaths", "Error: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("GetMapFilePaths", "Error: " + e.getMessage());
        }

        return filePaths;
    }

    public void saveMapUrltoDatabase(String mapName, String filePath) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("MapName", mapName)
                .add("filepath", filePath)
                .build();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/savemapurl.php")
                .post(formBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("SaveMapUrl", responseBody);
                    } else {
                        Log.e("SaveMapUrl", "Error: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("SaveMapUrl", "Error: " + e.getMessage());
                }
            }
        }).start();
    }



    public List<Map<String, String>> getlist() {
        OkHttpClient client = new OkHttpClient();
        List<Map<String, String>> data = new ArrayList<>();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/getlist.php")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONArray jsonArray = new JSONArray(responseBody);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String location = jsonObject.getString("locList");
                    String description = jsonObject.getString("desList");

                    Map<String, String> rowData = new HashMap<>();
                    rowData.put("locList", location);
                    rowData.put("desList", description);
                    data.add(rowData);
                }
            } else {
                Log.e("GetList", "Error: " + response.code() + " " + response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("GetList", "Error: " + e.getMessage());
        }

        return data;
    }



    public List<Map<String, String>> getCurrentMapList() {
        List<Map<String, String>> MyDataList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("MapName", MainActivity.MapName)
                .build();

        Request request = new Request.Builder()
                .url("https://temi.nokiagarage.pl/Koty/SimpleApi/get_current_map_list.php")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONArray jsonArray = new JSONArray(responseData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String, String> itemData = new HashMap<>();
                    itemData.put("locList", jsonObject.getString("locList"));
                    itemData.put("desList", jsonObject.getString("desList"));
                    MyDataList.add(itemData);
                }
            } else {
                Log.e("DataList", "Server response: " + response.code());
            }
        } catch (IOException | JSONException e) {
            Log.e("DataList", "Error: " + e.getMessage());
        }

        return MyDataList;
    }








}
