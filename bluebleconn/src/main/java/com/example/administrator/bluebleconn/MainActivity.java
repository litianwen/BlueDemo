package com.example.administrator.bluebleconn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_DEVICE = 111;
    BluetoothDevice device;
    BluetoothGatt gatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(new Intent(this, DevicesActivity.class), REQUEST_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DEVICE && resultCode == RESULT_OK) {
            device = data.getParcelableExtra("device");
            if (device != null)
                conn();
        } else {
            finish();
        }
    }

    private void conn() {
        //BLE连接过程
        gatt = device.connectGatt(this, false, mGattCallback);
        gatt.connect();
    }

    public void test(View v) {
        //发送数据
        //  byte[] buf = getBuf((byte) 0x30);
        byte[] buf = {0x55, (byte) 0xaa, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        writer.setValue(buf);
        gatt.writeCharacteristic(writer);
    }

    public void rgb(View v) {
        int r = (int) (Math.random() * 255);
        int g = (int) (Math.random() * 255);
        int b = (int) (Math.random() * 255);
        byte[] buf = getBuf(new byte[]{0x07, (byte) r, (byte) g, (byte) b, 0x00, (byte) 0x80});
        writer.setValue(buf);
        gatt.writeCharacteristic(writer);
    }


    public byte[] getBuf(byte... buf) {
        byte[] bufs = new byte[17];
        //两位协议头
        bufs[0] = 0x55;
        bufs[1] = (byte) 0xaa;
        //协议命令共14个,不足补0
        if (buf != null && buf.length > 0) {
            for (int i = 0; i < buf.length; i++) {
                bufs[2 + i] = buf[i];
            }
        }
        return bufs;
    }

    BluetoothGattCharacteristic writer;


    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("TAG", "-------------------->>第一步");
            Log.e("TAG", "----------->onConnectionStateChange");
            //获取服务   获取设备电量 获取设备名称
            gatt.discoverServices();
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
//            List<BluetoothGattService> services = gatt.getServices();
//            for (BluetoothGattService service : services) {
//                Log.e("TAG", "---------------" + service.getUuid());
//                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : service.getCharacteristics()) {
//                    Log.e("TAG", "------------->>>" + bluetoothGattCharacteristic.getUuid());
//                }
//            }
            Log.e("TAG", "----------->>onServicesDiscovered");
            writer = gatt.getServices().get(2).getCharacteristics().get(0);
            BluetoothGattCharacteristic reader = gatt.getServices().get(2).getCharacteristics().get(1);
            //打开读取开关
            Log.e("TAG", "----------->>获取到特征值");
            for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
            gatt.setCharacteristicNotification(reader, true);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e("TAG", "----------->onCharacteristicRead");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e("TAG", "----------->onCharacteristicRead");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e("TAG", "----------->收到硬件信息");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e("TAG", "----------->>onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e("TAG", "----------->>onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.e("TAG", "----------->>onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.e("TAG", "----------->>onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.e("TAG", "----------->>onMtuChanged");
        }
    };

}
