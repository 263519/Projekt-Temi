package com.example.temiv3;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.assist.AssistStructure;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


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
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.listener.OnReposeStatusChangedListener;
import com.robotemi.sdk.navigation.model.Position;
import com.robotemi.sdk.permission.OnRequestPermissionResultListener;
import com.robotemi.sdk.permission.Permission;
import com.robotemi.sdk.sequence.OnSequencePlayStatusChangedListener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, OnGoToLocationStatusChangedListener, Robot.TtsListener, OnSequencePlayStatusChangedListener, OnLocationsUpdatedListener, Robot.AsrListener, OnDetectionStateChangedListener, OnCurrentPositionChangedListener, OnRequestPermissionResultListener, OnReposeStatusChangedListener {


    private static final String AUTHORITY = "com.example.temiv3.provider";


    public static final String TAG = MainActivity.class.getSimpleName();
    public static Robot mRobot;
    public static String MapName;
    private ProgressBar progressBar;
    private FrameLayout progressOverlay;

    private ImageView imageView;

    private Button goButton ;
    private Button goAllButton;



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
                mRobot.setKioskModeOn(false);
                mRobot.showAppList();
                finish();
               // System.exit(0);
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
        progressBar = findViewById(R.id.progressBar);
        //nprogressBar.setVisibility(View.GONE);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressBar.setVisibility(View.VISIBLE);
        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

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
                finish();
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
        mRobot.addOnReposeStatusChangedListener(this);
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
        mRobot.removeOnReposeStatusChangedListener(this);


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
                buttonLoadMapFromPrivateFile.setEnabled(false); // Disable the button

                CompletableFuture<Void> saveMapFuture = new CompletableFuture<>();
                saveCurrentMap(saveMapFuture);

                saveMapFuture.thenRun(() -> {
                    runOnUiThread(() -> progressOverlay.setVisibility(View.GONE));
                    // This code will run after saveCurrentMap is complete
                    mRobot.addOnLoadMapStatusChangedListener(new OnLoadMapStatusChangedListener() {
                        private Handler handler = new Handler(Looper.getMainLooper());
                        private Runnable timeoutRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.i("MapStatus", "Status: TIMEOUT");
                                progressOverlay.setVisibility(View.GONE);
                            }
                        };

                        @Override
                        public void onLoadMapStatusChanged(int status, @NonNull String s) {
                            switch (status) {
                                case 0:
                                    Log.i("MapStatus", "Status: COMPLETE");
                                    handler.removeCallbacks(timeoutRunnable);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressOverlay.setVisibility(View.GONE);
                                        }
                                    });
                                    mRobot.removeOnLoadMapStatusChangedListener(this);
                                    MapDataModel map = mRobot.getMapData();
                                    assert map != null;
                                    MapName = map.getMapName();
                                    break;
                                case 1:
                                    Log.i("MapStatus", "Status: START");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressOverlay.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    handler.postDelayed(timeoutRunnable, 1 * 60 * 1000); // Set timeout to 1 minute
                                    break;
                                // Handle other statuses...
                                default:
                                    Log.i("MapStatus", "Status: " + status);
                                    break;
                            }
                        }
                    });

                    // First declare FileProvider in AndroidManifest.

                    // The folder needs to be declared in res/xml/provider_paths.xml
                    // <files-path name="map_internal_file" path="maps/" />
                    File internalMapDirectory = new File(getFilesDir(), "maps");
                    Log.i("load", String.format("loadInternal: %s", internalMapDirectory));
                    // The folder needs to be declared in res/xml/provider_paths.xml
                    // <external-files-path name="map_external_file" path="maps/"/>
                    File externalMapDirectory = new File(getExternalFilesDir(null), "maps");
                    Log.i("load", String.format("loadexternal: %s", externalMapDirectory));

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
                                    AlertDialog dialog = builder.show();
                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            buttonLoadMapFromPrivateFile.setEnabled(true); // Re-enable the button
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                }).exceptionally(throwable -> {
                    // Handle exceptions if saveCurrentMap failed
                    throwable.printStackTrace();
                    buttonLoadMapFromPrivateFile.setEnabled(true); // Re-enable the button in case of an error
                    return null;
                });
            }
        });

//////////////////////////////////////
        //goToLocations(locations_withou_base);
        goButton = findViewById(R.id.gobutton);
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
                goButton.setEnabled(false);
                goAllButton.setEnabled(false);
            }
        });
///////////////////////////////////////////
        goAllButton = findViewById(R.id.goAllbutton);
        goAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> locations = mRobot.getLocations();

                goToLocations(locations);
                goButton.setEnabled(false);
                goAllButton.setEnabled(false);
            }
        });
        List<String> loc = mRobot.getLocations();
        AddLocationToDatabase(loc);

    }

}

