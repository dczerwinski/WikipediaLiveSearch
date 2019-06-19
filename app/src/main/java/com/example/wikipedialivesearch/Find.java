package com.example.wikipedialivesearch;

import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Find extends  Thread{
    String findings[];
    String links[];
    String in;
    Boolean go;
    public Find(){
        this.findings = new String[5];
        links = new String[5];
        in = "";
        go = true;

    }

    void setIn(String in){
        go = true;
        this.in = in;
    }
    Boolean getGo(){
        return go;
    }

    public String getIn() {
        return in;
    }

    public String[] getLinks() {
        return links;
    }

    public String[] getFindings() {
        return findings;
    }

    public void run(){
        while(true){
            if(in.length() >0) {
                String urls = "https://en.wikipedia.org/w/api.php?action=opensearch&search="+in+"&limit=5&namespace=0&format=json";
                URL ur1 = null;
                try {
                    ur1 = new URL(urls);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(ur1.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String line = null;
                String elo="";
                do{
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    elo+=line;
                }while(line != null);


                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(elo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray searchResults = null;
                try {
                    searchResults = new JSONArray(jsonArray.get(1).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray links = null;
                try {
                    links = new JSONArray(jsonArray.get(3).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int i=0; i<5; i++){
                    try {
                        findings[i]= (searchResults.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        this.links[i] = links.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                go = false;
            }


        }

    }
}
