package com.example.temiv3;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.assist.AssistStructure;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;


import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.map.Layer;

import com.robotemi.sdk.map.MapDataModel;
import com.robotemi.sdk.map.MapImage;
import com.robotemi.sdk.map.MapInfo;
import com.robotemi.sdk.map.MapModel;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.permission.OnRequestPermissionResultListener;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, OnGoToLocationStatusChangedListener, Robot.TtsListener, OnSequencePlayStatusChangedListener, OnLocationsUpdatedListener, Robot.AsrListener, OnDetectionStateChangedListener, OnCurrentPositionChangedListener, OnRequestPermissionResultListener {


    private static final String AUTHORITY = "com.example.temiv3.provider";


    public static final String TAG = MainActivity.class.getSimpleName();
    public static Robot mRobot;
    public static String MapName;

    ListItem listItem;
    boolean isDetectionModeOn = false;
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
            }
        });


        Switch switchDetectionMode = findViewById(R.id.switchDetection);
        switchDetectionMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDetectionModeOn = true;

                    Log.i("Detection", String.format("isDetectionModeOn: %s",  isDetectionModeOn));

                } else {
                    isDetectionModeOn = false;
                    Log.i("Detection", String.format("isDetectionModeOF: %s",  isDetectionModeOn));
                }

            }


        });

        Button Listbutton = (Button) findViewById((R.id.listbutton)) ;
        Button AddLocation = (Button) findViewById(R.id.AddLoc);

        AddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddLocationActivity.class);
                startActivity(intent);
            }
        });



        Listbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataList.class);
                startActivity(intent);

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
           // mRobot.loadMap("6621663ae0b1ed18993bb54a");
           // mRobot.loadMap("66216ad6cc86bca190d07f69");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

       List<MapModel> maplist = mRobot.getMapList();
        for(MapModel map : maplist){
            String name = map.getName();
            String id = map.getId();
            Log.i("Maplist", String.format("MapID: %s", id));
            Log.i("Maplist", String.format("MapName: %s", name));


        }
        //
       // getMap("test",maplist);

        MapDataModel map = mRobot.getMapData();
        assert map != null;
        MapName = map.getMapName();


       // String mapId = map.component2();
      // Log.i("MapData", String.format("MapID: %s",  map.getMapName()));
       // Log.i("MapData", String.format("MapName: %s",  map.getMapName()));

        Button buttonBackupMap = findViewById(R.id.buttonBackupMap);
        buttonBackupMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParcelFileDescriptor parcelFileDescriptor;
                parcelFileDescriptor = Robot.getInstance().getCurrentMapBackupFile(true);
                if (parcelFileDescriptor == null) return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File dir = new File(getApplicationContext().getExternalFilesDir(null), "maps");
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String mapName = mRobot.getMapData().getMapName();
                        File file = new File(dir, "map-" + mapName + ".tar.gz");
                        try {
                            file.createNewFile();
                            listItem.saveMapUrltoDatabase(mapName,file.getPath());
                            ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
                            FileOutputStream output = new FileOutputStream(file);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                output.write(buffer, 0, length);
                            }
                            inputStream.close();
                            output.close();

                            if (file.length() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "File generated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        //////////////////////////////
        Button buttonLoadMapFromPrivateFile = findViewById(R.id.buttonLoadMapFromPrivateFile);
        buttonLoadMapFromPrivateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveCurrentMap();

                // First declare FileProvider in AndroidManifest.

                // The folder needs to be declared in res/xml/provider_paths.xml
                // <files-path name="map_internal_file" path="maps/" />
                File internalMapDirectory = new File(getFilesDir(), "maps");
                Log.i("load", String.format("loadInternal: %s",internalMapDirectory));
                // The folder needs to be declared in res/xml/provider_paths.xml
                // <external-files-path name="map_external_file" path="maps/"/>
                File externalMapDirectory = new File(getExternalFilesDir(null), "maps");
                Log.i("load", String.format("loadexternal: %s",externalMapDirectory));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File[] internalMapFiles = internalMapDirectory.listFiles();
                        File[] externalMapFiles = externalMapDirectory.listFiles();
                        List<String> filePaths = listItem.getMapFilePathsFromDatabase();

                        // Utwórz listę plików z plików wewnętrznych, zewnętrznych i z bazy danych
                        List<File> files = new ArrayList<>();
                        if (internalMapFiles != null) {
                            files.addAll(Arrays.asList(internalMapFiles));
                        }
                      /*  if (externalMapFiles != null) {
                            files.addAll(Arrays.asList(externalMapFiles));
                        }*/
                        for (String filePath : filePaths) {
                            files.add(new File(filePath));
                        }


                        files = files.stream()
                                .filter(file -> file.isFile() && file.getPath().toLowerCase().endsWith("tar.gz"))
                                .collect(Collectors.toList());

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        if (!files.isEmpty()) {
                            CharSequence[] fileNames = new CharSequence[files.size()];
                            for (int i = 0; i < files.size(); i++) {
                                fileNames[i] = files.get(i).getPath();
                            }

                            List<File> finalFiles = files;
                           // List<File> finalFiles = filesPaths;
                            builder.setItems(fileNames, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File fileSelected = finalFiles.get(which);
                                            Log.d("SDK-Sample", "Map file selected " + fileSelected.getPath());

                                            Uri uri = FileProvider.getUriForFile(MainActivity.this, AUTHORITY, fileSelected);
                                            loadMap(uri);


                                        }
                                    }).setTitle("Select one map file to load")
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            builder.setTitle("No map backup files found")
                                    .setMessage("This sample takes map files from\n/sdcard/Android/data/com.robotemi.sdk.sample/files/maps/\nand /data/data/com.robotemi.sdk.sample/files/maps/")
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                builder.show();
                            }
                        });
                    }
                }).start();
            }
        });


