package com.example.adam.translationapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Main class that runs majority of operations connected with other ones
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent to be called when saved translations wish to be viewed
        final Intent intent = new Intent(this, DisplaySavedActivity.class);

        // Get clickable items
        Spinner outputLang = (Spinner) findViewById(R.id.output_language_spinner);
        Button translate = (Button) findViewById(R.id.translateButton);
        Button saved = (Button) findViewById(R.id.savedButton);
        Button save = (Button) findViewById(R.id.saveButton);
        final EditText textToTranslate = (EditText) findViewById(R.id.editText);

        // Method called when Translate button is clicked
        translate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Hides the keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(textToTranslate.getWindowToken(), 0);

                // Removes whitespace and properly formats the string to be sent within API call
                String text = textToTranslate.getText().toString();
                inputText = text.replaceAll("\\s", "%20");

                // Calls method to get translation via Google Translate
                FetchTranslationTask translationTask = new FetchTranslationTask();
                translationTask.execute(inputText);
            }
        });

        // Method called when the saved button is clicked
        saved.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(intent);
            }
        });

        // Method called when the save button is clicked
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // If there is something being shown as a translation save it
                if (translated != null) {

                    // Declare file stream
                    FileOutputStream fos;

                    // Save translated word into file
                    try {
                        fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                        String toSave = textToTranslate.getText().toString() + " = " + translated + "\n";
                        fos.write(toSave.getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, translated + " : was saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No text entered", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Method that handles language selection within spinner
        outputLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                String lang = parent.getSelectedItem().toString();
                if (lang.equals("Spanish")) {
                    langCode = "es";
                } else if (lang.equals("German")) {
                    langCode = "de";
                } else if (lang.equals("Japanese")) {
                    langCode = "ja";
                } else if (lang.equals("French")) {
                    langCode = "fr";
                } else if (lang.equals("Zulu")) {
                    langCode = "zu";
                } else if (lang.equals("Arabic")) {
                    langCode = "ar";
                } else if (lang.equals("Swedish")) {
                    langCode = "sv";
                } else if (lang.equals("Ukranian")) {
                    langCode = "uk";
                } else if (lang.equals("Hebrew")) {
                    langCode = "iw";
                }
                else {
                    langCode = "en";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                langCode = "en";
            }
        });
    }

    // Class that handles calls to Google Translate API
    private class FetchTranslationTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String apiURL = "https://www.googleapis.com/language/translate/v2";
            String apiKey = "AIzaSyAOcxS2uAyYbgVI7ztjXwSb_9f86jz4u9s";
            String translation = "";


            try {

                // Open Connection and send request to API
                URL url = new URL(apiURL + "?key=" + apiKey + "&target=" + langCode + "&q=" + inputText);
                urlConnection = (HttpURLConnection) url.openConnection();

                // Handle response from API call
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                // Get translated string from JSONObject returned by API call
                JSONObject object = (JSONObject) new JSONTokener(result.toString()).nextValue();
                translation = object.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");

            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            finally {
                urlConnection.disconnect();
            }

            translated = translation;
            return  translation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Will set text view on main screen to be translation
            TextView translationView = (TextView) findViewById(R.id.translatedText);
            translationView.setTextSize(20);
            translationView.setText(translated);

        }

    }


    // Variables
    private String langCode;
    private String translated;
    private String inputText;
    private String FILENAME = "savedTranslations";
}