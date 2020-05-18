package com.example.toilet_bowl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder>  {
    private List<BoardInfo> mBoardInfo;
    private Context mContext;
    private FirebaseUser mFirebaseUser;
    private OnItemClick mCallback;

//    public BoardAdapter(List<BoardInfo> mBoardList){
//        this.mBoardInfo=mBoardList;
//    }


    public BoardAdapter(List<BoardInfo> mBoardInfo, Context mContext,FirebaseUser mFirebaseUser,OnItemClick listener) {
        this.mBoardInfo = mBoardInfo;
        this.mContext = mContext;
        this.mFirebaseUser=mFirebaseUser;
        this.mCallback=listener;
    }

    ///////////////////////////클릭리스너
    public interface OnItemClickListener{
        void onitemClick(View v,int pos);
    }
    private OnItemClickListener mListener=null;
    public void setOnIemlClickListner(OnItemClickListener listner){
        this.mListener=listner;
    }
    ////////////////////////////////


    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardViewHolder holder, final int position) {
            final BoardInfo boardInfo=mBoardInfo.get(position);

            holder.mTitleTextView.setText(boardInfo.getTitle());
            holder.mContentTextView.setText(boardInfo.getContent());
            holder.mImageView_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_menu(v,boardInfo.getDocumentId(),position);
                }

                private void show_menu(View v, String documentId, final int position) {
                    final FirebaseFirestore mStore=FirebaseFirestore.getInstance();
                    PopupMenu popup = new PopupMenu(mContext, v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.remove_superficially:
                                    Toast.makeText(mContext, "무슨수정이냐 그냥 쳐 삭제해라", Toast.LENGTH_LONG).show();
                                   // mBoardInfo.remove(position);
                                    return true;
                                case R.id.remove_firebase:
                                    DocumentReference documentReference=mStore.collection("Board").document(boardInfo.getDocumentId());
                                   Date date=new Date();
                                    if(boardInfo.getUid().equals(mFirebaseUser.getUid())){
                                        documentReference.update("deleted_at",date.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(mContext, "파이어베이스 deleted_at 현재신간으로 업데이트", Toast.LENGTH_LONG).show();
                                                mCallback.onClick("실시간 데이터 삭제");//삭제하면 콜백함수로 양성열 보내짐.//이 어댑터에서 보낼 정보는 이렇게쓰면댐
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(mContext, "파이어베이스 deleted_at 업데이트실패", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(mContext, "너가 올린 게시물이 아니다", Toast.LENGTH_LONG).show();
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
            });

    }

    @Override
    public int getItemCount() {
        return mBoardInfo.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mContentTextView;
        private ImageView mImageView_menu;

       public BoardViewHolder(View itemView) {
           super(itemView);
           mTitleTextView=itemView.findViewById(R.id.item_title);
           mContentTextView=itemView.findViewById(R.id.item_contents);
           mImageView_menu=itemView.findViewById(R.id.item_ImageView_menu);
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
    public void addItem(BoardInfo boardInfo){
        mBoardInfo.add(boardInfo);
        notifyDataSetChanged();
    }
}
