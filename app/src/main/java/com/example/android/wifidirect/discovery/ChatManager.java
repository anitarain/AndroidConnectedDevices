
package com.example.android.wifidirect.discovery;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {

	private Socket socket = null;
	private Handler handler;

	public ChatManager(Socket socket, Handler handler) {
		this.socket = socket;
		this.handler = handler;       
	}

	private InputStream iStream;
	private OutputStream oStream;
	private ObjectOutputStream oostream;
	private ObjectInputStream oistream;

	private static final String TAG = "ChatHandler";

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			iStream = socket.getInputStream();
			oStream = socket.getOutputStream();
			oostream = new ObjectOutputStream(oStream);
			oistream = new ObjectInputStream(iStream);
			//byte[] buffer = new byte[1024];
			//ArrayList<String> objectStream = new ArrayList<String>();
			int bytes;
			handler.obtainMessage(WiFiServiceDiscoveryActivity.MY_HANDLE, this)
			.sendToTarget();

			while (true) {
				try {
					// Read from the InputStream //girandeh = istream
//					                    bytes = iStream.read(buffer);
					//objectStream = (ArrayList<String>) oistream.readObject();
//
//					                    if (bytes == -1) {
//					                       break;
//					                    }

					// Send the obtained bytes to the UI Activity
					//                    Log.d(TAG, "Rec:" + String.valueOf(buffer));
					//Log.d(TAG, "Rec:" + String.valueOf(objectStream));
//					                    handler.obtainMessage(WiFiServiceDiscoveryActivity.MESSAGE_READ,
//					                            bytes, -1, buffer).sendToTarget();
					handler.obtainMessage(WiFiServiceDiscoveryActivity.OBJECT_READ, oistream.readObject()).sendToTarget();
					//
					//                    handler.obtainMessage(WiFiServiceDiscoveryActivity.OBJECT_READ,
					//                    		objectStream).sendToTarget();
				}
				catch (ClassNotFoundException e) {
					Log.e(TAG, "disconnected", e);
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buffer) {
		try {
			oStream.write(buffer);
		} catch (IOException e) {
			Log.e(TAG, "Exception during write", e);
		}
	}

	public void writeObject(Object object){
		try {
			oostream.writeObject(object);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Exception during write object", e);
		}
	}

	public String getRemoteAddress(){
		Log.d("Test2", socket.getInetAddress().toString() + socket.getLocalSocketAddress().toString() + socket.getRemoteSocketAddress().toString());
		return socket.getInetAddress().toString();		    	
	}
	public String getLocalAddress(){
		return socket.getLocalAddress().toString();
	}

}
