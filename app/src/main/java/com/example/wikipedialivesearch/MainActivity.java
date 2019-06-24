package com.example.wikipedialivesearch;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    Button buttons[];
    String links [] = new String[5];


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.searchField);
        buttons = new Button[5];
        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);
        buttons[4] = findViewById(R.id.button5);

        for(int i=0 ;i<5; i++){
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(links[finalI]); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    go(newText);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

    }

    private void go(String in) throws IOException, JSONException {
        if(in.length() >0) {
            String urls = "https://en.wikipedia.org/w/api.php?action=opensearch&search="+in+"&limit=5&namespace=0&format=json";
            URL ur1 = new URL(urls);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ur1.openStream()));
            String line;
            String elo="";
            while((line = reader.readLine()) != null){
                elo+=line;
            }

            JSONArray jsonArray = new JSONArray(elo);
            JSONArray searchResults = new JSONArray(jsonArray.get(1).toString());
            JSONArray links = new JSONArray(jsonArray.get(3).toString());

            for(int i=0; i<5; i++){
                buttons[i].setText(searchResults.get(i).toString());
                buttons[i].setVisibility(View.VISIBLE);
                this.links[i] = links.get(i).toString();
            }


            reader.close();
        }
        else {
            for(int i=0; i<5; i++){
                buttons[i].setVisibility(View.INVISIBLE);
            }
        }

    }



}