//////////////////////////////////////
        //goToLocations(locations_withou_base);
        Button goButton = findViewById(R.id.gobutton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (intent != null) {
                    // Pobieramy listę nazw zaznaczonych checkboxów
                    ArrayList<String> checkedCheckboxLocation = getIntent().getStringArrayListExtra("checkedCheckboxNames");

                    if (checkedCheckboxLocation != null) {
                        for(String name : checkedCheckboxLocation) {
                            Log.d("CheckedBox MAIN", String.format("Point  Main %s", name));
                        }
                        Toast.makeText(getBaseContext(), "Go !", Toast.LENGTH_SHORT).show();
                        goToLocations(checkedCheckboxLocation);
                }else {
                        Toast.makeText(getBaseContext(), "Not Added locations !", Toast.LENGTH_SHORT).show();
                    }
                }
               //List<String> locations = mRobot.getLocations();
               // List<String> locations_withou_base = new ArrayList<>(locations.subList(1, locations.size()));
                //goToLocations(locations_withou_base);
            }
        });
///////////////////////////////////////////
        Button goAllButton = findViewById(R.id.goAllbutton);
        goAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> locations = mRobot.getLocations();

                goToLocations(locations);
            }
        });
        List<String> loc = mRobot.getLocations();
        AddLocationToDatabase(loc);

    }

}
///////////////////////////////////////
    private void saveCurrentMap() {
        ParcelFileDescriptor parcelFileDescriptor = Robot.getInstance().getCurrentMapBackupFile(true);
        if (parcelFileDescriptor == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(getApplicationContext().getExternalFilesDir(null), "maps");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                String mapName = mRobot.getMapData().getMapName();
                File file = new File(dir, "map-" + mapName + ".tar.gz");
                try {
                    file.createNewFile();
                    ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
                    FileOutputStream output = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    inputStream.close();
                    output.close();

                    if (file.length() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Current map saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


//////////////////////////////////////////////
    private void loadMap(Uri uri) {
        AssistStructure.ViewNode checkBoxLoadMapWithRepose = null;
        boolean reposeRequired = true;
        AssistStructure.ViewNode checkBoxLoadMapWithoutUI = null;
        boolean withoutUI = true;
        Position position = null;
        AssistStructure.ViewNode checkBoxLoadMapFromPose = null;
       // if (checkBoxLoadMapFromPose.isChecked()) {
            position = new Position();
       // }

        mRobot.loadMapWithBackupFile(
                uri,
                reposeRequired,
                position,
                withoutUI
        );
    }
////////////////////////////////////////////////
    private void getMap(String name, List<MapModel> maplist)
    {
        Log.d(TAG, String.format("MapName %s", maplist.size()));

        for(MapModel mp : maplist) {
            if(mp.getName().equals(name)) {
                MapDataModel map = mRobot.getMapData();
                MapImage mapImage = map.getMapImage();
                String mapID = map.getMapId();
                MapInfo mapInfo = map.getMapInfo();
                List<Layer> virtualWalls = map.getVirtualWalls();
                List<Layer> greenPaths = map.getGreenPaths();
                List<Layer> locations = map.getLocations();
                String mapName = map.getMapName();
                Log.d(TAG, String.format("MapName %s", mapName));
                Log.d(TAG, String.format("MapID %s", mapID));

            }
            Log.d(TAG, String.format("MapName %s", mp.getName()));

        }
        Log.d(TAG, String.format("MapName %s", "mp.getName()"));


    }


//////////////////
private void AddLocationToDatabase(List<String> locations){
    for (String location : locations){
    listItem.addLocation(location,null,MapName);
    }

}


/////////////////////////////////


    private void speakOnArrival(String location, CompletableFuture<Void> future) {
        String text = listItem.getDescriptionByLocation(location);
       boolean isRotate = false;
        if(text == null){
            text = "Witam";
            isRotate = true;

        }
        final boolean isrotate = isRotate;
        TtsRequest ttsRequest = TtsRequest.create(text, false, TtsRequest.Language.PL_PL);

        Log.i(TAG, String.format("Detection mode is %s", isDetectionModeOn ? "ON" : "OFF"));

        if(isDetectionModeOn){
        Log.d(TAG, String.format("IS Detection mode ON1: %s ", mRobot.isDetectionModeOn()));
        mRobot.setDetectionModeOn(true);
        Log.d(TAG, String.format("IS KIOSK mode ON: %s ", mRobot.isSelectedKioskApp()));
        Log.d(TAG, String.format("IS Detection mode ON3: %s ", mRobot.isDetectionModeOn()));
       // mRobot.speak(ttsRequest);
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


                  if(isFace[0] != 2 ){
                    Log.d(TAG, String.format("Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d FaceID=%d", posX, posY, yaw, tilt ,isFace[0]));

                     // mRobot.goToPosition(pose);
                     // mRobot.stopMovement();


                    mRobot.removeOnCurrentPositionChangedListener(this);

                  }
            }
        });
        }else {
                    mRobot.speak(ttsRequest);

        }

        mRobot.addTtsListener(new Robot.TtsListener() {
            @Override
            public void onTtsStatusChanged(TtsRequest ttsRequest ) {
                if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED ) {

                    Button GoNext = (Button) findViewById(R.id.button_go_next);
                    GoNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            future.complete(null); // Oznacz CompletableFuture jako ukończony po zakończeniu wypowiedzi
                        }
                    });
                    //future.complete(null); // Oznacz CompletableFuture jako ukończony po zakończeniu wypowiedzi
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