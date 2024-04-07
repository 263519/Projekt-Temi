package com.example.temiv3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Map;

public class DataList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        Button exitButton = findViewById(R.id.goDJ);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


            tab.addView(button);


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

        ListItem MyData = new ListItem();
        List<Map<String, String>> MyDataList = MyData.getlist();


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


            tab.addView(button);


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
