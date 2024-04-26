package com.example.temiv3;




import static com.example.temiv3.MainActivity.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.permission.Permission;

import java.sql.Connection;
import java.util.ArrayList;

public class AddLocationActivity extends AppCompatActivity {

    ListItem listItem;
    public static Robot mRobot;


   // MainActivity main;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlocationlayout);
        mRobot = Robot.getInstance();

        listItem = new ListItem();

     //   main = new MainActivity();

        TextView location = (TextView) findViewById(R.id.edittextlocation);
        TextView description = (TextView) findViewById(R.id.edittextdescription);
        Button btninsert = (Button) findViewById(R.id.btnadd);
        Button exitButton = (Button) findViewById(R.id.exitButton);

        btninsert.setOnClickListener(new View.OnClickListener() {
            Connection connection = null;
            @Override
            public void onClick(View v) {

                // try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                connection = connectionHelper.connectionclass();
                if (connection != null) {
                    Log.d(TAG, String.format("TUUU %s", location.getText()));
                    Log.d(TAG, String.format("TUUU %s",description.getText()));
                    if(listItem.addLocation(location.getText().toString(),description.getText().toString())){
                        saveLocation(location);
                    }


                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddLocationActivity.this, MainActivity.class);

                startActivity(intent);
                finish(); // Zakończ bieżącą aktywność po kliknięciu na guzik

            }
        });


    }

    ////////////////////////////////

    public void saveLocation(TextView textlocat) {
        String location = textlocat.getText().toString().toLowerCase().trim();
        boolean result = mRobot.saveLocation(location);
        if (result) {

            Log.d(TAG, String.format("I've successfully saved the %s", location));
        } else {

            Log.d(TAG, String.format("Saved the %s location failed", location));
        }

    }



}

