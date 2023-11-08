package com.example.logintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MyPostActivity extends AppCompatActivity {

    private GridView myPostGridView;
    private List<ImageModel> myUploads;
    private ImageAdapter imageAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        myPostGridView = findViewById(R.id.myPostGridView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Assuming 'uploads' is your database reference where images are stored
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        myUploads = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, myUploads);
        myPostGridView.setAdapter(imageAdapter);

        if (user != null) {
            loadMyImages(user.getUid());
        } else {
            Toast.makeText(this, "You must be logged in to see your posts.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMyImages(String userId) {
        // Fetch images from Firebase where 'uploader' field matches the current user's UID
        databaseRef.orderByChild("uploader").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myUploads.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ImageModel upload = postSnapshot.getValue(ImageModel.class);
                            if (upload != null) {
                                myUploads.add(upload);
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyPostActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
