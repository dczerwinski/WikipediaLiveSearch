package com.example.wikipedialivesearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

    public TextView textView;
    private ItemClickListener itemClickListener;
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.txtDescription);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),true);
        return false;
    }
}



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{
    private List<String> findings = new ArrayList<>();
    private List<String> links = new ArrayList<>();
    private Context context;

    public RecyclerAdapter(List<String> listData,List<String> links, Context context) {
        this.findings = listData;
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.layout_item_recycler_view,viewGroup,false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        recyclerViewHolder.textView.setText(findings.get(i));
        final int final_i = i;
        recyclerViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Uri uri = Uri.parse(links.get(final_i));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if(findings == null)return 0;
        return findings.size();
    }

    public void setNewList(List<String> listData){
        this.findings = listData;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
    public void hide(){
        this.findings = null;
        this.links = null;
    }
}
