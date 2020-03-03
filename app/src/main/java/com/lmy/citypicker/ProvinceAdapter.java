package com.lmy.citypicker;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ViewHolder> {

    private List<ProvinceInfo> mFruitList;
    private int lastPosition = 0;

    public  ProvinceAdapter (List<ProvinceInfo> fruitList){
        mFruitList = fruitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.province_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("recycle", "--- " + position);
        ProvinceInfo province = mFruitList.get(position);
        holder.fruitName.setText(province.province);
        if (lastPosition == position) {
            holder.fruitName.setBackgroundColor(Color.parseColor("#cccccc"));
        }else {
            holder.fruitName.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.fruitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(position);
                    lastPosition = position;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFruitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView fruitName;

        public ViewHolder (View view)
        {
            super(view);
            fruitName = (TextView) view.findViewById(R.id.tv_province);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }

}
