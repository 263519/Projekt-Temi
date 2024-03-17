package com.example.temiv3;

import android.util.Log;

import java.sql.Array;
import java.sql.Connection;
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
                     dtname.put("idList",rs.getString("ID"));
                     dtname.put("nameList",rs.getString("Name"));
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

}
