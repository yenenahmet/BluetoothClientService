# BluetoothClientService
Android lib for working with Bluetooth Client and Bluetooth Scan in Android Sdk


 compile 'com.github.yenenahmet:BluetoothClientService:0.1.3'

----------------------------------------------------------------------

      private BSS bluetoohSock;
 
 

     @Override
      protected void onStart() {
         super.onStart();
         BluetoohSock(device);

      }
     private void BluetoohSock(BluetoothDevice device){
       bluetoohSock = new BSS(device,this,true,true,false,false);
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
--------------------------------------------------------------------
     Bluetooth Scan
--------------------------------------------------------------------
                private void BluetoothScan(){
                  if(bluetoothScan.BluetoothAdapterKontrol()){
                      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                  }else{
                      bluetoothScan.makeBTDiscoverable();
                  }
                  bluetoothScan.getProgressDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                      @Override
                      public void onCancel(DialogInterface dialog) {
                          if(bluetoothScan.isDevice()){
                              device = bluetoothScan.getBluetoothDevice();
                          }
                      }
                  });
              }
              -------------------------------------
                        @Override
              protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                  super.onActivityResult(requestCode, resultCode, data);
                  switch (requestCode){
                      case  1 :
                          if(resultCode == Activity.RESULT_OK){
                              bluetoothScan.makeBTDiscoverable();
                          }else {
                              Toast.makeText(getApplicationContext(), "Bluetooth Not Started", Toast.LENGTH_SHORT).show();
                          }
                          break;
                      case 2:
                          bluetoothScan.searchForDevices();
                          break;
                  }

              }
