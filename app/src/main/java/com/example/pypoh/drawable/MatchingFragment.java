package com.example.pypoh.drawable;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pypoh.drawable.Matchmaking.MatchingActivity;
import com.example.pypoh.drawable.Model.FriendModel;
import com.example.pypoh.drawable.Model.NotifModel;
import com.example.pypoh.drawable.Model.QuestionModel;
import com.example.pypoh.drawable.Model.RoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchingFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    RoomModel roomModel = new RoomModel();
    private String friendUid;

    private String roomId;
    QuestionModel questionModel = new QuestionModel();

    ArrayList<String> questionFiltered = new ArrayList<>();

    int playerCode;

    List<NotifModel> listNotification = new ArrayList<>();


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

        // Create Rooms
        Bundle bundle = getArguments();
        assert bundle != null;
        playerCode = bundle.getInt("STATUS_KEY");
        if (bundle != null) {
            int intentCode = bundle.getInt("STATUS_KEY");
            switch (intentCode) {
                case 0:
                    createRoom();
                    break;
                case 1:
                    updateNotificationData();
                    break;
            }
        }

        try {
            searchBattleTag(((MatchingActivity) getActivity()).getOpponentBattleTag());
        } catch (Exception e) {
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
        String uid = mAuth.getCurrentUser().getUid();
        final NotifModel notifModel = new NotifModel(uid, 0, roomId);
        Log.d("friendUIDNotif", friendUid);
        db.collection("users").document(friendUid).collection("notification").document(uid).set(notifModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("notifModel", String.valueOf(notifModel.getStatus()));
                }
            }
        });
        db.collection("users").document(friendUid).collection("notification").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    NotifModel notifModel1 = documentSnapshot.toObject(NotifModel.class);
                    if (notifModel1 != null) {
                        if (notifModel1.getStatus() == 2) {
                            db.collection("room").document(roomId).update("battleTag_opponent", friendUid);
                            // Intent ke aktipiiti sebelah;
                            Bundle bundle = new Bundle();
                            bundle.putInt("PLAYER_KEY", playerCode);
                            bundle.putStringArrayList("QUESTIONS_KEY", questionFiltered);
                            ((MatchingActivity) getActivity()).setFragmentWithBundle(new AcceptMatchingFragment(), bundle);
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
                            if (notifModel1.getStatus() == 2) {
                                // Intent ke aktipiiti sebelah;
                                String roomId = notifModel1.getRoomId();
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
                                db.collection("users").document(uid).collection("notification").document(listNotification.get(0).getBattleTag()).update("status", 2);
                            }
                        }, 2000);
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
                // Setup for intent
                Bundle bundle = new Bundle();
                bundle.putInt("PLAYER_KEY", playerCode);
                bundle.putStringArrayList("QUESTIONS_KEY", questionFiltered);
                bundle.putString("ROOM_KEY", roomId);
                ((MatchingActivity) getActivity()).setFragmentWithBundle(new AcceptMatchingFragment(), bundle);
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
