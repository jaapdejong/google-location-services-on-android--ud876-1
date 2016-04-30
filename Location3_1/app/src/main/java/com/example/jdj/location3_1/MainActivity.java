package com.example.jdj.location3_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button addGeoFencesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addGeoFencesButton = (Button) findViewById(R.id.add_geofences_button);
    }

    public void addGeoFencesButtonHandler(View view) {
    }

}
