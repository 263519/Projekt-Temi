package com.example.temiv3;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con = null;
String ip,port,username,password,databasename;

    @SuppressLint("NewApi")
    public Connection connectionclass() {

         ip = "172.16.0.115";
        //ip = "192.168.52.149";
        //ip ="127.0.0.1";
     //   ip ="10.0.2.2";
         port = "1433";
        // port = "5222";
       // username = "temis";
         username = "sa";
         password = "Oliwia123!@";

         databasename = "Temi";
        // databasename = "temi";
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        //Log.e("SIEMAMMAENIU", "WIATAMAMMAM");
        try {
            //Log.e("RUTUTUTUTU", "TJTUTUTUTU");
          //  String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;


           // Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
           // String connectionUrl = "jdbc:mysql://172.16.0.115:3306/test";
            con = DriverManager.getConnection(connectionUrl);
            //con = DriverManager.getConnection(connectionUrl,"usa","Oliwia123!@");

        } catch (Exception exception) {
            Log.e("ErrorDatabase", exception.getMessage());
        }
        return con;
    }

}
