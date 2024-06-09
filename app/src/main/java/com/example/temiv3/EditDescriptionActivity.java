package com.example.temiv3;

import static com.example.temiv3.MainActivity.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditDescriptionActivity extends AppCompatActivity {

    ListItem listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_description);


        String description = getIntent().getStringExtra("description");
        String location = getIntent().getStringExtra("location");


        EditText editText = findViewById(R.id.editTextDescription);
        editText.setText(description);


        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newDescription = editText.getText().toString();


                ListItem listItem = new ListItem();


                listItem.updateDescriptionInDatabase(location, newDescription);
                Log.d(TAG, String.format("Location: %s", location));
                Log.d(TAG, String.format("newDescription: %s", newDescription));
                Log.d(TAG, String.format("oldDescription: %s", description));

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        finish();
                    }
                }, 500);
            }
        });
    }
}