private void saveCurrentMap(CompletableFuture<Void> future) {
    ParcelFileDescriptor parcelFileDescriptor = Robot.getInstance().getCurrentMapBackupFile(true);
    if (parcelFileDescriptor == null) {
        future.complete(null); // Signal completion if there's nothing to save
        return;
    }

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
                future.complete(null); // Signal completion after saving the map
            } catch (IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e); // Signal exception if an error occurs
            }
        }
    }).start();
    progressOverlay.setVisibility(View.VISIBLE);

}

//////////////////////////////////////////////
    private void loadMap(Uri uri) {

        AssistStructure.ViewNode checkBoxLoadMapWithRepose = null;
        boolean reposeRequired = true;
        Log.d(TAG, String.format("Map Loading %s", reposeRequired));
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
        Log.d(TAG, String.format("Map Loaded %s", reposeRequired));
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
//private void speakOnArrival(String location, CompletableFuture<Void> future) {
//    String text = null;
//
//    final CountDownLatch latch = new CountDownLatch(1);
//    final String[] call = {null};
//
//    new Thread(new Runnable() {
//        @Override
//        public void run() {
//            Log.i(TAG, String.format("MAPNAME : %s ", MapName));
//            call[0] = listItem.getDescriptionByLocation(location, MapName);
//            latch.countDown();
//        }
//    }).start();
//
//    try {
//        latch.await();
//        text = call[0];
//        Log.e("call", "call " + text);
//    } catch (InterruptedException e) {
//        e.printStackTrace();
//    }
//
//    boolean isRotate = false;
//    if (text == null) {
//        text = "Witam";
//        isRotate = true;
//    }
//
//    final boolean isrotate = isRotate;
//    TtsRequest ttsRequest = TtsRequest.create(text, false, TtsRequest.Language.PL_PL);
//
//    Log.i(TAG, String.format("Detection mode is %s", isDetectionModeOn ? "ON" : "OFF"));
//
//    if (isDetectionModeOn) {
//        Log.d(TAG, String.format("IS Detection mode ON1: %s ", mRobot.isDetectionModeOn()));
//        mRobot.setDetectionModeOn(true);
//        Log.d(TAG, String.format("IS KIOSK mode ON: %s ", mRobot.isSelectedKioskApp()));
//        Log.d(TAG, String.format("IS Detection mode ON3: %s ", mRobot.isDetectionModeOn()));
//        final int[] isFace = {0};
//        mRobot.addOnDetectionStateChangedListener(new OnDetectionStateChangedListener() {
//            @Override
//            public void onDetectionStateChanged(int i) {
//                isFace[0] = i;
//                if (i == 2) {
//                    mRobot.setDetectionModeOn(false);
//                    Log.d(TAG, String.format("IS Detection mode ON2: %s ", mRobot.isDetectionModeOn()));
//                    mRobot.speak(ttsRequest);
//                    mRobot.removeOnDetectionStateChangedListener(this);
//                }
//            }
//        });
//
//        mRobot.addOnCurrentPositionChangedListener(new OnCurrentPositionChangedListener() {
//            public void onCurrentPositionChanged(@NonNull Position position) {
//                List<Object> RobotPos = new ArrayList<>();
//                float posX = position.getX();
//                float posY = position.getY();
//                float yaw = position.getYaw();
//                int tilt = position.getTiltAngle();
//
//                if (isFace[0] != 2) {
//                    Log.d(TAG, String.format("Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d FaceID=%d", posX, posY, yaw, tilt, isFace[0]));
//                    mRobot.removeOnCurrentPositionChangedListener(this);
//                }
//            }
//        });
//    } else {
//        mRobot.speak(ttsRequest);
//    }
//
//    mRobot.addTtsListener(new Robot.TtsListener() {
//        @Override
//        public void onTtsStatusChanged(TtsRequest ttsRequest) {
//            if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
//                Button GoNext = findViewById(R.id.button_go_next);
//                GoNext.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        future.complete(null);
//                    }
//                });
//            }
//        }
//    });
//}
//ImageView imageView = findViewById(R.id.imageView);
private void displayImage(String imageID) {
    String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.temiv3/files/images/" + imageID + ".jpg";
    File imgFile = new File(imagePath);
    Log.e("Image", "PATH:" + imagePath);
    if (imgFile.exists()) {
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        // Display the image
        imageView.setImageBitmap(myBitmap);
        imageView.setVisibility(View.VISIBLE);
        Log.e("Image", "Image file exists");
    } else {
        // If the file doesn't exist, display the default image
        String defaultImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.temiv3/files/images/nokia.jpg";
        File defaultImgFile = new File(defaultImagePath);
        if (defaultImgFile.exists()) {
            Bitmap defaultBitmap = BitmapFactory.decodeFile(defaultImgFile.getAbsolutePath());
            imageView.setImageBitmap(defaultBitmap);
        } else {
            // Optionally, handle the case where the default image doesn't exist
            Log.e("Image", "Default image file doesn't exist");
        }

        imageView.setVisibility(View.VISIBLE);
        Log.e("Image", "Image file doesn't exist");
    }

    // Set black background around the image
    imageView.setBackgroundColor(Color.BLACK);
}

    private void speakOnArrival(String location, CompletableFuture<Void> future) {
    String text = null;
    String imageID = null;
    String result = null;


    final CountDownLatch latch = new CountDownLatch(1);
    final String[] call = {null};

    new Thread(new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, String.format("MAPNAME : %s ", MapName));
            call[0] = listItem.getDescriptionAndIdByLocation(location, MapName);
            latch.countDown();
        }
    }).start();

    try {
        latch.await();
        result = call[0];
        Log.e("callTEXT", "resoult: " + result);
        String[] parts = result.split("\\|");
        if (parts.length == 2) {
            text = parts[0]; // Przypisanie opisu
            imageID = parts[1]; // Przypisanie ID obrazu
        }
        displayImage(imageID);
        Log.e("callTEXT", "text: " + text);
        Log.e("callTEXT", "imageID: " + imageID);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    boolean isRotate = false;
    if (text == null) {
        text = "Witam";
        isRotate = true;
    }

    final boolean isrotate = isRotate;
    TtsRequest ttsRequest = TtsRequest.create(text, false, TtsRequest.Language.PL_PL);

    Log.i(TAG, String.format("Detection mode is %s", isDetectionModeOn ? "ON" : "OFF"));

    if (isDetectionModeOn) {
        Log.d(TAG, String.format("IS Detection mode ON1: %s ", mRobot.isDetectionModeOn()));
        mRobot.setDetectionModeOn(true);
        Log.d(TAG, String.format("IS KIOSK mode ON: %s ", mRobot.isSelectedKioskApp()));
        Log.d(TAG, String.format("IS Detection mode ON3: %s ", mRobot.isDetectionModeOn()));
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
                List<Object> RobotPos = new ArrayList<>();
                float posX = position.getX();
                float posY = position.getY();
                float yaw = position.getYaw();
                int tilt = position.getTiltAngle();

                if (isFace[0] != 2) {
                    Log.d(TAG, String.format("Current Pose: X=%f, Y=%f, Yaw=%f, TiltAngle=%d FaceID=%d", posX, posY, yaw, tilt, isFace[0]));
                    mRobot.removeOnCurrentPositionChangedListener(this);
                }
            }
        });
    } else {
        mRobot.speak(ttsRequest);
    }

    mRobot.addTtsListener(new Robot.TtsListener() {
        @Override
        public void onTtsStatusChanged(TtsRequest ttsRequest) {
            if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
              //  setContentView(R.layout.activity_main);
                //viewSwitcher.showNext();
                imageView.setVisibility(View.GONE);
                Button GoNext = findViewById(R.id.button_go_next);
                GoNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        future.complete(null);
                    }
                });
            }
        }
    });
}


    private void goToLocations(List<String> locations) {
        for (String location : locations) {
            Log.d("Complete locations", location);
        }

        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (String location : locations) {
            future = future.thenCompose((Void) -> {
                CompletableFuture<Void> goToFuture = new CompletableFuture<>();
                OnGoToLocationStatusChangedListener listener = new OnGoToLocationStatusChangedListener() {

                    @Override
                    public void onGoToLocationStatusChanged(@NonNull String loc, @NonNull String status, int descriptionId, @NonNull String description) {
                        if (status.equals("complete")) {
                            Log.d(TAG, "Completed goTo: " + loc);
                            mRobot.stopMovement();
                            mRobot.removeOnGoToLocationStatusChangedListener(this); // Usuń słuchacza
                            speakOnArrival(loc, goToFuture);
                        } else if (status.equals("abort")) {
                            Button RetryButton = findViewById(R.id.button_retry);
                            RetryButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mRobot.goTo(loc);
                                }
                            });
                        }
                    }
                };

                mRobot.addOnGoToLocationStatusChangedListener(listener);
                mRobot.goTo(location);
                return goToFuture;
            });
        }

        future.thenRun(() -> {
            Log.d(TAG, "All goTo operations completed");
            goButton.setEnabled(true);
            goAllButton.setEnabled(true);
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

    @Override
    public void onReposeStatusChanged(int i, @NonNull String s) {

    }

    ///////////////////////////////////
}