package com.example.toilet_bowl.Start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.smarteist.autoimageslider.SliderView;

public class StartActivity extends AppCompatActivity {
    SliderAdapter adapter;
    SliderView sliderView;
    private EditText Enickname;

    private int[] images = {R.drawable.one, R.drawable.two, R.drawable.three};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sliderView=findViewById(R.id.start_sliderView);
        Enickname=findViewById(R.id.start_UserNickname);
        adapter = new SliderAdapter(this);
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
}
