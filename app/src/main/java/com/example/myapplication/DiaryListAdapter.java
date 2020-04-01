package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
    Dialog diaryDialog;

    /*
    // 커스텀 리스너 정의
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    private OnItemClickListener diaryListener = null;

    // 커스텀 리스너 객체 전달 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.diaryListener = listener ;
    }
     */

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

            /*
            //커스텀 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        //리스너 객체의 메소드 호출
                        if (diaryListener != null) {
                            diaryListener.onItemClick(v, pos);
                            Toast.makeText(context.getApplicationContext(),"Adapter Custom Touch Item",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
             */

            //디폴트 리스터
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조.
                        Diary diary = diaryList.get(pos);
                        String content = diary.getDiary();
                        Toast.makeText(context.getApplicationContext(),content,Toast.LENGTH_SHORT).show();
                        String carbon = diary.getCarbo() + " g";
                        String protein = diary.getProtein()+ " g";
                        String fat = diary.getFat()+ " g";
                        Toast.makeText(context.getApplicationContext(),"Adapter Touch Item",Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                        final View diaryDialog = inflater.inflate(R.layout.showing_diary_dialog, null);
                        TextView con = diaryDialog.findViewById(R.id.dialogDiary_content);
                        TextView car = diaryDialog.findViewById(R.id.diary_carbon);
                        TextView pro = diaryDialog.findViewById(R.id.diary_protein);
                        TextView fatt = diaryDialog.findViewById(R.id.diary_fat);

                        con.setText(content);
                        car.setText(carbon);
                        pro.setText(protein);
                        fatt.setText(fat);

                        builder.setView(diaryDialog);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // TODO : use item.

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
