package com.example.toilet_bowl.Main.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private TextView  mLikecount;
    private final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mypage,container,false);
        mEmail=view.findViewById(R.id.mypage_email);
        mNickname=view.findViewById(R.id.mypage_Nickname);
        mUserNickname=view.findViewById(R.id.mypage_UserNickname);
        mLikecount=view.findViewById(R.id.mypage_likecount);


        RetrieveALL();



        return view;
    }

    private void RetrieveALL() {
        mStore.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult()!=null){
                    FirebaseUserModel fm=task.getResult().toObject(FirebaseUserModel.class);
                    mEmail.setText(firebaseUser.getEmail());
                    mNickname.setText(fm.getNickname());
                    mUserNickname.setText(fm.getUserNickName());
                    mLikecount.setText(String.valueOf(fm.getLikecount()));

                }


            }
        });
    }
}
