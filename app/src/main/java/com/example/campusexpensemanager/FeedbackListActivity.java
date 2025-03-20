package com.example.campusexpensemanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {

    private ListView lvFeedback;
    private FirebaseFirestore db;
    private List<String> feedbackList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvFeedback = findViewById(R.id.lv_feedback);
        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, feedbackList);
        lvFeedback.setAdapter(adapter);

        loadFeedback();
    }

    private void loadFeedback() {
        db.collection("feedbacks")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    feedbackList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String feedbackText = document.getString("feedback");
                        String userEmail = document.getString("userEmail");
                        String timestamp = document.getTimestamp("timestamp").toDate().toString();

                        String feedbackItem = "From: " + userEmail + "\n" + "Date: " + timestamp +  "\n\n" + feedbackText;

                        feedbackList.add(feedbackItem);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }
}