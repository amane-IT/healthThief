package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class DiaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<Diary> diaryList;
    Context context;
    int position;


    /**
     * 생성자
     * @param diaryList
     */

    public DiaryListAdapter(ArrayList<Diary> diaryList, Context context) {
        this.diaryList=diaryList;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ViewHolder) viewHolder).onBind(diaryList.get(position));
    }

    // 전체 데이터 갯수 return
    @Override
    public int getItemCount() {
        int num = diaryList.size();
        Log.i("COUNT ITEM : ",String.valueOf(num));
        return this.diaryList.size();
    }

    // 데이터 목록 지우기
    public void clear(){
        int size = diaryList.size();
        diaryList.clear();
        notifyItemRangeRemoved(0,size);
    }


    //아이템 뷰를 저장하는 뷰 홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView meal;
        public TextView food;
        public TextView cal;
        public TextView unit;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            meal = itemView.findViewById(R.id.itemMeal);
            food = itemView.findViewById(R.id.itemFood);
            cal = itemView.findViewById(R.id.itemCal);
            unit = itemView.findViewById(R.id.itemCalUnit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // TODO : use pos.
                        Toast.makeText(context, "Touch Success", Toast.LENGTH_SHORT).show();
                        Log.i("List Item Click : ","You Touch the item!");
                    }

                }
            });
        }


        public void onBind(Diary item){
           image.setImageURI(Uri.parse(item.getImage()));
            meal.setText(item.getMeal());
            food.setText(item.getFood());
            cal.setText(item.getCal());
            unit.setText("Kcal");
         }
    }
}
