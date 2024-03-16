package com.example.temiv3;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements OnRobotReadyListener {



    @SuppressLint("NewApi")
    public Connection connectionclass() {
        Connection con = null;
        String ip = "172.16.0.115", port = "1433", username = "sa", password = "Oliwia123!@", databasename = "Temi";
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
            con = DriverManager.getConnection(connectionUrl);
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
        return con;
    }

    public static final String TAG = MainActivity.class.getSimpleName();
    public static Robot mRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize robot instance`
        mRobot = Robot.getInstance();

        // Initialize exit button
        final Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRobot.showAppList();
            }
        });

        TextView id = (TextView) findViewById(R.id.edittexid);
        TextView name = (TextView) findViewById(R.id.edittextname);
        TextView location = (TextView) findViewById(R.id.edittextlocation);
        TextView description = (TextView) findViewById(R.id.edittextdescription);
        Button btninsert = (Button) findViewById(R.id.btnadd);
//        Button btnupdate = (Button) findViewById(R.id.btnupdate);
//        Button btndelete = (Button) findViewById(R.id.btndelete);
//        Button btnget = (Button) findViewById(R.id.btnget);

        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection connection = connectionclass();
                try {
                    if (connection != null) {
                        String sqlinsert = "Insert into Garage values ('" + id.getText().toString() + "','" + name.getText().toString() + "','" + location.getText().toString() + "','" + description.getText().toString() + "')";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(sqlinsert);
                    }
                } catch (Exception exception) {
                    Log.e("Error", exception.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Add robot event listeners
        mRobot.addOnRobotReadyListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove robot event listeners
        mRobot.removeOnRobotReadyListener(this);
    }

    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                mRobot.onStart(activityInfo);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}