package com.liuh.bluetoothlearn;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 2018/2/25 13:44
 * Description:
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> devices;
    private OnItemClickListener clickListener;

    private int bondDeviceCount ;//已配对设备数量,用于显示顶部配对状态栏

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDevices(List<BluetoothDevice> devices) {
        this.devices = devices;

        //写在这个位置似乎不起作用,于是写在notifyDataSetChanged前面了
//        for (BluetoothDevice device : devices) {
//            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
//                bondDeviceCount++;
//            }
//        }
    }

    public void setBondDeviceCount(int bondDeviceCount) {
        this.bondDeviceCount = bondDeviceCount;
    }

    public MyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_btdevice, null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.itemBtdeviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(v);
            }
        });

        viewHolder.itemBtdeviceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onItemLongClicked(v);
                return true;
            }
        });

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);

        if (0 == position) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                //已配对设备
                holder.itemBtdeviceTitle.setVisibility(View.VISIBLE);
                holder.itemBtdeviceTitle.setText("已配对设备");
            } else {
                //可用设备
                holder.itemBtdeviceTitle.setVisibility(View.VISIBLE);
                holder.itemBtdeviceTitle.setText("可用设备");
            }
        }

        if (bondDeviceCount != 0 && (position == bondDeviceCount)) {
            holder.itemBtdeviceTitle.setVisibility(View.VISIBLE);
            holder.itemBtdeviceTitle.setText("可用设备");
        }

        if (device != null) {
            holder.itemBtdeviceName.setText(device.getName());
            holder.itemBtdeviceAddress.setText(device.getAddress());
            holder.itemBtdeviceView.setTag(device);
        }
    }

    @Override
    public int getItemCount() {
        if (devices != null && devices.size() > 0) {
            return devices.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_btdevice_title)
        TextView itemBtdeviceTitle;
        @BindView(R.id.item_btdevice_view)
        RelativeLayout itemBtdeviceView;
        @BindView(R.id.item_btdevice_img)
        ImageView itemBtdeviceImg;
        @BindView(R.id.item_btdevice_name)
        TextView itemBtdeviceName;
        @BindView(R.id.item_btdevice_address)
        TextView itemBtdeviceAddress;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
