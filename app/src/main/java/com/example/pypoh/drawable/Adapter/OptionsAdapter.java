package com.example.pypoh.drawable.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.OptionModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.R;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private Context mContext;
    private List<OptionModel> questionData;
//    private OnItemClickListener mListener;

    public OptionsAdapter(Context mContext, List<OptionModel> questionData) {
        this.mContext = mContext;
        this.questionData = questionData;
    }


//    public interface OnItemClickListener{
//        void onItemClick(int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener){
//        mListener = listener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final OptionModel option = questionData.get(position);
//        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
        Log.d("Position", " " + questionData.get(position));

        holder.textOptions.setText(option.getQuestion());
        holder.rowLinier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (option.isSelected()) {
                    option.setSelected(false);
                    holder.rowLinier.setBackgroundResource(R.drawable.rounded_button_border_primary);
                    holder.textOptions.setTextColor(mContext.getResources().getColor(R.color.semi_black));
                } else {
                    option.setSelected(true);
                    holder.rowLinier.setBackgroundResource(R.drawable.btn_background);
                    holder.textOptions.setTextColor(Color.WHITE);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textOptions;
        private LinearLayout rowLinier;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textOptions = itemView.findViewById(R.id.text_options);
            rowLinier =(LinearLayout) itemView.findViewById(R.id.row_item);
        }
    }
}
