package com.example.wikipedialivesearch;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    SearchView searchView;
    Button buttons[];
    List<String> links = new ArrayList<String>();
    List<String> findings = new ArrayList<String>();
    Handler handler;
    volatile String in;

    @SuppressLint("HandlerLeak")
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
        in = "";

        for(int i=0 ;i<5; i++){
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(links.get(finalI));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.obj instanceof  List && msg.obj != null) {
                    if(((List<String>)msg.obj).size() != 0) {
                        findings.clear();
                        links.clear();
                        for(int i=0 ; i<((List<String>) msg.obj).size(); i++){
                            if(i%2 == 0)findings.add(((List<String>) msg.obj).get(i));
                            else links.add(((List<String>) msg.obj).get(i));
                        }
                        for(int i=0; i<5; i++){
                            buttons[i].setVisibility(View.VISIBLE);
                            buttons[i].setText(findings.get(i));
                        }
                    }
                }

                if(in.length() == 0){
                    for(int i=0; i<5; i++){
                        buttons[i].setVisibility(View.INVISIBLE);
                    }
                }
            }

        };

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                in = newText;
                if(newText.length() == 0){
                    for(int i=0; i<5; i++){
                        buttons[i].setVisibility(View.INVISIBLE);
                    }
                }

                return false;
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Message message = new Message();
                        message.obj = go();
                        if(message.obj != null)
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }




    private List<String> go() throws IOException, JSONException {
        List<String> list = new ArrayList<String>();
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
                list.add(searchResults.get(i).toString());
                list.add(links.get(i).toString());
            }
            reader.close();
        }
        else{
            return null;
        }
        return list;
    }
}