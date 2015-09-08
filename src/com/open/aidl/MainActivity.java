package com.open.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.open.aidl.service.ITestService;
import com.open.aidl.service.ITestServiceCallback;
import com.open.aidl.service.TestService;

public class MainActivity extends Activity {

	private final String TAG="MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	private void init()
	{
		findViewById(R.id.bindBtn).setOnClickListener(clickListener);
		findViewById(R.id.unBindBtn).setOnClickListener(clickListener);
	}
	
	View.OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId())
			{
				case R.id.bindBtn:
					bind();
					break;
					
				case R.id.unBindBtn:
					unbind();
			}
			
		}
	};
	
	private void bind()
	{
		Intent mIntent=new Intent(TestService.class.getName());
		bindService(mIntent, mServerConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void unbind()
	{
		if(null!=mService)
		{
			try {
				mService.unregisterCallback(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			unbindService(mServerConnection);
		}
	}
	
	@Override
	protected void onDestroy() {
		unbind();
		super.onDestroy();
	}

	private ITestService mService=null;
	private ServiceConnection mServerConnection=new ServiceConnection() 
	{
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
Log.v(TAG, "onServiceDisconnected");
			mService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
Log.v(TAG, "onServiceConnected");
			mService=ITestService.Stub.asInterface(service);
			try {
				mService.registerCallback(mCallBack);
				mService.request(null);
			} catch (RemoteException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private ITestServiceCallback mCallBack=new ITestServiceCallback.Stub() {
		
		@Override
		public void onResponse(Bundle mBundle) throws RemoteException {
Log.v(TAG,"call from service");
			}
		};
}
