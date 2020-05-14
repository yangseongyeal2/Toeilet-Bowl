package com.example.toilet_bowl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class BoardActivity extends AppCompatActivity {
    private TextView mNickname;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Intent intent=getIntent();
        String nickName=intent.getStringExtra("nickName");
        String photoUrl=intent.getStringExtra("photoURL");

        findViewById(R.id.Board_FloatingActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),WriteActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.Board_logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });




    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
