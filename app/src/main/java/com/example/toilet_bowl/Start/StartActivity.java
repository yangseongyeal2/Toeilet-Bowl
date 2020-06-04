package com.example.toilet_bowl.Start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toilet_bowl.Adapter.SliderAdapter;
import com.example.toilet_bowl.Adapter.SliderAdapterExample;
import com.example.toilet_bowl.Login.LoginActivity;
import com.example.toilet_bowl.Main.MainActivity;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.SliderItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.smarteist.autoimageslider.SliderView;

public class StartActivity extends AppCompatActivity {
    SliderAdapter adapter;
    SliderView sliderView;
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private EditText Enickname;
    private ProgressDialog loadingbar;

    private int[] images = {R.drawable.one, R.drawable.two, R.drawable.three};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sliderView=findViewById(R.id.start_sliderView);
        Enickname=findViewById(R.id.start_UserNickname);
        loadingbar=new ProgressDialog(StartActivity.this);
        adapter = new SliderAdapter(this);
        check();

        for(int a:images){
            adapter.addItem(a);
        }
        sliderView.setSliderAdapter(adapter);

        findViewById(R.id.start_gotomain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Enickname.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"닉네임을 입력하시오:",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                    String nickname=Enickname.getText().toString();
                    intent.putExtra("UserNickName",nickname);
                    startActivity(intent);

                }

            }
        });


    }
    private void check(){
        Enickname.setClickable(false);
        loadingbar.setTitle("Set profile image");
        loadingbar.setMessage("서버에 접속중");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        mStore.collection("user").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Enickname.setClickable(true);
                Toast.makeText(getApplicationContext(),"서버에이미 정보가 있음",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                loadingbar.dismiss();
                String nickname=Enickname.getText().toString();
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();
            }
        });
    }
}
