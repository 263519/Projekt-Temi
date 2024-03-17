package com.example.temiv3;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;





public class MainActivity extends AppCompatActivity implements OnRobotReadyListener {





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
        Button Listbutton = (Button) findViewById((R.id.listbutton)) ;
//        Button btnupdate = (Button) findViewById(R.id.btnupdate);
//        Button btndelete = (Button) findViewById(R.id.btndelete);
//        Button btnget = (Button) findViewById(R.id.btnget);



        Listbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataList.class);
                startActivity(intent);

            }
        });
////////////////////////////////////
        btninsert.setOnClickListener(new View.OnClickListener() {
            Connection connection = null;
            @Override
            public void onClick(View v) {

                try {
                    ConnectionHelper connectionHelper = new ConnectionHelper();
                    connection = connectionHelper.connectionclass();
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






    public void GetList(View v){
        SimpleAdapter ad;
        setContentView(R.layout.datalistlayout);
        TableLayout tab = (TableLayout) findViewById(R.id.tableLayout);

        List<Map<String,String>> MyDataList=null;
        ListItem MyData = new ListItem();
        MyDataList = MyData.getlist();

        String[] Fromw={"idList","nameList","locList","desList"};
        int[] Tow={R.id.idList,R.id.nameList,R.id.locList,R.id.desList};
        ad = new SimpleAdapter(MainActivity.this,MyDataList,R.layout.datalistlayout,Fromw,Tow);
        for (int i = 0; i < ad.getCount(); i++) {
            View itemView = ad.getView(i, null, tab);
            tab.addView(itemView);
        }
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setVisibility(View.VISIBLE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }



/////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

        // Add robot event listeners
        mRobot.addOnRobotReadyListener(this);
    }
/////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();

        // Remove robot event listeners
        mRobot.removeOnRobotReadyListener(this);
    }
//////////////////////////////
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