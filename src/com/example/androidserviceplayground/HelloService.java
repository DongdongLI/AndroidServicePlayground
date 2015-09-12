package com.example.androidserviceplayground;

import java.util.ArrayList;

import android.app.Service;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HelloService extends Service {
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_SET_VALUE = 3;

	
   Thread doWork;
   Handler hanlHandler=new Handler();
   private IBinder mBinder = new MyBinder();
   final Messenger mMessenger = new Messenger(new IncomingHandler());// this is not used
   ArrayList<Messenger> mClients = new ArrayList<Messenger>();
   //final Messenger mMessenger=new Messenger(mBinder);
   /** indicates how to behave if the service is killed */
   int mStartMode;
   
   /** interface for clients that bind */
   
   /** indicates whether onRebind should be used */
   boolean mAllowRebind;

   /** Called when the service is being created. */
   @Override
   public void onCreate() {
	   Log.d("demo1","service onCreate");
	   
	   	
   }

  
   public void doHeavyWork(){ // within this function, there is another thread to do the work
	   new Thread(new Runnable() {
			public void run() {
				for(int i=1;i<=5;i++){
					try {
						synchronized (this) {
							wait(500);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//Log.d("demo1",i+"");
					if(mMessenger==null){
						Log.d("demo1","it is null");
					}
					
					Bundle bundle=new Bundle();
					bundle.putInt("value",i);
					//Message message=Message.obtain(null, MSG_SET_VALUE, i, 0);
					Message message=Message.obtain(null, MSG_SET_VALUE, 0,0);
					message.setData(bundle);
					
					// send broadcast back so it can be received in activity
					Intent intent = new Intent();
					intent.setAction("IncomingMessage");
					intent.putExtra("I", i);
					sendBroadcast(intent);
					
					try {
						mMessenger.send(message);
						
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
   }
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
	   Log.d("demo1","service onStart");
	   Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
	      
	   
	   return START_STICKY;
   }

   /** A client is binding to the service with bindService() */
   @Override
   public IBinder onBind(Intent intent) { // after binding, send back the binder
	   //return mMessenger.getBinder();
      return mBinder;
   }

   /** Called when all clients have unbound with unbindService() */
   @Override
   public boolean onUnbind(Intent intent) {
      return mAllowRebind;
   }

   /** Called when a client is binding to the service with bindService()*/
   @Override
   public void onRebind(Intent intent) {

   }

   /** Called when The service is no longer used and is being destroyed */
   @Override
   public void onDestroy() {
	   Log.d("demo1", "service onDestroy");
   }
   
   public class MyBinder extends Binder { // help to get the service object in activity
		HelloService getService() {
			return HelloService.this;
		}
	}
   
	class IncomingHandler extends Handler { // handles incoming messages
		@Override
		public void handleMessage(Message msg) {
			// obtain Activity address from Message
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_VALUE:
				
				int mValue = msg.arg1;
				//Log.d("demo1","size of mClients: "+mClients.size());
				for (int i = mClients.size() - 1; i >= 0; i--) {
					try {
						mClients.get(i).send(Message.obtain(null, MSG_SET_VALUE, mValue, 0));
						//Log.d("demo1","sent");
					} catch (RemoteException e) {
						// The client is dead. Remove it from the list;
						// we are going through the list from back to front
						// so this is safe to do inside the loop.
						Log.d("demo1","get removed from list");
						//mClients.remove(i);
					}
				}
				break;
				
			}
		}
	}
}