package com.example.temiv3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class DataList extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        ////////////////////////




/*
        Button exitButton = (Button)  findViewById(R.id.exitButton);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        ////////////////
        Button exitButton = (Button)  findViewById(R.id.goDJ);

/*
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataList.this, MainActivity.class);
                startActivity(intent);

            }
        });*/
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
        ad = new SimpleAdapter(DataList.this,MyDataList,R.layout.datalistlayout,Fromw,Tow);
        for (int i = 0; i < ad.getCount(); i++) {
            View itemView = ad.getView(i, null, tab);
            tab.addView(itemView);
        }
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();

            }
        });


    }

}