
package com.open.aidl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

/**
 * 后台服务类
 * @author DexYang
 *
 */
public class TestService extends Service {

	private final String TAG="TestService";
	private Object mCallbacksLock=new Object();
	
	private Handler mHandler=new Handler();
	
	@Override
	public void onCreate() {
		super.onCreate();
Log.v(TAG, "onCreate()");
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
Log.v(TAG, "onStart()");
        handleCommand(intent);
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
Log.v(TAG, "onStartCommand()");
		handleCommand(intent);
		return START_STICKY;
	}

	private void handleCommand(Intent intent)
	{
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
Log.v(TAG, "onBind()");
		if(null!=arg0&&TestService.class.getName().equals(arg0.getAction()))
		{
			handleCommand(arg0);
		}
		return mBinder;
	}

    @Override
	public void onRebind(Intent intent) {
Log.v(TAG, "onRebind()");
		if(TestService.class.getName().equals(intent.getAction()))
		{
			handleCommand(intent);
		}
		super.onRebind(intent);
	}
    
	@Override
	public boolean onUnbind(Intent intent) {
Log.v(TAG, "onUnbind()");
		return true;
	}

	@Override
	public void onDestroy() {
Log.v(TAG, "onDestroy()");
		mCallbacks.kill();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}
	
	/**
	 * Binder 相关
	 */
	private final CusRemoteCallbackList<ITestServiceCallback> mCallbacks= new CusRemoteCallbackList<ITestServiceCallback>();
	private ITestService.Stub mBinder=new ITestService.Stub() {

		@Override
		public int request(Bundle mBundle) throws RemoteException {
Log.v(TAG,"call from Activity ");
			mHandler.postDelayed(new Runnable(){
			
				@Override
				public void run() {
					synchronized (mCallbacksLock) 
					{
						int callbacksNum = mCallbacks.beginBroadcast();
				        for (int i=callbacksNum-1; i>=0; i--) 
				        {
				            try {
			   						mCallbacks.getBroadcastItem(i).onResponse(null);;
				            } catch (Exception e) {
				            	e.printStackTrace();
				            }
				        }
				        mCallbacks.finishBroadcast();
					}
				}
				
			}, 3000);
			return 0;
		}

		@Override
		public void registerCallback(ITestServiceCallback cb)
				throws RemoteException {
Log.v(TAG,"registerCallback :");
			if (cb != null)
			{
				mCallbacks.register(cb);
			}

		}

		@Override
		public void unregisterCallback(ITestServiceCallback cb)
				throws RemoteException {
Log.v(TAG,"unregisterCallback :");
				if (cb != null) 
				{
					mCallbacks.unregister(cb);
				}
		}

	};
	
	/**
	 * 经过测试onCallbackDied()方法，只有在bindService()，没有调用unbind()方法process就挂了的情况下才会执行
	 * @author Administrator
	 * @param <E>
	 */
	private class CusRemoteCallbackList<E extends IInterface> extends RemoteCallbackList<E>
	{
		@Override
		public void onCallbackDied(E callback) {
Log.v(TAG, "CusRemoteCallbackList onCallbackDied 1");
			super.onCallbackDied(callback);

		}

		@Override
		public void onCallbackDied(E callback, Object cookie) {
Log.v(TAG, "CusRemoteCallbackList onCallbackDied 2");
			
			super.onCallbackDied(callback, cookie);
		}
	}
 
}
