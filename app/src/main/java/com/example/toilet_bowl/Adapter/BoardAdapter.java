package com.example.toilet_bowl.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private List<BoardInfo> mBoardInfo=new ArrayList<>();
    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {
        BoardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
