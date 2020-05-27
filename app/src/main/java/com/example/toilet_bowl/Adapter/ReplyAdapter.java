package com.example.toilet_bowl.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.MainActivity;
import com.example.toilet_bowl.R;

import com.example.toilet_bowl.model.FirebaseUserModel;

import com.example.toilet_bowl.model.ReplyInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.like.LikeButton;
import com.like.OnLikeListener;




import java.util.Date;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
   // private List<String> documentIdList;
    private FirebaseUser mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private Context mContext;
    private OnItemClick mCallback;
    private DocumentReference documentReference_reply;//reply 전까지
    private EditText mEditText;

    ///////////////////////////클릭리스너
    public interface OnItemClickListener{
        void onitemClick(View v,int pos);
    }
    private BoardAdapter.OnItemClickListener mListener=null;
    public void setOnIemlClickListner(BoardAdapter.OnItemClickListener listner){
        this.mListener=listner;
    }
    ////////////////////////////////
    private List<ReplyInfo> mReplyList;

    public ReplyAdapter(List<ReplyInfo> mReplyList,DocumentReference documentReference_reply,Context context,OnItemClick listener,EditText mEditText) {//생성자
        this.mReplyList = mReplyList;
        this.documentReference_reply=documentReference_reply;
        this.mContext=context;
        this.mCallback=listener;
        this.mEditText=mEditText;
    }
    public ReplyAdapter(){}

    @NonNull
    @Override
    public ReplyAdapter.ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyAdapter.ReplyViewHolder holder, final int position) {
        final ReplyInfo replyInfo=mReplyList.get(position);
        holder.mContent.setText(replyInfo.getContent());
        holder.mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String documentId=replyInfo.getDocumentId();
                show_menu(v,position);

            }



        });

       mStore.collection("users")//닉네임 가져오기
               .document(replyInfo.getUid())
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               FirebaseUserModel fm=documentSnapshot.toObject(FirebaseUserModel.class);
               assert fm != null;
               holder.mNickname.setText(fm.getNickname());
           }
       });
       holder.mLikebutton.setOnLikeListener(new OnLikeListener() {
           @Override
           public void liked(LikeButton likeButton) {
               final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
               documentReference_reply.collection("reply").document(replyInfo.getDocumentId())
                       .update("uidLikelist",FieldValue.arrayUnion(mFirebaseUser.getUid())).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       holder.mLikecount.setText(String.valueOf(replyInfo.getUidLikelist().size()));
                       mStore.collection("users").document(replyInfo.getUid())//user에있는 좋아요 갯수 늘리기
                               .update("likecount",FieldValue.increment(1));
                   }
               });
           }

           @Override
           public void unLiked(LikeButton likeButton) {
               documentReference_reply.collection("reply").document(replyInfo.getDocumentId())
                       .update("uidLikelist",FieldValue.arrayRemove(mFirebaseUser.getUid()));
               holder.mLikecount.setText(String.valueOf(replyInfo.getUidLikelist().size()-1));
               mStore.collection("users").document(replyInfo.getUid())//user에있는 좋아요 갯수 늘리기
                       .update("likecount",FieldValue.increment(1));
           }
       });
        holder.mLikecount.setText(String.valueOf(replyInfo.getUidLikelist().size()-1));
        holder.mReplyimage.setOnClickListener(new View.OnClickListener() {//대 댓글을 실행하시겠습니까 ?
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad=new AlertDialog.Builder(mContext);
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("제목");
                ad.setMessage("대댓글을 실행하시겠습니까?");

                //yes or no
                ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(INPUT_METHOD_SERVICE);
//                        assert imm != null;
//                        imm.showSoftInput(mEditText, 0);
                        mEditText.requestFocus();//에디트 텍스트 활성화
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        holder.itemView.setBackgroundColor(Color.RED
                        );
                        //TODO 대댓글 클릭했을때 실행할 방법.






                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReplyList.size();
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent;
        private ImageView mMenu;
        private TextView mNickname;
        private LikeButton mLikebutton;
        private TextView mLikecount;
        private ImageView mReplyimage;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            mContent=itemView.findViewById(R.id.item_reply_content);
            mMenu=itemView.findViewById(R.id.item_reply_menu_imageView);
            mNickname=itemView.findViewById(R.id.item_nickname_level);
            mLikebutton=itemView.findViewById(R.id.item_reply_likebutton);
            mLikecount=itemView.findViewById(R.id.item_reply_likecount);
            mReplyimage=itemView.findViewById(R.id.item_reply);



            itemView.setOnClickListener(new View.OnClickListener() {//클릭했을때
                @Override
                public void onClick(View v) {//들어가는 기능 detail로
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onitemClick(v,pos);
                        }
                    }
                }
            });

        }
    }

    private void show_menu(View v, final int position) {
        final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove_superficially:
                        //Toast.makeText(mContext, "무슨수정이냐 그냥 쳐 삭제해라", Toast.LENGTH_LONG).show();
                        // mBoardInfo.remove(position);
                        return true;
                    case R.id.remove_firebase:
                        Date date=new Date();
                        if(mReplyList.get(position).getUid().equals(mFirebaseUser.getUid())){
                            documentReference_reply.collection("reply").document(mReplyList.get(position).getDocumentId())
                                    .update("deleted_at",date.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext, "파이어베이스 deleted_at 현재신간으로 업데이트", Toast.LENGTH_LONG).show();
                                    mCallback.onClick("실시간 댓글 삭제");//삭제하면 콜백함수로 양성열 보내짐.//이 어댑터에서 보낼 정보는 이렇게쓰면댐
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "파이어베이스 deleted_at 업데이트실패", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(mContext, "너가 올린 댓글이 아니다", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_showup, popup.getMenu());
        popup.show();

    }

}
