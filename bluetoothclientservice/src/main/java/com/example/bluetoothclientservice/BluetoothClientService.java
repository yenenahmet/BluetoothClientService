package com.example.bluetoothclientservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ahmet on 18.09.2017.
 */

public class BluetoothClientService {
    private BluetoothDevice device;
    private Connect connect;
    private Connected connected;
    private int State =0;
    private Handler mHandler;
    private boolean  SecureConnect;
    public  BluetoothClientService(BluetoothDevice bluetoothDevice,Handler mHandler,boolean SecureConnect){
        this.device = bluetoothDevice;
        this.mHandler= mHandler;
        this.SecureConnect = SecureConnect;
    }
    private synchronized void setState(int state) {
        this.State = state;
        this.mHandler.obtainMessage(1, state, -1).sendToTarget();
    }
    public synchronized void Connect() {
        if (this.connect != null) {
            this.connect.SocketClose();
            this.connect = null;
        }
        if (this.connected != null) {
            this.connected.SocketClose();
            this.connected = null;
        }
        setState(1);
        this.connect = new Connect();
        this.connect.run();
}
    public synchronized void stop() {
        if (this.connect != null) {
            this.connect.SocketClose();
            this.connect = null;
        }
        if (this.connected != null) {
            this.connected.SocketClose();
            this.connected = null;
        }
        setState(0);
    }
    public void SendMessage(String command)  {
        Connected r;
        synchronized (this){
            if(State ==0) return;
            r = connected;
        }
        r.Write(command);
    }
    private class Connect extends  Thread{
        private BluetoothSocket mmSocket = null;
        public  Connect()  {
            try{
                BluetoothSocket socket = null;
                if(SecureConnect){
                    socket = device.createRfcommSocketToServiceRecord(BluetoothState.uuid);
                }else{
                    socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothState.uuid);
                }
                mmSocket = socket;
                setState(2);
            }catch (IOException ex){
                setState(0);
                mHandler.obtainMessage(BluetoothState.MESSAGE_TOAST,"Not Connect. ERROR ").sendToTarget();
                return;
            }


        }
        @Override
        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                mmSocket.connect();
                connected = new Connected(mmSocket);
                connected.run();
            } catch (IOException e) {
                Log.e("Hata Düştü Client",e.toString());
                SocketClose();
                setState(0);
                mHandler.obtainMessage(BluetoothState.MESSAGE_TOAST,"Device connection was lost").sendToTarget();
                return;
            }
        }
        public void SocketClose(){
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Socket Close Bom", closeException.toString());
            }

        }
    }
    private class Connected extends Thread {
        private BluetoothSocket socket = null;
        private InputStream in;
        private OutputStream out;
        private StringBuilder builder = new StringBuilder();
        private  byte[] buffer = new byte[512];
        public Connected(BluetoothSocket socket) {
            try{
                this.socket = socket;
                in = socket.getInputStream();
                out = socket.getOutputStream();
                setState(3);
            }catch (IOException ex){
                setState(0);
                mHandler.obtainMessage(BluetoothState.MESSAGE_TOAST,"Not Connected.Input Output ERROR ").sendToTarget();
                return;
            }

        }
        @Override
        public void run() {
            while (State == BluetoothState.STATE_CONNECTED){
                try{
                    DataCharEdit();
                   // DataStringEdit();
                }catch (IOException ex){
                    SocketClose();
                    Log.e("Connected BAM",ex.toString());
                    mHandler.obtainMessage(BluetoothState.MESSAGE_TOAST,"Device connection was lost");
                    break;
                }
            }
        }
        public void Write(String command)  {
            try{
                out.write((command+"\r").getBytes());
                mHandler.obtainMessage(BluetoothState.MESSAGE_WRITE,-1,-1,command).sendToTarget();
            }catch (IOException ex){
                mHandler.obtainMessage(BluetoothState.MESSAGE_TOAST,"Error Send Message");
            }
        }
        private void DataCharEdit() throws IOException {// Bluetooth Socket Fast  Read
            char c =(char)in.read();
            if(c=='\n'){
                mHandler.obtainMessage(2,-1,-1,builder.toString()).sendToTarget();
                builder.setLength(0);
            }else{
                builder.append(c);
            }
        }
        private void DataStringEdit() throws IOException {
            if(in.available() >2){
                int bytes =  in.read(buffer);
                mHandler.obtainMessage(2,bytes,-1,new String(buffer, 0, bytes)).sendToTarget();
            }else{
                SystemClock.sleep(100);
            }
        }
        public void SocketClose() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("Bammmmmmm", "close() of connect socket failed", e);
            }
        }
    }
}
