package com.example.toilet_bowl.Board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.example.toilet_bowl.Adapter.SliderAdapterExample;
import com.example.toilet_bowl.Main.Fragment.Board;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;
import com.example.toilet_bowl.model.SliderItem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private static final int RESULT_LOAD_IMAGE = 1;
    private ProgressDialog loadingbar;

    private ArrayList<String> imageStringList;
    private List<Uri> imageUriList;
    private SliderAdapterExample sliderAdapterExample;
    private ArrayList<String> mDownloadURI;
    private StorageReference mStorageRef;

    ///
    private String title;
    private String contents;
    private String documentId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = db.collection("Board").document();
        documentId=documentReference.getId();
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
        loadingbar=new ProgressDialog(this);
        findViewById(R.id.write_addphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
//이미지슬라이더
        imageStringList=new ArrayList<>();//이거없으면안댐
        imageUriList=new ArrayList<>();//이거없으면안댐
        sliderAdapterExample = new SliderAdapterExample(this);
        SliderView sliderView=findViewById(R.id.write_sliderview);
        sliderView.setSliderAdapter(sliderAdapterExample);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(false);

        mContentsLayOut.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();//스토리지에 업로드
            }
        });

    }

    private void uploadStore(final BoardInfo boardInfo) {
        loadingbar.setTitle("Set profile image");
        loadingbar.setMessage("pleas wait업로딩중");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

//                final DocumentReference documentReference = db.collection("Board").document();
                    documentReference.set(boardInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"업로드성공",Toast.LENGTH_LONG).show();
                            setResult( 99);//99보냄
                            FirebaseMessaging.getInstance().subscribeToTopic(documentId);//구독하기
                            loadingbar.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "파이어베이스 업로드실패", Toast.LENGTH_LONG).show();
                            loadingbar.dismiss();
                            finish();
                        }
                    });

    }



    private void uploadFile() {
        //업로드할 파일이 있으면 수행

        mDownloadURI = new ArrayList<>();
        Date time = new Date();
        if (mTitle.getText().toString().length() ==0||mContents.getText().toString().length() ==0){
            Toast.makeText(getApplicationContext(), "제목,내용을 입력하시오", Toast.LENGTH_LONG).show();
        }else{
            title=mTitle.getText().toString();
            contents=mContents.getText().toString();

            mStorageRef = FirebaseStorage.getInstance().getReference("image");//loaction 설정
            if(imageUriList.size()!=0){
                for (int i = 0; i < imageUriList.size(); i++) {
                    Uri imageUri = imageUriList.get(i);
                    final StorageReference imgref = mStorageRef.child(imageUri.getLastPathSegment()+time.toString()+uid.toString());//고유하게 저장하기위해서.
                    UploadTask uploadTask = imgref.putFile(imageUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return imgref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                mDownloadURI.add(downloadUri.toString());
                                if(mDownloadURI.size()==imageUriList.size()){
                                    String dynamiclink="test";
                                    Date date=new Date();
                                    BoardInfo boardInfo=new BoardInfo(
                                            title
                                            ,contents
                                            ,uid
                                            ,documentId
                                            ,date
                                            ,"0"
                                            ,Arrays.asList("")
                                            ,0
                                            ,0
                                            ,mDownloadURI
                                    );
                                    Log.d("성공","성공");
                                    uploadStore(boardInfo);

                                }
                            }
                        }
                    });

                }//for문끝  스토리지에 저장만함
            }else {
                BoardInfo boardInfo=new BoardInfo(
                        title
                        ,contents
                        ,uid
                        ,documentId
                        ,new Date()
                        ,"0"
                        ,Arrays.asList("")
                        ,0
                        ,0
                        ,mDownloadURI
                );
                uploadStore(boardInfo);

            }


        }


    }





    @Override
    protected void onDestroy(){      //액티비티가 종료될 때의 메서드
        super.onDestroy();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        //Log.i("result", String.valueOf(resultCode));
        if (resultCode == Activity.RESULT_OK) {
            //ArrayList imageList = new ArrayList<>();
            // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
            if (data.getClipData() == null) {
                // Log.i("1. single choice", String.valueOf(data.getData()));
                imageStringList.add(String.valueOf(data.getData()));
                imageUriList.add(data.getData());
                sliderAdapterExample.addItem(new SliderItem(String.valueOf(data.getData())));
            } else {
                ClipData clipData = data.getClipData();
                if (clipData.getItemCount() > 10) {
                    Toast.makeText(WriteActivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 멀티 선택에서 하나만 선택했을 경우
                else if (clipData.getItemCount() == 1) {
                    String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                    imageStringList.add(dataStr);
                    imageUriList.add(data.getData());
                    sliderAdapterExample.addItem(new SliderItem(dataStr));
                } else if (clipData.getItemCount() > 1 && clipData.getItemCount() < 10) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        //     Log.i("3. single choice", String.valueOf(clipData.getItemAt(i).getUri()));
                        imageStringList.add(String.valueOf(clipData.getItemAt(i).getUri()));
                        imageUriList.add(clipData.getItemAt(i).getUri());
                        sliderAdapterExample.addItem(new SliderItem(String.valueOf(clipData.getItemAt(i).getUri())));
                    }
                }
            }
        } else {
            Toast.makeText(WriteActivity.this, "사진 선택을 취소하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
