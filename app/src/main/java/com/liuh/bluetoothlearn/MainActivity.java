package com.liuh.bluetoothlearn;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * BluetoothAdapter 本地设备的蓝牙适配器.
 * 可以执行基本的蓝牙任务,例如启动设备发现,查询配对的设备列表,使用已知的mac地址实例化一个BluetoothDevice类,
 * 并创建一个BluetoothServerSocket监听来自其他设备的连接请求.
 * <p>
 * 硬件标识符访问权
 * 为了给用户提供更严格的数据保护,从android 6.0版本开始,对于使用WLAN API和Bluetooth API的应用,
 * android移除了对设备本地硬件标识符的编程访问权.WifiInfo.getMacAddress()方法和BluetoothAdapter.getAddress()
 * 方法方法会返回常量02:00:00:00:00:00.
 * 要想通过蓝牙和WLAN扫描访问附近外部设备的硬件标识符,应用必须拥有ACCESS_FINE_LOCATION或ACCESS_COARSE_LOCATION权限.
 * <p>
 * 当运行android 6.0(API级别23)的设备发起后台WLAN或蓝牙扫描时,在外部设备看来,该操作的发起来源是一个随机化的NAC地址.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_bluetooth_device)
    RecyclerView rvBluetoothDevice;

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ENABLE_BT = 100;

    private static final int SCAN_PERIOD = 10000;
    //指定设备
    private static final String DESIGNATED_BLUETOOTH_NAME = "Galaxy Nexus";

    private BluetoothAdapter mBluetoothAdapter;

    boolean mScanning = false;//蓝牙扫描状态

    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<BluetoothDevice>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    BluetoothAdapter.LeScanCallback mLeScanCallback;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1000;
    private AlertDialog.Builder builder;

    public PermissionListener mPermissionListener;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        myAdapter = new MyAdapter(this);
        myAdapter.setDevices(bluetoothDeviceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvBluetoothDevice.setLayoutManager(linearLayoutManager);
        rvBluetoothDevice.setAdapter(myAdapter);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //如果检测到蓝牙没有开启,尝试开启蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (mBluetoothAdapter.isEnabled()) {
            //当其他的设备进行蓝牙扫描时扫描不到此设备,可能是因为此设备对外不可见或者距离较远,需要设置其对外蓝牙可见,这样才能被搜索到.
            //可见时间默认值为120s,最多可设置为300s.
            if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivity(discoverableIntent);
            }
        }

        //获取本机蓝牙名称和蓝牙地址
        String localBtName = mBluetoothAdapter.getName();
        String localBtAddress = mBluetoothAdapter.getAddress();
        Log.e(TAG, "bluetooth name : " + localBtName + ", address : " + localBtAddress);

        printBluetoothDevice();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //
            requestRuntimePermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, new MyRequestPermission());
        } else {
            mBluetoothAdapter.startDiscovery();
        }


//        BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                bluetoothDeviceList.add(device);
//                Log.e(TAG, "run : scanning...");
//            }
//        };
//
//        mBluetoothAdapter.startLeScan(callback);

        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //扫描结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothReceiver, filter);


//        mBluetoothAdapter.startDiscovery();

//        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//            }
//        };
//
//        scanLeDevice(true);

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    for (BluetoothDevice bluetoothDevice : bluetoothDeviceList) {
                        Log.e(TAG, bluetoothDevice.getName() + "----" + bluetoothDevice.getAddress());
                    }

                }
            }, SCAN_PERIOD);
            mScanning = true;

            // 定义一个回调接口供扫描结束处理
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "mBluetoothReceiver action : " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //每扫描到一个设备,系统都会发送此广播

                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (scanDevice == null || scanDevice.getName() == null) return;

                String scanDeciceName = scanDevice.getName();
                Log.e(TAG, "-----扫描到蓝牙设备 : " + scanDeciceName + ",address: " + scanDevice.getAddress());
                bluetoothDeviceList.add(scanDevice);
                myAdapter.notifyDataSetChanged();

                if (scanDeciceName != null && scanDeciceName.equals(DESIGNATED_BLUETOOTH_NAME)) {
                    //扫描到指定设备
                    mBluetoothAdapter.cancelDiscovery();


                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //扫描结束
                Log.e(TAG, "------------蓝牙扫描结束");
                printBluetoothDevice();
            }
        }
    };

    /**
     * 打印已配对蓝牙设备
     */
    public void printBluetoothDevice() {
        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        Log.e(TAG, "bonded device size : " + devices.size());

        for (BluetoothDevice device : devices) {
            Log.e(TAG, "bonded device name :" + device.getName() + "bonded device address :" + device.getAddress());
        }

    }

    @OnClick({R.id.btn_scan_again})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_again:
                //正在扫描
                if (mBluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "正在扫描,请稍后...", Toast.LENGTH_SHORT).show();
                    return;
                }
                bluetoothDeviceList.clear();
                myAdapter.notifyDataSetChanged();
                mBluetoothAdapter.startDiscovery();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {

        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();


        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }

    }

    class MyRequestPermission implements PermissionListener {

        @Override
        public void onGranted() {
            Log.e(TAG, "获取到了获取位置信息的权限");
            mBluetoothAdapter.startDiscovery();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onDenied(List<String> deniedPermissions) {
            for (String str : deniedPermissions) {
                Log.e(TAG, "-----deniedPermission : " + str);
            }

            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                //如果用户勾选了不再提醒,shouldShowRequestPermissionRationale(...)会返回false
                //但国内定制的ROM，比如小米是永久返回false的。
                //下次进来的时候在这里做一些处理,一般是引导用户到设置里面去设置
                builder = new AlertDialog.Builder(MainActivity.this);


                builder.setMessage("应用需要获取用户位置信息,是否前往设置?");

                builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //这段跳转到应用权限设置界面的代码在不同的手机上需要进行适配(小米5X是可以的)
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        MainActivity.this.startActivity(intent);
                    }
                });

                builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        UIUtils.showToast("应用需要获取地理位置信息,否则定位功能可能无法正常使用");
                    }
                });
                builder.setCancelable(false);

                builder.show();
            } else {
//                UIUtils.showToast("应用需要获取地理位置信息,否则定位功能可能无法正常使用");
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermission = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            deniedPermission.add(permission);
                        }
                    }

                    if (deniedPermission.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermission);
                    }
                }
                break;

        }
    }
}
