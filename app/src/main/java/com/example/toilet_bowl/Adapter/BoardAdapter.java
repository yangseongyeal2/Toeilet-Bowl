package com.example.toilet_bowl.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private List<BoardInfo> mBoardInfo;

    ///////////////////////////클릭리스너
    public interface OnItemClickListener{
        void onitemClick(View v,int pos);
    }
    public BoardAdapter(List<BoardInfo> mBoardList){
      this.mBoardInfo=mBoardList;

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
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
            BoardInfo boardInfo=mBoardInfo.get(position);

            holder.mTitleTextView.setText(boardInfo.getTitle());
            holder.mContentTextView.setText(boardInfo.getContent());
    }

    @Override
    public int getItemCount() {
        return mBoardInfo.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mContentTextView;

       public BoardViewHolder(View itemView) {
           super(itemView);
           mTitleTextView=itemView.findViewById(R.id.item_title);
           mContentTextView=itemView.findViewById(R.id.item_contents);
           itemView.setOnClickListener(new View.OnClickListener() {//클릭했을때
               @Override
               public void onClick(View v) {
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
