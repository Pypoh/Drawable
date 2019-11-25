package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.ModeModel;
import com.example.pypoh.drawable.R;

import java.util.List;

public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder> {

    private Context mContext;
    private List<ModeModel> dataset;

    public ModeAdapter(Context mContext, List<ModeModel> dataset) {
        this.mContext = mContext;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        // Views


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
