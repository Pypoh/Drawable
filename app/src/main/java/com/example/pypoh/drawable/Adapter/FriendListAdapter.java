package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.friendViewHolder> {

    private Context context;
    private List<FriendModel> friend;
    private OnItemClickListener onItemClickListener;

    public FriendListAdapter(Context context, List<FriendModel> friend) {
        this.context = context;
        this.friend = friend;
    }

    @NonNull
    @Override
    public friendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new friendViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull friendViewHolder holder, int position) {
        final FriendModel current = friend.get(position);
        holder.bind(current,onItemClickListener);
        holder.tv_friends_name.setText(current.getName());
        holder.tv_friends_rank.setText(String.valueOf(current.getLevel()));
        Picasso.get().load(current.getImage()).into(holder.civ_friends_pict);
        if (current.isOnline()) {
            holder.online_indicator.setVisibility(View.VISIBLE);

        } else {
            holder.online_indicator.setVisibility(View.INVISIBLE);
        }
        //        holder.online_indicator.setBackgroundColor(R.drawable.online_indicator);
    }
    public interface OnItemClickListener {
        void onItemClick(FriendModel friendModel);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<FriendModel> getFriend() {
        return friend;
    }

    public void setFriend(List<FriendModel> friend) {
        this.friend = friend;
    }

    @Override
    public int getItemCount() {
        return friend.size();
    }

    public class friendViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_friends_name, tv_friends_rank;
        private CircleImageView civ_friends_pict;
        private View online_indicator;
        private CardView cardView;

        public friendViewHolder(@NonNull View itemView, FriendListAdapter adapter) {
            super(itemView);
            tv_friends_name = itemView.findViewById(R.id.tv_nama_profile);
            tv_friends_rank = itemView.findViewById(R.id.tv_rank_profile);
            online_indicator = itemView.findViewById(R.id.online_indicator);
            civ_friends_pict = itemView.findViewById(R.id.iv_profile_pict);
            cardView = itemView.findViewById(R.id.cv_friend);
        }
        public void bind(final FriendModel friendModel, final OnItemClickListener listener) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(friendModel);
                }
            });
        }
    }


}
