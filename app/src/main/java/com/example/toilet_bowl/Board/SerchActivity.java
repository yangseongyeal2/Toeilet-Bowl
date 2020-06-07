package com.example.toilet_bowl.Board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SerchActivity extends AppCompatActivity implements OnItemClick {
    private List<BoardInfo> mBoardList;
    private final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    private TextInputEditText mTextInputEditText;
    private TextInputLayout mTextInputLayout;
    private ProgressDialog loadingbar;
    private BoardAdapter mBoardAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        loadingbar=new ProgressDialog(this);
        mRecyclerView=findViewById(R.id.serch_RecyclerView);
        mTextInputEditText=findViewById(R.id.serch_TextInputEditText);
        mTextInputLayout=findViewById(R.id.serch_TextInputLayout);
        mTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=mTextInputEditText.getText().toString();
                retriveSerch(content);
                mTextInputEditText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(mTextInputEditText.getWindowToken(), 0);
            }
        });

    }

    private void retriveSerch(String content) {
        FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        loadingbar.setTitle("Serching");
        loadingbar.setMessage("Serching");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        mBoardList=new ArrayList<>();
        mStore.collection("Board")
                .whereEqualTo("title",content)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult()!=null){
                    for(QueryDocumentSnapshot data: task.getResult()){
                        BoardInfo boardInfo=data.toObject(BoardInfo.class);
                        Log.d("서치",boardInfo.getContent());
                        mBoardList.add(boardInfo);
                        Log.d("서치",String.valueOf(mBoardList.size()));
                    }
                    if(mBoardList.size()==0){//찾는 데이터가 없을떄
                        Toast.makeText(getApplicationContext(),"데이터가없습니다",Toast.LENGTH_SHORT).show();
                    }
                    mBoardAdapter=new BoardAdapter(mBoardList,SerchActivity.this, firebaseUser,SerchActivity.this);
                    mBoardAdapter.setOnIemlClickListner(new BoardAdapter.OnItemClickListener() {//Detail 액티비티로 이동
                        @Override
                        public void onitemClick(View v, int pos) {
                            Intent intent=new Intent(getApplicationContext(),DetailActivity.class);
                            intent.putExtra("DocumentId",mBoardList.get(pos).getDocumentId());
                            startActivity(intent);
                        }
                    });
                    mRecyclerView.setAdapter(mBoardAdapter);

                }else{
                    Log.d("서치","데이터가 없다");
                }

                loadingbar.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("서치","불러오기 실패");
            }
        });
    }

    @Override
    public void onClick(String value) {

    }
}
