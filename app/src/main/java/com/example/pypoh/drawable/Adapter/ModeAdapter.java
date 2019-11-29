package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.ModeModel;
import com.example.pypoh.drawable.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ModeAdapter extends RecyclerView.Adapter<ModeAdapter.ViewHolder> {

    private Context mContext;
    private List<ModeModel> dataset;
    private OnItemClickListener onItemClickListener;

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
        ModeModel modeModel = dataset.get(position);
        holder.bind(modeModel, onItemClickListener);

        // Set Views
//        holder.modeImage.setImageResource();
        holder.modeTitle.setText(modeModel.getName());
        holder.modeDesc.setText(modeModel.getDescription());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ModeModel modeModel);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Views
        private RoundedImageView modeImage;
        private TextView modeTitle, modeDesc;
        private CardView modeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            modeImage = itemView.findViewById(R.id.item_mode_image);
            modeTitle = itemView.findViewById(R.id.item_mode_title);
            modeDesc = itemView.findViewById(R.id.item_mode_desc);
            modeCard = itemView.findViewById(R.id.item_mode_card);

        }

        public void bind(final ModeModel modeModel, final OnItemClickListener listener) {
            modeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(modeModel);
                }
            });
        }
    }
}
