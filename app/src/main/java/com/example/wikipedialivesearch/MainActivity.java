package com.example.wikipedialivesearch;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    SearchView searchView;
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

        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerAdapter adapter = new RecyclerAdapter(findings,links,this);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchField);



        in = "";


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

                        final RecyclerAdapter adapter = new RecyclerAdapter(findings,links,MainActivity.this);
                        recyclerView.setAdapter(adapter);

                    }
                }

                if(in.length() == 0){
                    final RecyclerAdapter adapter = new RecyclerAdapter(null,null,MainActivity.this);
                    recyclerView.setAdapter(adapter);
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
                    final RecyclerAdapter adapter = new RecyclerAdapter(null,null,MainActivity.this);
                    recyclerView.setAdapter(adapter);
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

