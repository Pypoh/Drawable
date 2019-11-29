package com.example.pypoh.drawable.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.friendViewHolder> {

    private Context context;
    private List<FriendModel> friend;

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
        FriendModel current = friend.get(position);
        holder.tv_friends_name.setText(current.getName());
        holder.tv_friends_rank.setText(String.valueOf(current.getLevel()));
        if (current.isOnline()) {
            holder.online_indicator.setVisibility(View.VISIBLE);
        } else {
            holder.online_indicator.setVisibility(View.INVISIBLE);
        }
        //        holder.online_indicator.setBackgroundColor(R.drawable.online_indicator);
    }

    @Override
    public int getItemCount() {
        return friend.size();
    }

    public class friendViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_friends_name, tv_friends_rank;
        private CircleImageView civ_friends_pict;
        private View online_indicator;

        public friendViewHolder(@NonNull View itemView, FriendListAdapter adapter) {
            super(itemView);
            tv_friends_name = itemView.findViewById(R.id.tv_nama_profile);
            tv_friends_rank = itemView.findViewById(R.id.tv_rank_profile);
            online_indicator = itemView.findViewById(R.id.online_indicator);
            civ_friends_pict = itemView.findViewById(R.id.iv_profile_pict);
        }
    }


}
