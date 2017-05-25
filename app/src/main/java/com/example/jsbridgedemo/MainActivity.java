package com.example.jsbridgedemo;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity implements OnClickListener {

	private final String TAG = "MainActivity";
	private static int REQUEST_CAMERA_1 = 1;
	BridgeWebView webView;

	Button mGebButton;
	Button mSetButton;
	EditText mUsername;
	EditText mPassword;

	int RESULT_CODE = 0;

	ValueCallback<Uri> mUploadMessage;

	static class User {
		String name;
		String password;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = (BridgeWebView) findViewById(R.id.webView);

		mGebButton = (Button) findViewById(R.id.bt_get);
		mSetButton = (Button) findViewById(R.id.bt_set);
		mUsername = (EditText) findViewById(R.id.edt_username);
		mPassword = (EditText) findViewById(R.id.edt_password);

		mGebButton.setOnClickListener(this);
		mSetButton.setOnClickListener(this);

		webView.setDefaultHandler(new NativeMethodHandler());

		webView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
										String AcceptType, String capture) {
				this.openFileChooser(uploadMsg);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
										String AcceptType) {
				this.openFileChooser(uploadMsg);
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
			}
		});

//		webView.loadUrl("file:///android_asset/demo23.html");
		webView.loadUrl("http://47.93.6.6:9090/quickloan/pages/phonehtml5/loanMoreInformation.html");


		webView.registerHandler("submitFromWeb", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
				Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
				User user = new Gson().fromJson(data, User.class);


				mUsername.setText(user.name);
				mPassword.setText(user.password);
				function.onCallBack("get js data!");
			}

		});


		webView.registerHandler("photoAction", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
				Log.i(TAG, "handler = submitFromWeb, data from web = " + data);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
				startActivityForResult(intent, REQUEST_CAMERA_1);
				//function.onCallBack("get js data!");
			}

		});



		webView.send("hello");

	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		if (requestCode == RESULT_CODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}else if (requestCode == REQUEST_CAMERA_1) { // 判断请求码是否为REQUEST_CAMERA,如果是代表是这个页面传过去的，需要进行获取
//			Bundle bundle = intent.getExtras(); // 从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
//			Bitmap bitmap = (Bitmap) bundle.get("data"); // 将data中的信息流解析为Bitmap类型
//
//			String base64 = imgToBase64(bitmap);
//			Toast.makeText(MainActivity.this, "base64：" + base64,Toast.LENGTH_SHORT).show();

			Uri base64 = intent.getData();
			Toast.makeText(MainActivity.this, "base64：" + base64,Toast.LENGTH_SHORT).show();
			webView.callHandler("getImage",base64+"",new CallBackFunction(){
				@Override
				public void onCallBack(String data) {
					Toast.makeText(MainActivity.this, "传送图片：" + data,Toast.LENGTH_SHORT).show();
				}
			});


		}
	}

	@Override
	public void onClick(View v) {
		if (mGebButton.equals(v)) {
			webView.callHandler("getdataInJs", null, new CallBackFunction() {

				@Override
				public void onCallBack(String data) {
					User user = new Gson().fromJson(data, User.class);
					mUsername.setText(user.name);
					mPassword.setText(user.password);

					//带参数跳转activity
					Intent intent = new Intent(MainActivity.this,SecondActivity.class);
					intent.putExtra("data",data);
					startActivity(intent);

				}

			});
		} else if (mSetButton.equals(v)) {
//			User user = new User();
//			user.name = mUsername.getText().toString();
//			user.password = mPassword.getText().toString();
//			webView.callHandler("setdataInJs", new Gson().toJson(user),
//					new CallBackFunction() {
//
//						@Override
//						public void onCallBack(String data) {
//							Log.i(TAG, "reponse data from js " + data);
//							Toast.makeText(MainActivity.this,"data = " + data
//									,Toast.LENGTH_SHORT).show();
//						}
//
//					});


			String uploadss = "{\"loanUseList\":[{\"loanUseName\":\"流动资金周转\",\"loanUserNum\":\"1\"},{\"loanUseName\":\"扩大经营\",\"loanUserNum\":\"2\"},{\"loanUseName\":\"经营场所装修\",\"loanUserNum\":\"3\"},{\"loanUseName\":\"更新经营设备\",\"loanUserNum\":\"4\"},{\"loanUseName\":\"支付租赁场地租金\",\"loanUserNum\":\"5\"},{\"loanUseName\":\"个人消费信贷\",\"loanUserNum\":\"6\"},{\"loanUseName\":\"其他\",\"loanUserNum\":\"7\"}],\"maxLimit\":\"100000\",\"merchantId\":\"111111111133\",\"merchantName\":\"\",\"minLimit\":\"5000\",\"periodList\":[{\"period\":\"3\"},{\"period\":\"6\"}]}";
			webView.callHandler("registerAction",uploadss,new CallBackFunction(){
				@Override
				public void onCallBack(String data) {
					Toast.makeText(MainActivity.this, "接收来自web的回传数据：" + data,Toast.LENGTH_SHORT).show();
				}
			});
		}

	}


	//c此方法 会在web上的按钮点击后触发
	class NativeMethodHandler implements BridgeHandler {

		@Override
		public void handler(String data, CallBackFunction function) {

			Log.d("TTTTT","function" + function);
			Toast.makeText(MainActivity.this,"function = " + function.getClass().getName(),Toast.LENGTH_SHORT).show();

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
			startActivityForResult(intent, REQUEST_CAMERA_1);
		}

	}



	public String imgToBase64(Bitmap bitmap ) {

		if(bitmap == null){
			//bitmap not found!!
			Log.d("TTT","bitmap = " + bitmap);

			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {

			Log.d("TTT","Exception = " + e.getMessage());
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
