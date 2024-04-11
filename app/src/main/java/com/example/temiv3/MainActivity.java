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

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;


import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;


import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.permission.OnRequestPermissionResultListener;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.List;
import java.util.concurrent.CompletableFuture;




public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, OnGoToLocationStatusChangedListener, Robot.TtsListener, OnSequencePlayStatusChangedListener, OnLocationsUpdatedListener, Robot.AsrListener, OnDetectionStateChangedListener, OnCurrentPositionChangedListener, OnRequestPermissionResultListener {





    public static final String TAG = MainActivity.class.getSimpleName();
    public static Robot mRobot;

    public List<String> locationsRobot = mRobot.getLocations();

    ListItem listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize robot instance`
        mRobot = Robot.getInstance();
        mRobot.requestToBeKioskApp();

        Log.d(TAG, String.format("Permission: %s ",mRobot.checkSelfPermission(Permission.FACE_RECOGNITION)));
        mRobot.setKioskModeOn(true);
      //  mRobot.setDetectionModeOn(true, 2.0f);


        listItem = new ListItem();

        // Initialize exit button
        final Button buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRobot.showAppList();
            }
        });

        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
               // recreate(); // Apply the new theme
            }
        });






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
                       // String sqlinsert = "Insert into Garage values ('"+ location.getText().toString() + "','" + description.getText().toString() + "')";
                        String sqlinsert = "INSERT INTO Garage (Location, Description) VALUES ('" + location.getText().toString() + "','" + description.getText().toString() + "')";
                        saveLocation(location);
                        Statement st = connection.createStatement();
                        //ResultSet rs = st.executeQuery(sqlinsert);
                        int rowsAffected = st.executeUpdate(sqlinsert);
                        if (rowsAffected > 0) {
                            Log.d("Insertion", "Data inserted successfully");
                        } else {
                            Log.d("Insertion", "Failed to insert data");
                        }
                        connection.close();
                    }
                } catch (Exception exception) {
                    Log.e("Error", exception.getMessage());
                }
            }
        });
    }










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
        mRobot.addOnDetectionStateChangedListener(this);
        mRobot.addOnCurrentPositionChangedListener(this);
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
        mRobot.removeOnDetectionStateChangedListener(this);
        mRobot.removeOnCurrentPositionChangedListener(this);


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


        List<String> locations_withou_base = new ArrayList<>(locationsRobot.subList(1, locationsRobot.size()));


        //goToLocations(locations_withou_base);
        Button goButton = findViewById(R.id.gobutton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocations(locations_withou_base);
            }
        });

        Button goAllButton = findViewById(R.id.goAllbutton);
        goAllButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                goToLocations(locationsRobot);
            }
        });

        AddLocationToDatabase(locationsRobot);

    }
}
//////////////////
private void AddLocationToDatabase(List<String> locations){
    for (String location : locations){
    listItem.addLocation(location);
    }

}



    private void saveLocation(TextView textlocation) {
        String location = textlocation.getText().toString().toLowerCase().trim();
        boolean result = mRobot.saveLocation(location);
        if (result) {

            Log.d(TAG, String.format("I've successfully saved the %s", location));
        } else {

            Log.d(TAG, String.format("Saved the %s location failed", location));
        }

    }





/////////////////////
  /*  private void speakOnArrival(String location, CompletableFuture<Void> future) {
        String text = listItem.getDescriptionByLocation(location);

        TtsRequest ttsRequest = TtsRequest.create(text, false, TtsRequest.Language.PL_PL);
        Log.d(TAG, String.format("IS Detection mode ON1: %s ", mRobot.isDetectionModeOn()));
        mRobot.setDetectionModeOn(true);
        Log.d(TAG, String.format("IS KIOSK mode ON: %s ", mRobot.isSelectedKioskApp()));
        Log.d(TAG, String.format("IS Detection mode ON2: %s ", mRobot.isDetectionModeOn()));



            mRobot.addOnCurrentPositionChangedListener(new OnCurrentPositionChangedListener() {
                public void onCurrentPositionChanged(@NonNull Position position) {
                    Log.d(TAG, String.format("IS Detection mode ON2: %s ", mRobot.isDetectionModeOn()));
                    List<Object> RobotPos = new ArrayList<>();
                    float posX = position.getX(); // [m]
                    float posY = position.getY(); // [m]
                    float yaw = position.getYaw() ; // [deg] // krecenie
                    int tilt = position.getTiltAngle(); // [deg]

                    Position pose = new Position(posX, posY, yaw, tilt);
                    mRobot.goToPosition(pose);

                    if (mRobot.isDetectionModeOn()) {
                        Log.d(TAG, String.format("Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d ", posX, posY, yaw, tilt));

                    }


                }


            });
            mRobot.addOnDetectionStateChangedListener(new OnDetectionStateChangedListener() {
                @Override
                public void onDetectionStateChanged(int i) {
                    if(i == 2){
                        mRobot.setDetectionModeOn(false);
                    }

                }
            });






        mRobot.speak(ttsRequest);


        mRobot.addTtsListener(new Robot.TtsListener() {
            @Override
            public void onTtsStatusChanged(TtsRequest ttsRequest) {



                if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
                    future.complete(null); // Oznacz CompletableFuture jako ukończony po zakończeniu wypowiedzi
                }
            }
        });
    }*/


    private void speakOnArrival(String location, CompletableFuture<Void> future) {
        String text = listItem.getDescriptionByLocation(location);
       boolean isRotate = false;
        if(text == null){
            text = "Witam";
            isRotate = true;

        }
        final boolean isrotate = isRotate;
        TtsRequest ttsRequest = TtsRequest.create(text, false, TtsRequest.Language.PL_PL);
        Log.d(TAG, String.format("IS Detection mode ON1: %s ", mRobot.isDetectionModeOn()));

        mRobot.setDetectionModeOn(true);
        Log.d(TAG, String.format("IS KIOSK mode ON: %s ", mRobot.isSelectedKioskApp()));

        final int[] isFace = {0};
        mRobot.addOnDetectionStateChangedListener(new OnDetectionStateChangedListener() {
            @Override
            public void onDetectionStateChanged(int i) {
                  isFace[0] = i;
                if (i == 2) {
                    mRobot.setDetectionModeOn(false);
                    Log.d(TAG, String.format("IS Detection mode ON2: %s ", mRobot.isDetectionModeOn()));
                    mRobot.speak(ttsRequest);
                    mRobot.removeOnDetectionStateChangedListener(this);
                }
            }
        });

        mRobot.addOnCurrentPositionChangedListener(new OnCurrentPositionChangedListener() {
            public void onCurrentPositionChanged(@NonNull Position position) {
               // Log.d(TAG, String.format("IS Detection mode ON2: %s ", mRobot.isDetectionModeOn()));
                List<Object> RobotPos = new ArrayList<>();
                float posX = position.getX(); // [m]
                float posY = position.getY(); // [m]
                float yaw =position.getYaw() ; // [deg] // krecenie
                int tilt = position.getTiltAngle(); // [deg]

                //Position pose = new Position(posX, posY, yaw, tilt);



                  if(isFace[0] != 2 ){
                    Log.d(TAG, String.format("Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d FaceID=%d", posX, posY, yaw, tilt ,isFace[0]));

                     // mRobot.goToPosition(pose);
                     // mRobot.stopMovement();


                    mRobot.removeOnCurrentPositionChangedListener(this);

                  }
            }
        });

        mRobot.addTtsListener(new Robot.TtsListener() {
            @Override
            public void onTtsStatusChanged(TtsRequest ttsRequest ) {
                if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED ) {
                    future.complete(null); // Oznacz CompletableFuture jako ukończony po zakończeniu wypowiedzi
                }
            }
        });
    }

    ///////////////////////////
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

/////////////////////////////
    @Override
    public void onGoToLocationStatusChanged(@NonNull String location, @NonNull String status, int  descriptionId, @NonNull String description) {

        Log.d(TAG, String.format("GoToStatusChanged: location=%s, status=%s, descriptionId=%d, description=%s", location, status, descriptionId, description));
       // mRobot.speak(TtsRequest.create(status, false));



    }
///////////////////////
    @Override
    public void onTtsStatusChanged(@NonNull TtsRequest ttsRequest) {
        @NonNull String stat = ttsRequest.getStatus().name();
        Log.d(TAG, String.format("TTS Status: %s", stat));
    }
/////////////////////////
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
/////////////////////////////////
    @Override
    public void onLocationsUpdated(@NonNull List<String> list) {
        for (String location : list) {
            Log.d("LocationsUpdated", "Location: " + location);
        }
    }
///////////////////////////////////////
    @Override
    public void onAsrResult(@NonNull String s) {

    }

    @Override
    public void onDetectionStateChanged(int i) {
        @NonNull String state= "" ;
        switch (i) {
            case 0:
                state = "No active detection occurring and 4 seconds have passed since last detection was lost";
                break;
            case 1:
                state = "Target detected lost";
                break;
            case 2:
                state = "Human body is detected";
                break;


        }
        Log.d(TAG, String.format("Detected status: %s ", state));
    }

    @Override
    public void onCurrentPositionChanged(@NonNull Position position) {
        List<Object> i = new ArrayList<>();
        i.add(position.getX());
        i.add(position.getY());
        i.add(position.getYaw());
        i.add(position.getTiltAngle());
        if(mRobot.isDetectionModeOn()) {
            Log.d(TAG, String.format("Generall Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d ", (float) i.get(0), (float) i.get(1), (float) i.get(2), (int) i.get(3)));
        }

    }

    @Override
    public void onRequestPermissionResult(@NonNull Permission permission, int i, int i1) {

    }

    ///////////////////////////////////
}