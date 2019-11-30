package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.R;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private Context mContext;
    private List<String> questionData;

    public OptionsAdapter(Context mContext, List<String> questionData) {
        this.mContext = mContext;
        this.questionData = questionData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = questionData.get(position);

        holder.textOptions.setText(option);

    }

    @Override
    public int getItemCount() {
        return questionData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textOptions = itemView.findViewById(R.id.text_options);
        }
    }
}
