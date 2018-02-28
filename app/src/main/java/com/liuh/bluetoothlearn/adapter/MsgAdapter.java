package com.liuh.bluetoothlearn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuh.bluetoothlearn.R;
import com.liuh.bluetoothlearn.model.Msg;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huan on 2018/2/28.
 * 参考http://www.jb51.net/article/118882.htm。看这篇文章思路十分简单，目前制作一种暂选方案。
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private Context context;

    private List<Msg> mMsgList;

    public MsgAdapter(Context context) {
        this.context = context;
    }

    public void setmMsgList(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgAdapter.ViewHolder holder, int position) {
        if (mMsgList != null) {
            Msg msg = mMsgList.get(position);

            if (Msg.TYPE_RECEIVED == msg.getType()) {
                holder.llLeft.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.GONE);
                holder.tvLeftMsg.setText(msg.getContent());

            } else if (Msg.TYPE_RECEIVED == msg.getType()) {
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.VISIBLE);
                holder.tvRightMsg.setText(msg.getContent());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMsgList == null)
            return 0;
        else return mMsgList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_left)
        LinearLayout llLeft;
        @BindView(R.id.tv_left_msg)
        TextView tvLeftMsg;
        @BindView(R.id.ll_right)
        LinearLayout llRight;
        @BindView(R.id.tv_right_msg)
        TextView tvRightMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
