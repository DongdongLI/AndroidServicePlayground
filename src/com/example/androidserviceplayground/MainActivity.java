package com.example.androidserviceplayground;

import com.example.androidserviceplayground.HelloService.MyBinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//Messenger mService=null;
	boolean mIsBound; // see if the service is bound
	TextView mCallbackText; // show the info get from broadcast receiver
	Button startBtn; // start service
	Button stopBtn; // stop service
	Button startWorkBtn; // triger service work
	
	boolean serviceBound=false;
	HelloService helloService;
	
	//final Messenger mMessenger = new Messenger(new IncomingHandler()); // mMessenger=new Messenger(service);
	//Messenger mMessenger=null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        startBtn=(Button) findViewById(R.id.startBtn);
        stopBtn=(Button) findViewById(R.id.stopBtn);
        startWorkBtn=(Button) findViewById(R.id.starWorBtn);
        mCallbackText=(TextView)findViewById(R.id.callbackTex);
        
        IntentFilter filter = new IntentFilter("IncomingMessage");
    	registerReceiver(messageReceiver, filter);
        // register the broadcast receiver
    	
        startBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//startService(new Intent(getBaseContext(),HelloService.class));
				//Intent intent=new Intent(getBaseContext(),HelloService.class);
				//startService(intent);
				bindService(new Intent(getBaseContext(),HelloService.class), serviceConnection, Context.BIND_AUTO_CREATE);
			}
		});
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//stopService(new Intent(getBaseContext(),HelloService.class));
				if(serviceBound){
					unbindService(serviceConnection);
					serviceBound=false;
					helloService=null;
					Intent intent=new Intent(getBaseContext(),HelloService.class);
					stopService(intent);
				}
			}
		});
        
        startWorkBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(helloService!=null)
					helloService.doHeavyWork();
				else
					Toast.makeText(getApplicationContext(), "Connection is lost.", Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    private ServiceConnection serviceConnection=new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			serviceBound=false;
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			//mMessenger=new Messenger(new IncomingHandler());
			//mMessenger=new Messenger(service);
			
			helloService=((MyBinder)service).getService(); // get the reference of the bounded service
			
			serviceBound=true;
//			try {
//	            Message msg = Message.obtain(null,
//	                    HelloService.MSG_REGISTER_CLIENT);
//	            msg.replyTo = mMessenger;
//	            mMessenger.send(msg);
//	            Log.d("demo1","signup message sent");
//	            // Give it some value as an example.
//	            msg = Message.obtain(null,
//	                    HelloService.MSG_SET_VALUE, this.hashCode(), 0);
//	            mMessenger.send(msg);
//	            
//	        } catch (RemoteException e) {
//	            e.printStackTrace();
//	        }

		}
	};
	
	public void invoke(){
		//usually send some data to the service
	}
	
//	class IncomingHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case HelloService.MSG_SET_VALUE:
//                	//int val=msg.getData().getInt("value");
//                	Log.d("demo1","val: "+msg.getData().getInt("value"));
//                    mCallbackText.setText("Received from service: " + msg.arg1);
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
	
	private BroadcastReceiver messageReceiver=new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if(action.equals("IncomingMessage")){
	        	int val=intent.getExtras().getInt("I");
	        	Toast.makeText(getApplicationContext(), "BOOM"+val, Toast.LENGTH_SHORT).show();
	        	mCallbackText.setText(mCallbackText.getText()+""+val);
	        }
	    }
	};
	
	
}
