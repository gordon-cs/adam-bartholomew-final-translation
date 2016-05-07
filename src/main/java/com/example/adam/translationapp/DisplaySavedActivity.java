package com.example.adam.translationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DisplaySavedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_saved);

        // Clear button
        Button clear = (Button) findViewById(R.id.clearButton);

        // Initialize the view that will show saved translations
        TextView descr = (TextView) findViewById(R.id.description);
        final TextView saves = (TextView) findViewById(R.id.savedDisplay);
        saves.setTextSize(20);
        descr.setTextSize(20);

        // Grab the intent sent to this activity
        Intent intent = getIntent();

        // Method called when "Clear" button is clicked
        clear.setOnClickListener(new View.OnClickListener() {

            // Clears all data within the save file for this app
            public void onClick(View v) {

                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.close();

                    FileInputStream in = openFileInput(FILENAME);
                    ByteArrayOutputStream result = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    saves.setText(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        // Get saved translations and put them in the view
        try {
            FileInputStream in = openFileInput(FILENAME);
            ByteArrayOutputStream result = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            saves.setText(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String FILENAME = "savedTranslations";
}
