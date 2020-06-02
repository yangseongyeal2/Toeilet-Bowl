package com.example.toilet_bowl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.toilet_bowl.Adapter.SliderAdapter.*;

public class SliderAdapter extends SliderViewAdapter<SliderAdapterVH> {
    private Context context;
    private List<Integer> mSliderItems = new ArrayList<>();


    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapter.SliderAdapterVH(inflate);
    }
    public void addItem(int a) {
        this.mSliderItems.add(a);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        int image=mSliderItems.get(position);
        //String path=mSliderItems.get(position);
        Glide.with(viewHolder.itemView)//.load("https://firebasestorage.googleapis.com/v0/b/testformain.appspot.com/o/image%2F%ED%8C%8C%EC%9D%B4%EC%96%B4%EB%B2%A0%EC%9D%B4%EC%8A%A4%EA%B3%84%EC%A0%95%EC%97%B0%EB%8F%99%EB%B0%94%EA%BE%B8%EB%8A%94%EB%B0%A9%EB%B2%95.PNG?alt=media&token=681df0a3-e503-45ca-95c1-c656a351cfe2")
                .load(image)
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
