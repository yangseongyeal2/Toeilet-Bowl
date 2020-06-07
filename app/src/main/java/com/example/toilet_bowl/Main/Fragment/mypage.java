package com.example.toilet_bowl.Main.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.toilet_bowl.Main.MainActivity;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.FirebaseUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class mypage extends Fragment {
    private View view;
    private TextView  mEmail;
    private TextView  mNickname;
    private TextView  mUserNickname;
    private TextView  mLikecount,mCreatedAt;
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mypage,container,false);
        mEmail=view.findViewById(R.id.mypage_email);
        mNickname=view.findViewById(R.id.mypage_Nickname);
        mUserNickname=view.findViewById(R.id.mypage_UserNickname);
        mLikecount=view.findViewById(R.id.mypage_likecount);
        mCreatedAt=view.findViewById(R.id.mypage_created_at);
        RetrieveALL();
        view.findViewById(R.id.mypage_changeNick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad=new AlertDialog.Builder(getContext());
                ad.setMessage("바꾸고 싶은 닉네임 입력");
                final EditText et=new EditText(getContext());
                et.setHint("13글자 이내로 작성하시오");
                ad.setView(et);
                ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        String result=et.getText().toString();
                        if(result.length()>13){
                            Toast.makeText(getContext(),"13글자이하로 작성하시오",Toast.LENGTH_LONG).show();
                        }else{
                            mStore.collection("users").document(firebaseUser.getUid())
                                    .update("userNickName",result)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            RetrieveALL();
                                            dialog.dismiss();
                                        }
                                    });
                        }

                    }
                });

                ad.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//
                        dialog.dismiss();
                    }
                });
                ad.show();

            }
        });
        return view;
    }

    private void RetrieveALL() {
        assert firebaseUser != null;
        mStore.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult()!=null){
                    FirebaseUserModel fm=task.getResult().toObject(FirebaseUserModel.class);
                    mEmail.setText("사용중인 Email:"+firebaseUser.getEmail());
                    assert fm != null;
                    mNickname.setText("현재 레벨"+fm.getNickname());
                    mUserNickname.setText("닉네임:"+fm.getUserNickName());
                    mLikecount.setText("좋아요 받은 갯수:"+String.valueOf(fm.getLikecount()));
                    String date="생성된 날짜: "+fm.getDate().toString().substring(0,13);
                    mCreatedAt.setText(date);
                }


            }
        });
    }
}
