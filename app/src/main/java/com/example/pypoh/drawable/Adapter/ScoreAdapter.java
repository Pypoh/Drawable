package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.RoundModel;
import com.example.pypoh.drawable.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private Context context;
    private List<RoundModel> roundModel;

    public ScoreAdapter(Context context, List<RoundModel> roundModel) {
        this.context = context;
        this.roundModel = roundModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoundModel data = roundModel.get(position);
        holder.score_host.setText(String.valueOf(data.getScore_host()));
        holder.score_opponent.setText(String.valueOf(data.getScore_opponent()));
        holder.question.setText(data.getQuestion());
        Picasso.get().load(data.getImage_host()).into(holder.image_host);
        Picasso.get().load(data.getImage_opponent()).into(holder.image_opponent);

    }

    @Override
    public int getItemCount() {
        return roundModel.size();
    }
    
    public int getTotalHost() {
        int totalHost = 0;
        for (RoundModel item : roundModel) {
            totalHost += item.getScore_host();
        }
        
        return totalHost;
    }

    public int getTotalOpponent() {
        int totalOpponent = 0;
        for (RoundModel item : roundModel) {
            totalOpponent += item.getScore_opponent();
        }

        return totalOpponent;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_host, image_opponent;
        private TextView score_host, score_opponent, question;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_host = itemView.findViewById(R.id.draw_host);
            image_opponent = itemView.findViewById(R.id.draw_opponent);
            score_host = itemView.findViewById(R.id.scoredraw_host);
            score_opponent = itemView.findViewById(R.id.scoredraw_opponent);
            question = itemView.findViewById(R.id.nameQuestion);

        }


    }
}
