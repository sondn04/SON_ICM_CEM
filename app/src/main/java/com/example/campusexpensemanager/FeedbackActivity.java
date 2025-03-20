package com.example.campusexpensemanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText edtFeedback;
    private Button btnSubmitFeedback;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtFeedback = findViewById(R.id.edt_feedback);
        btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);

        db = FirebaseFirestore.getInstance();

        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedbackText = edtFeedback.getText().toString().trim();

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        String userEmail = currentUser.getEmail();


        Map<String, Object> feedback = new HashMap<>();
        feedback.put("userId", userId);
        feedback.put("userEmail", userEmail);
        feedback.put("feedback", feedbackText);
        feedback.put("timestamp", com.google.firebase.Timestamp.now());

        db.collection("feedbacks")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Error submitting feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}