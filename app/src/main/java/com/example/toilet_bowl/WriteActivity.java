package com.example.toilet_bowl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    private String uid;
    private TextInputEditText mTitle;
    private TextInputEditText mContents;
    private TextInputLayout mTitleLayout;
    private TextInputLayout mContentsLayOut;
    private String mTitle_intent;
    private String mContent_intent;
    private String uid_intent;
    private Date date_intent;
    private String documentId_intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
            //Toast.makeText(this,uid,Toast.LENGTH_LONG).show();
        }
        mTitle=findViewById(R.id.write_title_EditText);
        mContents=findViewById(R.id.write_contents_EditText);
        mTitleLayout=findViewById(R.id.write_textInputLayout_title);
        mContentsLayOut=findViewById(R.id.write_textInputLayout_contents);
        mContentsLayOut.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference documentReference = db.collection("Board").document();
                if (mTitle.getText().toString().length() ==0||mContents.getText().toString().length() ==0) {
                    Toast.makeText(getApplicationContext(), "제목,내용을 입력하시오", Toast.LENGTH_LONG).show();
                } else {
                    String title = mTitle.getText().toString();
                    Log.d("양성열",uid);
                    String contents = mContents.getText().toString();
                    Date date=new Date();
                    String documentId=documentReference.getId();
                    FirebaseMessaging.getInstance().subscribeToTopic(documentId);//구독하기

                    final BoardInfo boardInfo = new BoardInfo(title, contents, uid,documentId,date,"0", Arrays.asList(""),0);
                    documentReference.set(boardInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"업로드성공",Toast.LENGTH_LONG).show();
                            setResult( 99);//99보냄



//                            LikeInfo likeInfo=new LikeInfo("","");
//                            documentReference.collection("Like").document(uid).set(likeInfo);
             //               documentReference.update("uidList", FieldValue.arrayUnion(uid));

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "파이어베이스 업로드실패", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onDestroy(){      //액티비티가 종료될 때의 메서드
        super.onDestroy();


    }


}
