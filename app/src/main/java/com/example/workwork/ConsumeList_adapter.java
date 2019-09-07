package com.example.workwork;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

interface OnRecyclerItemClickListener {
    //RecyclerView的点击事件，将信息回调给view
    void onItemClick(View Position, int mConsumeList);
}
public class ConsumeList_adapter extends RecyclerView.Adapter<ConsumeList_adapter.ViewHolder>{
        private List<ConsumeItem> mConsumeList;
        private OnRecyclerItemClickListener MyClickItemLisenter;
        static  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            LinearLayout item_Layout;
            TextView ItemContent;
            TextView ItemType;
            TextView ItemValue;
            TextView ItemDate;
            SimpleDateFormat sdf;
            List<Integer> colorList=new ArrayList<>();
            OnRecyclerItemClickListener mListener;
            String[] Types={"饮食","娱乐","网络通信","购物","生活水电"};
            public ViewHolder(View view,OnRecyclerItemClickListener mListener) {
                super(view);
                item_Layout=view.findViewById(R.id.Item_Layout);
                ItemContent=view.findViewById(R.id.Item_Content);
                ItemType=view.findViewById(R.id.Item_Type);
                ItemDate=view.findViewById(R.id.Item_Date);
                ItemValue=view.findViewById(R.id.Item_Value);
                colorList.add(Color.rgb(155, 187, 90));
                colorList.add(Color.rgb(191, 79, 75));
                colorList.add(Color.rgb(242, 167, 69));
                colorList.add(Color.rgb(60, 173, 213));
                colorList.add( Color.rgb(90, 79, 88));
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                view.setOnClickListener(this);
                this.mListener=mListener;
            }

            @Override
            public void onClick(View v) {
                mListener.onItemClick(v,getAdapterPosition());
            }//点击事件实现的接口
        }

        public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.MyClickItemLisenter = listener;
        }//暴露给外部的方法


        public ConsumeList_adapter(List<ConsumeItem> mConsumeList)
        {
            this.mConsumeList=mConsumeList;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumeitem,parent,false);
            return new ViewHolder(view,MyClickItemLisenter);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder Holder, int position) {
        ConsumeItem item=mConsumeList.get(position);
        Holder.ItemContent.setText(item.getContent());
        Holder.ItemValue.setText(String.valueOf(item.getValue())+"元");
        Holder.ItemType.setText(Holder.Types[item.getType()]);
        Holder.ItemDate.setText(item.gettime());
        Holder.item_Layout.setBackgroundColor(Holder.colorList.get(item.getType()));
    }

    @Override
    public int getItemCount() {
        return mConsumeList.size();
    }

}
