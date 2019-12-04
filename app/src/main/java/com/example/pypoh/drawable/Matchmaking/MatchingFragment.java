package com.example.pypoh.drawable.Matchmaking;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pypoh.drawable.Model.NotifModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.Model.RoomModel;
import com.example.pypoh.drawable.Model.UserModel;
import com.example.pypoh.drawable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchingFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    RoomModel roomModel = new RoomModel();
    private String friendUid, userId;

    private Thread notificationThread;
    private Runnable notificationRunnable;
    private Handler notificationHandler;

    private TextView tv_host, tv_opponent;
    private CircleImageView civ_host, civ_opponent;
    private NotifModel notifModel;

    private String roomId;

    private UserModel userModel;
    QuestionModel questionModel = new QuestionModel();

    ArrayList<String> questionFiltered = new ArrayList<>();

    int playerCode;

    List<NotifModel> listNotification = new ArrayList<>();

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    // host data
    private String nameOpponent, imageOpponent, nameHost, imageHost;

    public MatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matching, container, false);

        civ_host = view.findViewById(R.id.civ_host);
        civ_opponent = view.findViewById(R.id.civ_opponent);
        tv_host = view.findViewById(R.id.host_name);
        tv_opponent = view.findViewById(R.id.opponent_name);

        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager != null) {
            mNotificationManager.cancel(0);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        // Create Rooms
        Bundle bundle = getArguments();
        playerCode = bundle.getInt("STATUS_KEY");
        if (bundle != null) {
            int intentCode = bundle.getInt("STATUS_KEY");
            switch (intentCode) {
                case 0:
                    try {
                        searchBattleTag(((MatchingActivity) getActivity()).getOpponentBattleTag());
                        String json = bundle.getString("PROFILE_DATA");
                        nameOpponent = bundle.getString("NAME_FRIEND_KEY");
                        imageOpponent = bundle.getString("PROFILE_PICTURE_FRIEND_KEY");
                        Gson gson = new Gson();
                        userModel = gson.fromJson(json, UserModel.class);
                        if (nameOpponent != null) {
                            tv_opponent.setText(nameOpponent);
                        }
                        if (imageOpponent != null) {
                            Picasso.get().load(imageOpponent).into(civ_opponent);
                        }
                        if (userModel.getImage() != null) {
                            Picasso.get().load(userModel.getImage()).into(civ_host);
                        }
                        if (userModel.getName() != null) {
                            tv_host.setText(userModel.getName());
                        }
                    } catch (Exception e) {
                    }
                    createRoom();
                    break;
                case 1:
                    updateNotificationData();
                    String json = bundle.getString("PROFILE_DATA");
                    Gson gson = new Gson();
                    userModel = gson.fromJson(json, UserModel.class);
                    if (userModel.getImage() != null) {
                        Picasso.get().load(userModel.getImage()).into(civ_opponent);
                    }
                    if (userModel.getName() != null) {
                        tv_opponent.setText(userModel.getName());
                    }
                    break;
            }
        }
    }

    private void searchBattleTag(String battleTag) {
        db.collection("users").whereEqualTo("battleTag", battleTag).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    friendUid = doc.getId();
                    Log.d("friendUid", friendUid);
                    sendNotificationToOpponent(friendUid);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void sendNotificationToOpponent(final String friendUid) {
        final String uid = mAuth.getCurrentUser().getUid();
        final NotifModel notifModel = new NotifModel(uid, 0, roomId);
        Log.d("friendUIDNotif", friendUid);
        db.collection("users").document(friendUid).collection("notification").document(uid).set(notifModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("notifModel", String.valueOf(notifModel.getStatus()));
                    // add thread
                    notificationRunnable = new Runnable() {
                        @Override
                        public void run() {
                            db.collection("users").document(friendUid).collection("notification").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot != null) {
                                            deleteNotification(friendUid, roomId);
                                        }
                                    }
                                }
                            });
                            getActivity().finish();
                        }
                    };
                    notificationHandler = new Handler();
                    notificationHandler.postDelayed(notificationRunnable, 10000);
                }
            }
        });
        db.collection("users").document(friendUid).collection("notification").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    NotifModel notifModel1 = documentSnapshot.toObject(NotifModel.class);
                    if (notifModel1 != null) {
                        if (notifModel1.getStatus() == 2) {
                            // sisi host
                            notificationHandler.removeCallbacks(notificationRunnable);
                            db.collection("room").document(roomId).update("battleTag_opponent", friendUid);
                            // Intent ke aktipiiti sebelah;
                            Bundle bundle = new Bundle();
                            bundle.putInt("PLAYER_KEY", playerCode);
                            bundle.putStringArrayList("QUESTIONS_KEY", questionFiltered);
                            bundle.putString("ROOM_KEY", roomId);
                            bundle.putString("FRIEND_ID", friendUid);
                            Log.d("FriendIDDebug", " " + friendUid);

                            // add bundle for load data
                            bundle.putString("HOST_NAME_KEY", userModel.getName());
                            bundle.putString("HOST_IMAGE_KEY", userModel.getImage());
                            bundle.putString("OPPONENT_NAME_KEY", nameOpponent);
                            bundle.putString("OPPONENT_IMAGE_KEY", imageOpponent);

                            deleteNotification(friendUid);
                            try {
                                ((MatchingActivity) getActivity()).setFragmentWithBundle(new AcceptMatchingFragment(), bundle);

                            } catch (NullPointerException ne) {
                                Log.d("setFragmentBundleNull", ne.toString());
                            }
                        } else if (notifModel1.getStatus() == 1) {
                            // nanti send notif juga
                            db.collection("room").document(roomId).delete();
                            // intent ke main
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("friendUidDestroy", "Jalan Wes");
        if (friendUid != null)
            Log.d("friendUidDestroy", friendUid);
        deleteNotification(friendUid);
    }

    private void deleteNotification(String friendUid) {
        String uid = mAuth.getCurrentUser().getUid();
        try {
            db.collection("users").document(friendUid).collection("notification").document(uid).delete();
        } catch (Exception e) {

        }
    }

    private void deleteNotification(String friendUid, String roomId) {
        String uid = mAuth.getCurrentUser().getUid();
        try {
            db.collection("users").document(friendUid).collection("notification").document(uid).delete();
            db.collection("room").document(roomId).delete();
        } catch (Exception e) {

        }
    }

    private void createRoom() {
        String uId = mAuth.getCurrentUser().getUid();
        roomModel.setBattleTag_host(uId);

        roomId = db.collection("room").document().getId();
        db.collection("room").document(roomId).set(roomModel);
        divideQuestion(roomId);

    }

    private void updateNotificationData() {
        final String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        NotifModel notifModel1 = doc.toObject(NotifModel.class);
                        if (notifModel1 != null) {
                            //
                            db.collection("users").document(notifModel1.getBattleTag()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        nameHost = task.getResult().getString("name");
                                        imageHost = task.getResult().getString("image");
                                        if (nameHost != null) {
                                            tv_host.setText(nameHost);
                                        }
                                        if (imageHost != null) {
                                            Picasso.get().load(imageHost).into(civ_host);
                                        }
                                    }
                                }
                            });

                            if (notifModel1.getStatus() == 2) {
                                // Intent ke aktipiiti sebelah;
                                roomId = notifModel1.getRoomId();
                                setQuestionForInvitedPerson(roomId);

                            } else if (notifModel1.getStatus() == 1) {
                                // state gagal
//                                ((MatchingActivity) getActivity()).setFragment(new AcceptMatchingFragment(), 0);
                                // nanti send notif juga
                                // intent ke main
                            }
                        }
                    }

                }
            }
        });

        db.collection("users").document(uid).collection("notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        listNotification.add(doc.toObject(NotifModel.class));
                    }
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    db.collection("users").document(uid).collection("notification").document(listNotification.get(0).getBattleTag()).update("status", 2);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Room tidak ditemukan", Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }
                            }
                        }, 3000);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void setQuestionForInvitedPerson(final String roomId) {
        db.collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (task.isSuccessful()) {
                        ArrayList<String> a = new ArrayList<>();
                        questionModel = documentSnapshot.toObject(QuestionModel.class);
                        if (questionModel != null) {
                            a.addAll(questionModel.getQuestionList());
                        }
                        Collections.shuffle(a);
                        questionModel.setQuestionList(a);
                    }
                }
                questionFiltered.clear();
                for (int i = 0; i < 18; i++) {
                    questionFiltered.add(questionModel.getQuestionList().get(i));
                    Log.d("QuestionModelDebg", " " + questionModel.getQuestionList().get(i));
                }
                db.collection("room").document(roomId).update("availableQuestion", questionFiltered).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("question", questionModel.getQuestionList().get(0));
                        }
                    }
                });
                // Setup for intent
                Bundle bundle = new Bundle();
                bundle.putInt("PLAYER_KEY", playerCode);
                bundle.putStringArrayList("QUESTIONS_KEY", questionFiltered);
                bundle.putString("ROOM_KEY", roomId);

                // add data to load
                bundle.putString("HOST_NAME_KEY", nameHost);
                bundle.putString("HOST_IMAGE_KEY", imageHost);
                bundle.putString("OPPONENT_NAME_KEY", userModel.getName());
                bundle.putString("OPPONENT_IMAGE_KEY", userModel.getImage());
                if ((MatchingActivity) getActivity() != null) {
                    ((MatchingActivity) getActivity()).setFragmentWithBundle(new AcceptMatchingFragment(), bundle);
                } else {
                    Log.d("getActivityDebug", "Activity is null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Tidak ada data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void divideQuestion(final String roomId) {
        db.collection("question").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (task.isSuccessful()) {
                        ArrayList<String> a = new ArrayList<>();
                        questionModel = documentSnapshot.toObject(QuestionModel.class);
                        if (questionModel != null) {
                            a.addAll(questionModel.getQuestionList());
                        }
                        Collections.shuffle(a);
                        questionModel.setQuestionList(a);
                    }
                }
                questionFiltered.clear();
                for (int i = 0; i < 18; i++) {
                    // disini dia ga dapet question modelnya
                    questionFiltered.add(questionModel.getQuestionList().get(i));
                }
                db.collection("room").document(roomId).update("availableQuestion", questionFiltered).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("question", questionModel.getQuestionList().get(0));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Tidak ada data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
