package com.updateapp;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class ReplaceBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG="ApkDelete";
	public static final String TEMP_FILE_NAME="NewAppSample.apk";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		File downLoadApk = new File(Environment.getExternalStorageDirectory(),
				TEMP_FILE_NAME);
		if(downLoadApk.exists()){
			downLoadApk.delete();
		}
		Log.i(TAG, "downLoadApkFile was deleted!");
	}

}
