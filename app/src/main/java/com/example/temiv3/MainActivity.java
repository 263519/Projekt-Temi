package com.example.temiv3;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import androidx.core.view.ViewCompat;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;


import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;

import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.concurrent.CompletableFuture;




public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, OnGoToLocationStatusChangedListener, Robot.TtsListener, OnSequencePlayStatusChangedListener, OnLocationsUpdatedListener {





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

//    public String GetRowFromDB(){
//        String return_string;
//        Connection connection = null;
//        try {
//            ConnectionHelper connectionHelper = new ConnectionHelper();
//            connection = connectionHelper.connectionclass();
//            if (connection != null) {
//                String sqlselect = "Insert ";
//                Statement st = connection.createStatement();
//                ResultSet rs = st.executeQuery(sqlselect);
//            }
//        } catch (Exception exception) {
//            Log.e("Error", exception.getMessage());
//        }
//    return return_string;
//    }


    /////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

        // Add robot event listeners
        mRobot.addTtsListener(this);
        mRobot.addOnRobotReadyListener(this);
        mRobot.addOnGoToLocationStatusChangedListener(this);
        mRobot.addOnSequencePlayStatusChangedListener(this);
        mRobot.addOnLocationsUpdatedListener(this);
    }
    /////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();

        // Remove robot event listeners
        mRobot.removeOnSequencePlayStatusChangedListener(this);
        mRobot.removeTtsListener(this);
        mRobot.removeOnRobotReadyListener(this);
        mRobot.removeOnGoToLocationStatusChangedListener(this);
        mRobot.removeOnLocationsUpdateListener(this);


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

            List<String> locations = mRobot.getLocations();
            List<String> locations_withou_base = new ArrayList<>(locations.subList(1, locations.size()));
            goToLocations(locations_withou_base);

        }
    }



    private void speakOnArrival(String location, CompletableFuture<Void> future) {
        String text = "dymimyy" ;
        TtsRequest ttsRequest = TtsRequest.create(text, false);
        mRobot.speak(ttsRequest);


        mRobot.addTtsListener(new Robot.TtsListener() {
            @Override
            public void onTtsStatusChanged(TtsRequest ttsRequest) {
                if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
                    Button GoNext = (Button) findViewById(R.id.button_go_next);
                    GoNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            future.complete(null); // Oznacz CompletableFuture jako ukończony po zakończeniu wypowiedzi
                        }
                    });
                }
            }
        });
    }


    private void goToLocations(List<String> locations) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (String location : locations) {


            future = future.thenCompose((Void) -> {
                CompletableFuture<Void> goToFuture = new CompletableFuture<>();
                mRobot.addOnGoToLocationStatusChangedListener(new OnGoToLocationStatusChangedListener() {

                    @Override
                    public void onGoToLocationStatusChanged(@NonNull String location, @NonNull String status, int descriptionId, @NonNull String description) {

                        if (status.equals("complete")) {
                            Log.d(TAG, "Completed goTo: " + location);
                            mRobot.stopMovement(); // Zatrzymaj ruch robota po dotarciu do lokalizacji
                            speakOnArrival(location, goToFuture); // Wywołaj metodę mówienia, a następnie przekaż CompletableFuture dla kontynuacji

                            //goToFuture.complete(null);

                        }
                        if (status.equals("abort")) {
                            Button RetryButton = (Button) findViewById(R.id.button_retry);
                            RetryButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mRobot.goTo(location);
                                }
                            });
                            //goToFuture.complete(null);

                        }

                    }
                });

                mRobot.goTo(location);


                return goToFuture;
            });
        }

        future.thenRun(() -> {
            Log.d(TAG, "All goTo operations completed");

        });
    }


    @Override
    public void onGoToLocationStatusChanged(@NonNull String location, @NonNull String status, int  descriptionId, @NonNull String description) {

        Log.d(TAG, String.format("GoToStatusChanged: location=%s, status=%s, descriptionId=%d, description=%s", location, status, descriptionId, description));
        // mRobot.speak(TtsRequest.create(status, false));



    }

    @Override
    public void onTtsStatusChanged(@NonNull TtsRequest ttsRequest) {
        @NonNull String stat = ttsRequest.getStatus().name();
        Log.d(TAG, String.format("TTS Status: %s", stat));
    }

    @Override
    public void onSequencePlayStatusChanged(int i) {
        @NonNull String state= "" ;
        switch (i) {
            case 0:
                state = "Finishing playing";
                break;
            case 1:
                state = "Source preparing";
                break;
            case 2:
                state = "Playing";
                break;
            case -1:
                state = "Errors occurred while playing";
                break;
        }
        Log.d(TAG, String.format("SequenceStatus: %s ", state));
        //Log.d(TAG, String.format("SequenceStatusINT: %s ", i));
    }

    @Override
    public void onLocationsUpdated(@NonNull List<String> list) {
        for (String location : list) {
            Log.d("LocationsUpdated", "Location: " + location);
        }
    }
}