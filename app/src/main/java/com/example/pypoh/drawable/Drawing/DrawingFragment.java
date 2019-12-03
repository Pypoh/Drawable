package com.example.pypoh.drawable.Drawing;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pypoh.drawable.Drawing.Classification.ImageClassifier;
import com.example.pypoh.drawable.Drawing.Classification.Result;
import com.example.pypoh.drawable.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Random;

public class DrawingFragment extends Fragment {

    private PaintView paintView;
    private Button buttonClear;
    private TextView textPlaceholder;

    private ImageClassifier classifier; // complete image classification

    private RelativeLayout layout;

    private String roomId;
    private int playerCode;

    private String questions;

    private int iterasi;

    private TextView textQuestion, textRound;

    private FirebaseFirestore db;

    public DrawingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        db = FirebaseFirestore.getInstance();

        paintView = view.findViewById(R.id.paint_view);
        buttonClear = view.findViewById(R.id.button_clear);
        textPlaceholder = view.findViewById(R.id.tv_placeholder_draw_here);
        layout = view.findViewById(R.id.layout_drawing);

        textQuestion = view.findViewById(R.id.tv_question_round);
        textRound = view.findViewById(R.id.tv_title_round);


        paintView.init();

        paintView.setView(textPlaceholder);

        Bundle bundle = getArguments();
        if (bundle != null) {
            roomId = bundle.getString("ROOM_KEY");
            playerCode = bundle.getInt("PLAYER_KEY");
            questions = bundle.getString("QUESTION");
            iterasi = bundle.getInt("ITERASI");

            textRound.setText("Round " + (iterasi+1) + "/6");
            textQuestion.setText(questions);
        }

        // instantiate classifier
        try {
            this.classifier = new ImageClassifier(getActivity());
        } catch (IOException e) {
            Log.e("MainActivity", "Cannot initialize tfLite model!", e);
            e.printStackTrace();
        }

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getResult();
                resetView();
            }
        });

        return view;
    }

    public int getResult() {
        Bitmap sketch = paintView.getNormalizedBitmap(); // get resized bitmap

//        showImage(paintView.scaleBitmap(40, sketch));

        // create the result
        Result result = classifier.classify(sketch);

        float resultFloat = result.getProbbability();

        int resultInt = (int) (resultFloat*100);

        return resultInt;
    }

    public Bitmap getBitmapResult() {
//        Bitmap sketch = paintView.getNormalizedBitmap();

        Bitmap sketch = paintView.getmBitmap();
//        showImage(sketch);
        return sketch;
    }

    // debug: ImageView with rescaled 28x28 bitmap
    private void showImage(Bitmap bitmap) {
        Dialog builder = new Dialog(getContext());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    private void resetView() {
        paintView.clear();

//        // get a random label and set as expected class
//        classifier.setExpectedIndex(new Random().nextInt(classifier.getNumberOfClasses()));
//        textViewDraw.setText("Draw ... " + classifier.getLabel(classifier.getExpectedIndex()));

    }
}
