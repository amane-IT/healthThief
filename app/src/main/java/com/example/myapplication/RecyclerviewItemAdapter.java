package com.example.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerviewItemAdapter extends RecyclerView.Adapter<RecyclerviewItemAdapter.ViewHolder> {

    private ArrayList<String> mData = null;

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ViewHolder(View itemView){
            super(itemView);
        }

    }

    // 생성자에서 데이터 리스트 객체를 전달받음
    RecyclerviewItemAdapter(ArrayList<String> list){
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체를 생성해 리턴
    @Override
    public RecyclerviewItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false) ;
        RecyclerviewItemAdapter.ViewHolder vh = new RecyclerviewItemAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(RecyclerviewItemAdapter.ViewHolder holder, int position) {
        String text = mData.get(position);
    }

    // getItemCount() - 전체 데이터갯수 리턴
    @Override
    public int getItemCount(){
        return mData.size();
    }


}
