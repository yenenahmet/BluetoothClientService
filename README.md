# BluetoothClientService
Android lib for working with Bluetooth Client and Bluetooth Scan in Android Sdk


 compile 'com.github.yenenahmet:BluetoothClientService:0.1.0'



 private BSS bluetoohSock;
 
 

 @Override
    protected void onStart() {
        super.onStart();
        BluetoohSock(device);

    }
private void BluetoohSock(BluetoothDevice device){
       bluetoohSock = new BSS(device,true,true,this,false);
       bluetoohSock.setBSSListener(new BSS.BSSListener() {
           @Override
           public void onStateChange(String Status, int StatusCode) {

           }

           @Override
           public void onError(String Error) {

           }

           @Override
           public void onMessage(String Message) {

           }
       });
   }
   
    private void SendCommandBluetooth(String Send){
        bluetoohSock.SendMessage(Send);
    }
