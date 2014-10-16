package com.updateapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UpdateAppActivity extends Activity {

	/** Called when the activity is first created. */
	private static final String TAG = "Update";
	private Button btnUpdateApp;
	private ProgressDialog pBar;
	private int newVerCode = 0;
	private String newVerName = "";
	private GetUpdateInfo update;

	static class MyHandler extends Handler {
		WeakReference<UpdateAppActivity> mActivity;

		MyHandler(UpdateAppActivity activity) {
			mActivity = new WeakReference<UpdateAppActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			UpdateAppActivity _this = mActivity.get();
			switch (msg.what) {

			case 1:
					_this.showUpdateDialog();
				break;
			case 2:
				_this.haveDownLoad();
				break;
			default:
				break;
			}
		}
	};

	private Handler handler = new UpdateAppActivity.MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		update = new GetUpdateInfo();
		showProgressBar();
		try {
			if (isNetworkAvailable(this) == false) {
				System.out.println("fufei---网络不可用");
				return;
			} else {
				System.out.println("fufei---网络可用");
				new Thread(new CheckVersionTask()).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		btnUpdateApp = (Button) findViewById(R.id.btnUpdateApp);
		btnUpdateApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new Thread(new CheckVersionTask()).start();
			}
		});
	}

	private class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			int currentCode = CurrentVersion.getVerCode(UpdateAppActivity.this);
			newVerCode = update.getUpdataVerJSON().getAppVersion();
			if (newVerCode > currentCode) {// Current Version is old
											// 弹出更新提示对话框
				handler.obtainMessage(1).sendToTarget();
			}
		}
	}

	// check the Network is available
	private static boolean isNetworkAvailable(Context context) {
		try {

			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
			return (netWorkInfo != null && netWorkInfo.isAvailable());// 检测网络是否可用
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// show Update Dialog
	private void showUpdateDialog(){
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本：");
		sb.append(CurrentVersion.getVerName(this));
		sb.append("VerCode:");
		sb.append(CurrentVersion.getVerCode(this));
		sb.append("\n");
		sb.append("发现新版本：");
		sb.append(newVerName);
		sb.append("NewVerCode:");
		sb.append(newVerCode);
		sb.append("\n");
		sb.append("是否更新？");
		Dialog dialog = new AlertDialog.Builder(UpdateAppActivity.this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downAppFile();
					}
				})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();
		dialog.show();
	}

	protected void showProgressBar() {
		pBar = new ProgressDialog(UpdateAppActivity.this);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍后...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	// Get ServerVersion from GetUpdateInfo.getUpdateVerJSON

	protected void downAppFile() {
		pBar.show();
		new Thread() {
			public void run() {
				update.downloadFile();
				handler.obtainMessage(2).sendToTarget();

			}
		}.start();
	}

	// cancel progressBar and start new App
	protected void haveDownLoad() {
		pBar.cancel();
		// 弹出警告框 提示是否安装新的版本
		Dialog installDialog = new AlertDialog.Builder(UpdateAppActivity.this)
				.setTitle("下载完成").setMessage("是否安装新的应用")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						installNewApk();
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		installDialog.show();
	}

	// 安装新的应用
	protected void installNewApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(),
				ReplaceBroadcastReceiver.TEMP_FILE_NAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

}
