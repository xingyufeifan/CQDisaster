package com.nandi.cqdisaster.examine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.nandi.cqdisaster.R;
import com.nandi.cqdisaster.examine.bean.TaskInfoBean;

import java.util.List;

/**
 * Created by lemon on 2017/3/1.
 *  我的任务
 */

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.MyViewHolder> implements View.OnClickListener {

    private List<TaskInfoBean> mDatas;
    private Context context;
    private LayoutInflater inflater;

    public MyTaskAdapter(Context context, List<TaskInfoBean> mDatas) {
        this.context = context;
        this.mDatas=mDatas;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvTask.setText("调查任务-"+mDatas.get(position).getmDisaster());
        if (mDatas.get(position).getmAddress().equals("")){
            holder.tvAddress.setText("地址： " +" 地址未填写");
        }else{
            holder.tvAddress.setText("地址： " + mDatas.get(position).getmAddress());
        }
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(mDatas.get(position));
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(TaskInfoBean)v.getTag());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddress;
        TextView tvTask;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTask = (TextView) itemView.findViewById(R.id.tv_task);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
        }
    }


    /**
     * 设置item点击监听
     */
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, TaskInfoBean taskInfoBean);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
