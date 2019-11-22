package com.example.reciplease;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CommServ {

    private final Handler handler;
    private BluetoothAdapter btAdapter;
    private ConnectThread connectThread;
    private AcceptThread acceptThread;
    private ReadWriteThread connectedThread;

    private final UUID APP_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private final String APP_NAME = "myapplication";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private int state;
    public static int currentState;

    /**Constructor for the Communication Service**/
    public CommServ(Context context, Handler handler){

        this.handler = handler;

        state = STATE_NONE;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CommServ.this.start();
    }

    public static synchronized int getState(){
        return currentState;
    }

    // Sets the current state of the connection
    public synchronized void setState(int state){
        this.state = state;
        currentState = state;
        handler.obtainMessage(ShareRecipeFragment.MESSAGE_STATE_CHANGED, currentState, -1).sendToTarget();
    }

    // Starts the connection service...
    public synchronized void start(){
        CancelThreads();
        setState(STATE_LISTEN);
        //now start the accept thread...
        if (acceptThread == null){
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // Stops the connection service...
    public synchronized void stop(){
        CancelThreads();
        setState(STATE_NONE);
    }

    // Initiates connection to the remote device
    public void connect(BluetoothDevice device){
        if (state == STATE_CONNECTING){
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        // Cancel the running thread...
        if (connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }

        // Now start the thread to connect with the selected device...
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    //Write to the thread...
    public void write(byte[] buffer){
        ReadWriteThread r;
        synchronized (this){
            if(state != STATE_CONNECTED){
                return;
            }
           r = connectedThread;
        }
        r.write(buffer);
    }

    // Manages the bluetooth connection
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        CancelThreads();

        // Start the thread to manage the connection and perform transmissions...
        connectedThread = new ReadWriteThread(socket);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity...
        Message message = handler.obtainMessage(ShareRecipeFragment.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(ShareRecipeFragment.DEVICE_NAME, device.getName());
        message.setData(bundle);
        handler.sendMessage(message);

        setState(STATE_CONNECTED);
    }

    // Runs while listening for incoming connections...
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket serverSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, APP_UUID);
            } catch (IOException e){
                Log.e("Accept->Constructor", e.toString());
            }
            serverSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket;
            while(state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If the connection was accepted...
                if (socket != null) {
                    synchronized (CommServ.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e("Accept->CloseSocket", e.toString());
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel(){
            try {
                serverSocket.close();
            }catch (IOException e){
                Log.e("Accept->CloseServer", e.toString());
            }
        }
    }

    // runs while attempting to make an outgoing connection
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException e) {
                Log.e("Connect->Constructor", e.toString());
            }
            socket = tmp;
        }

        public void run() {
            //Always make sure to cancel discovery because it will slow down the connection...
            btAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                Log.e("Connect->Run", e.toString());
                try {
                    socket.close();
                } catch (IOException ee) {
                    Log.e("Connect->CloseSocket ", ee.toString());
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done here...
            synchronized (CommServ.this) {
                connectThread = null;
            }

            // Start the connected thread...
            connected(socket, device);
        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException e){
                Log.e("Connect->Cancel", e.toString());
            }
        }
    }

    // runs during a connection with the remote device...
    private class ReadWriteThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;

        public ReadWriteThread(BluetoothSocket socket){
            this.socket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){
            }
            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            // Keeps listening to the InputStream
            while(true) {
                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);

                    // Send the obtained bytes to the UI Activity...
                    handler.obtainMessage(ShareRecipeFragment.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    // Restart the service
                    CommServ.this.start();
                    break;
                }
            }
        }

        // Write to OutputStream
        public void write(byte[] buffer){
            try {
                outStream.write(buffer);
                handler.obtainMessage(ShareRecipeFragment.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e){
            }
        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException e){
            }
        }
    }

    //Handles if connection is lost
    private void connectionLost(){
        Message message = handler.obtainMessage(ShareRecipeFragment.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ShareRecipeFragment.TOAST, "Connection Lost");
        message.setData(bundle);
        handler.sendMessage(message);

        // Restarts the service
        CommServ.this.start();
    }

    //Handles if connection fails
    private synchronized void connectionFailed(){
        Message message = handler.obtainMessage(ShareRecipeFragment.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ShareRecipeFragment.TOAST, "Cant connect to the device");
        message.setData(bundle);
        handler.sendMessage(message);
        // Restarts the service
        CommServ.this.start();
    }

    // Cancels the running threads
    private void CancelThreads(){
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null){
            acceptThread.cancel();
            acceptThread = null;
        }
    }
}
