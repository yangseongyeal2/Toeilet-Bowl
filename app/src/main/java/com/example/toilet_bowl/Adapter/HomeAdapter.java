package com.example.toilet_bowl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;

import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder> {
    private List<BoardInfo> mBoardInfoList;

    public HomeAdapter(List<BoardInfo> mBoardInfoList) {
        this.mBoardInfoList = mBoardInfoList;
    }
    ///////////////////////////클릭리스너
    public interface OnItemClickListener{
        void onitemClick(View v,int pos);
    }
    private static OnItemClickListener mListener=null;
    public void setOnIemlClickListner(HomeAdapter.OnItemClickListener listner){
        mListener=listner;
    }
    ////////////////////////////////

    @NonNull
    @Override
    public HomeAdapter.HomeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        assert inflater != null;
        View view = inflater.inflate(R.layout.item_home, parent, false) ;
        return new HomeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.HomeAdapterViewHolder holder, int position) {
        BoardInfo boardInfo=mBoardInfoList.get(position);
        holder.mTitleView.setText(boardInfo.getTitle());
        holder.mLikecount.setText(String.valueOf(boardInfo.getUidList().size()-1));
        holder.mReplycount.setText(String.valueOf(boardInfo.getReplycount()));
        holder.mViewcount.setText(String.valueOf(boardInfo.getViewcount()));
        String date=boardInfo.getDate().toString();
        String date1=date.substring(11,16);
        String date2=date.substring(0,13)+" "+date.substring(30,34);
        holder.mCreated_at.setText(date1);


    }

    @Override
    public int getItemCount() {
        return mBoardInfoList.size();
    }

    static class HomeAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private TextView mLikecount;
        private TextView mReplycount;
        private TextView mViewcount;
        private TextView mCreated_at;
        HomeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView=itemView.findViewById(R.id.item_home_title);
            mLikecount=itemView.findViewById(R.id.item_home_like_TextView);
            mReplycount=itemView.findViewById(R.id.item_home_replycount_TextView);
            mViewcount=itemView.findViewById(R.id.item_home_viewcount_TextView);
            mCreated_at=itemView.findViewById(R.id.item_home_created_at);

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
}

