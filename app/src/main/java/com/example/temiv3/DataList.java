package com.example.temiv3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataList extends AppCompatActivity {
    private List<String> checkedCheckboxesNames = new ArrayList<>();

    public boolean currentMap = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        Button allLocationsButton = findViewById(R.id.goAllLocations);
        Button currentLocationsButton = findViewById(R.id.goCurrentMapLocations);
        allLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetList(v);
            }
        });

        currentLocationsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentMap = true;
                GetList(v);
            }
        });


    }

    protected void onResume() {
        super.onResume();

        refreshData();
    }
    private void refreshData() {

        ListItem MyData = new ListItem();
        List<Map<String, String>> MyDataList = MyData.getlist();


        TableLayout tab = findViewById(R.id.tableLayout);
        if (tab == null) {

            Log.e("DataList", "Nie ma");
            return;
        }


        tab.removeAllViews();


        for (int i = 0; i < MyDataList.size(); i++) {

            Map<String, String> itemData = MyDataList.get(i);
            final String description = itemData.get("desList");

            final String local = itemData.get("locList");


            Button button = new Button(this);
            button.setText(local);
            CheckBox box = new CheckBox(this);
            box.setText(local);

            tab.addView(box);
            tab.addView(button);
            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedCheckboxesNames.add(local); // Dodaj nazwę zaznaczonego checkboxa do listy
                    } else {
                        checkedCheckboxesNames.remove(local); // Usuń nazwę checkboxa z listy, jeśli zostanie odznaczony
                    }
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DataList.this, EditDescriptionActivity.class);


                    intent.putExtra("description", description);
                    intent.putExtra("location", local);

                    startActivity(intent);
                }
            });
        }
    }

    public void GetList(View v) {
        setContentView(R.layout.datalistlayout);


        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setVisibility(View.VISIBLE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String name : checkedCheckboxesNames) {
                    Log.d("CheckedBox Add", String.format("Pos to main Finish %s", name));
                }
                Intent intent = new Intent(DataList.this, MainActivity.class);

                // Dodajemy listę nazw zaznaczonych checkboxów do intentu
                intent.putStringArrayListExtra("checkedCheckboxNames", new ArrayList<>(checkedCheckboxesNames));
                startActivity(intent);
                finish();

            }
        });


        ListItem MyData = new ListItem();
        List<Map<String, String>> MyDataList = new ArrayList<Map<String, String>>();
        // all location
        if(!currentMap){
        MyDataList = MyData.getlist();
        }else {
            //current map location
             MyDataList = MyData.getCurrentMapList();
        }
        TableLayout tab = findViewById(R.id.tableLayout);
        if (tab == null) {

            Log.e("DataList", "TableLayout 'tableLayout' not found in the main layout");
            return;
        }


        for (int i = 0; i < MyDataList.size(); i++) {

            Map<String, String> itemData = MyDataList.get(i);
            final String description = itemData.get("desList");


            final String local = itemData.get("locList");


            Button button = new Button(this);
            button.setText(local);
            CheckBox box = new CheckBox(this);
            box.setText(local);

            tab.addView(box);
            tab.addView(button);
            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedCheckboxesNames.add(local);
                    } else {
                        checkedCheckboxesNames.remove(local);
                    }
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(DataList.this, EditDescriptionActivity.class);


                    intent.putExtra("description", description);
                    intent.putExtra("location", local);

                    startActivity(intent);
                }
            });
        }


    }


}
