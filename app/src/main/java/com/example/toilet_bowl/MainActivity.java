package com.example.toilet_bowl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private SignInButton loginbtn_google;//구글 로그인 버튼
    private FirebaseAuth mAuth;//파이어 베이스 인증 객체
    private GoogleApiClient googleApiClient;//구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE=100;//구글 로그인 결과 코드


    @Override
    protected void onCreate(Bundle savedInstanceState) {//앱이 실행될때 처음 수행되는 곳
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        mAuth=FirebaseAuth.getInstance();
        loginbtn_google=findViewById(R.id.login_google);
        loginbtn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,REQ_SIGN_GOOGLE);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try{
            updateUI(currentUser);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(this,"로그인이 이미 되어있습니다 :"+currentUser.toString(),Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),BoardActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//구글 로그인 인증을 요청했을때 결과값을 되돌려 받는 곳.
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_SIGN_GOOGLE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){//인증결과 성공
                GoogleSignInAccount account=result.getSignInAccount();//account는 구글로그인 정보를 담고있다.
                resultLogin(account);//로그인 결과값 출력수행하는 메소드
            }
        }
    }

    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){//로그인 이 성공
                          Toast.makeText(MainActivity.this,"로그인성공",Toast.LENGTH_LONG).show();
                          Intent intent=new Intent(getApplicationContext(),BoardActivity.class);
                          intent.putExtra("nickName",account.getDisplayName());
                          intent.putExtra("photoURL",String.valueOf(account.getPhotoUrl()));


                          startActivity(intent);
                      }else {//로그인이 실패
                          Toast.makeText(MainActivity.this,"로그인실패",Toast.LENGTH_LONG).show();
                      }
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
