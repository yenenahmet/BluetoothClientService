package com.example.bluetoothclientservice;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;



/**
 * Created by Ahmet on 22.09.2017.
 */
public class BSS {
    private Context context;
    private ProgressDialog progressDialog;
    private BSSListener bssListener;
    private String SendMessage ="" , TAG="BSS";
    private BluetoothClientService bluetoothClientService;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothState.MESSAGE_WRITE:
                    SendMessage = msg.obj.toString();
                    Log.e(TAG +"Write ",msg.obj.toString());
                    break;
                case BluetoothState.MESSAGE_READ:
                    String readMessage = ( String) msg.obj;
                    readMessage = ClearData(readMessage);
                    SendMessage = ClearData(SendMessage);
                    if(readMessage != null && !SendMessage.equals(readMessage) && readMessage != "") {
                        bssListener.onMessage(readMessage);
                    }
                    break;
                case BluetoothState.MESSAGE_DEVICE_NAME:
                    break;
                case BluetoothState.MESSAGE_TOAST:
                     if(progressDialog.isShowing() && progressDialog != null){
                        progressDialog.cancel();
                     }
                    bssListener.onError(msg.obj.toString());
                    Log.e(TAG+" TOAST ",msg.obj.toString());
                    break;
                case BluetoothState.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1){
                        case 0:
                            bssListener.onStateChange("Stop",0);
                            Log.e(TAG+" Change : ","Stop");
                            break;
                        case 1:
                            bssListener.onStateChange("Start",1);
                            Log.e(TAG +" Change","Start");
                            break;
                        case 2:
                            bssListener.onStateChange("Connecting",2);
                            Log.e(TAG +" Change","Connecting...");
                            break;
                        case 3:
                            bssListener.onStateChange("Connected",3);
                            if(progressDialog !=null) progressDialog.cancel();
                            Log.e(TAG +" Change","Connected.");
                            break;
                    }
                    break;
            }
        }
    };
    public BSS(@NonNull BluetoothDevice device,@NonNull Context context,@NonNull boolean AutoStart,@NonNull boolean AutoProgress,@NonNull boolean SecureConnect,@NonNull boolean isDevice){
        bluetoothClientService = new BluetoothClientService(device,mHandler,SecureConnect,isDevice);
        this.context = context;
        if(AutoStart){
            Connect();
        }
        if(AutoProgress){
            progressDialog = BluetoothState.ProgressRun(context,"Connecting to Device ...");
        }
    }
    
    public BSS(@NonNull BluetoothDevice device,@NonNull boolean SecureConnect,@NonNull boolean isDevice){
        bluetoothClientService = new BluetoothClientService(device,mHandler,SecureConnect,isDevice);
    }
    
    public void Connect(){
            if(bluetoothClientService.getState() ==0){
                bluetoothClientService.Connect();
            }else{
                bssListener.onError("Connected on Device");
            }
    }
    public void SendMessage(String Message){
        if(Message == null){
            return;
        }
        bluetoothClientService.SendMessage(Message);
    }
    public void StopService(){
     bluetoothClientService.stop();
 }
    public interface BSSListener{
        void onStateChange(String Status, int StatusCode);
        void onError(String Error);
        void onMessage(String Message);
    }
    public void setBSSListener(BSSListener bssListener){
        this.bssListener = bssListener;
    }
    public void Restart(){
        StopService();
        try{
            Thread.sleep(99);
            Connect();
        }catch (InterruptedException ex){
            Log.e(TAG +"Restart",ex.toString());
        }
    }
    private String ClearData(String Data){
        String Clear = Data.replace("\r","");
        return Clear.replace("\n","");
    }
}
