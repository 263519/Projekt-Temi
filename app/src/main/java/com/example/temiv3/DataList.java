package com.example.temiv3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataList extends AppCompatActivity {
    private List<String> checkedCheckboxesNames = new ArrayList<>();
    public boolean currentMap = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        executorService.execute(() -> {
            ListItem MyData = new ListItem();

            List<Map<String, String>> MyDataList;

            if(!currentMap){
                 MyDataList = MyData.getlist();
            }else{

                MyDataList = MyData.getCurrentMapList();
            }
            mainThreadHandler.post(() -> populateTable(MyDataList));
        });
    }

    private void populateTable(List<Map<String, String>> MyDataList) {
        TableLayout tab = findViewById(R.id.tableLayout);
        if (tab == null) {
            Log.e("DataList", "TableLayout 'tableLayout' not found in the main layout");
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

            box.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedCheckboxesNames.add(local);
                } else {
                    checkedCheckboxesNames.remove(local);
                }
            });

            button.setOnClickListener(v -> {
                Intent intent = new Intent(DataList.this, EditDescriptionActivity.class);
                intent.putExtra("description", description);
                intent.putExtra("location", local);
                startActivity(intent);
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
                for (String name : checkedCheckboxesNames) {
                    Log.d("CheckedBox Add", String.format("Pos to main Finish %s", name));
                }
                Intent intent = new Intent(DataList.this, MainActivity.class);
                intent.putStringArrayListExtra("checkedCheckboxNames", new ArrayList<>(checkedCheckboxesNames));
                startActivity(intent);
                finish();
            }
        });

        executorService.execute(() -> {
            ListItem MyData = new ListItem();
            List<Map<String, String>> MyDataList;
            if (!currentMap) {
                MyDataList = MyData.getlist();
            } else {

                 MyDataList = MyData.getCurrentMapList();

            }
            mainThreadHandler.post(() -> populateTable(MyDataList));
        });
    }
}




