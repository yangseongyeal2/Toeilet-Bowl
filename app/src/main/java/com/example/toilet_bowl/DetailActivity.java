package com.example.toilet_bowl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Adapter.ReplyAdapter;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.model.BoardInfo;
import com.example.toilet_bowl.model.ReplyInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnItemClick {
    private static final String TAG="DetailActivity";
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private TextView mTitle;
    private TextView mContent;
    private TextInputEditText mEditText;
    private DocumentReference documentReference;
    private ReplyAdapter mReplyAdapter;
    private RecyclerView mRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTitle=findViewById(R.id.detail_title);
        mContent=findViewById(R.id.detail_content);
        mEditText=findViewById(R.id.detail_reply_EditText);
        mRecyclerView=findViewById(R.id.detail_recyclerview);
        TextInputLayout mTextInputLayout = findViewById(R.id.detail_TextIputLayout);
        final String mDocumentId = getIntent().getStringExtra("DocumentId");
        assert mDocumentId != null;
        documentReference=mStore.collection("Board").document(mDocumentId);
        retreiveDocumentReference(documentReference);
        retreiveReply(documentReference);
        mRecyclerView.setAdapter(mReplyAdapter);

        mTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {//에딧텍스트 업로드
            @Override
            public void onClick(View v) {
                final DocumentReference replyDocumentreference = documentReference.collection("reply").document();
                if(mEditText.getText()!=null){
                    String reply_string = mEditText.getText().toString();
                    assert firebaseUser != null;
                    ReplyInfo replyInfo=new ReplyInfo(firebaseUser.getUid(),"0",reply_string,new Date(),replyDocumentreference.getId());
                    //mReplyInfoList.add(replyInfo);
                    replyDocumentreference.set(replyInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "success");
                            //mDetailAdapter.addItem(replyInfo);
                            mEditText.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                            retreiveReply(documentReference);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"댓글을 입력하시오",Toast.LENGTH_LONG).show();
                }
            }
        });



    }
    private void retreiveDocumentReference(DocumentReference documentReference) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BoardInfo boardInfo = documentSnapshot.toObject(BoardInfo.class);
                assert boardInfo != null;
                mTitle.setText(boardInfo.getTitle());
                mContent.setText(boardInfo.getContent());
            }
        });

    }
    private void retreiveReply(final DocumentReference documentReference){
        CollectionReference collectionReference=documentReference.collection("reply");
        collectionReference
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ReplyInfo> list=new ArrayList<>();
                if(task.getResult()!=null) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        ReplyInfo replyInfo=documentSnapshot.toObject(ReplyInfo.class);
                        Log.d(TAG, "댓글불러오기 성공");
                        assert replyInfo != null;
                        if(replyInfo.getDeleted_at().equals("0")){
                            list.add(replyInfo);
                        }
                    }
                    mReplyAdapter=new ReplyAdapter(list,documentReference,DetailActivity.this,DetailActivity.this);
                    mRecyclerView.setAdapter(mReplyAdapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"댓글불러오기 실패");
            }
        });
    }

    @Override
    public void onClick(String value) {//어댑터에서 클릭후 오는 정보는 여기로옴
        // value this data you receive when increment() / decrement()
        if(value.equals("실시간 댓글 삭제")){
            retreiveReply(documentReference);
        }
    }
}
