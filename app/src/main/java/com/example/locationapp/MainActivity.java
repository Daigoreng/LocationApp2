package com.example.locationapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
//AndroidXにより変更になった
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
//新しいバージョン
import androidx.appcompat.app.AppCompatActivity;
//以前のバージョン
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;




public class MainActivity extends AppCompatActivity {
	private final int REQUEST_PERMISSION = 10;
	private MyLocation my_loc;
	private ImageView imageView;
	private Animation animation,animation2;
	private String fileName = "car_data.txt";
	private int dele = 1;
	private String str = null;
	public int restart;


	AsyncNetworkTask task;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(checkLocationPermission()) {        // 位置情報が利用可能かチェックする
			my_loc = new MyLocation(this);
		}
		imageView = findViewById(R.id.imageView);
		imageView.setVisibility(View.INVISIBLE);
		animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
		animation2 = AnimationUtils.loadAnimation(this, R.anim.alpha_fadeout);

		str = readFile(fileName);

	}



	// 位置情報が利用可能かどうかのチェック
	private boolean checkLocationPermission(){
		if(Build.VERSION.SDK_INT < 23) return true;
		if (ActivityCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
			return true;
		ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						REQUEST_PERMISSION);
		return false;
	}

	// 権限の許可要求の結果
	public void onRequestPermissionsResult(int requestCode,
																				 @NonNull String[] permissions,
																				 @NonNull int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION) {
			// 使用が許可された
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				my_loc = new MyLocation(this);
			else
				Toast.makeText(this,"位置情報は使えません", Toast.LENGTH_SHORT).show();
		}
	}

	// startボタンが押された
	public void measureLocation(View v){   //mesureLocationはactivty_main.xmlのbuttonで登録されている
		TextView tv=findViewById(R.id.report_area);
		if(my_loc==null){
			tv.setText("位置情報は使えません");
			return;
		}
		my_loc.startLocationUpdate(this);
		imageView.startAnimation(animation);
		str = readFile(fileName);
	}


	//stopボタンが押された
	public void stopLocation(View v){
		TextView tv=findViewById(R.id.report_area);
		tv.setText("位置情報の取得を停止しています");
		super.onPause();

		my_loc.stopLocationUpdate(this);

		FirstActivity.fFlag = true;
		finish();
		moveTaskToBack(true);
		finish();
	}

	//表示しないボタンが押された
	public void delete(View v){
		imageView.startAnimation(animation2);
		dele = 0;
	}

	//到着ボタンが押された
	public void arrive(View v){
//		str = readFile(fileName);
		task = new AsyncNetworkTask(this);
//		task.execute("https://sakura.mbc.co.jp/pony_trace/pony/arrive.php?terminal="+ str);
		task.execute("https://sakura.mbc.co.jp/pony_trace/pony/arrive.php?terminal=pony");
		Toast.makeText(this,"運転お疲れ様でした", Toast.LENGTH_LONG).show();

	}

	//再表示ボタンが押された
	public void restart(View v){
		restart = 1;
		Intent intent = new Intent(getApplication(), FirstActivity.class);
		intent.putExtra("res", restart);
		startActivity(intent);
		finish();
	}

	// 位置情報の表示
	public void displayLocationInfo(LocationResult lr,boolean first_time) {
		Location loc = lr.getLastLocation();
		String t = DateFormat.getTimeInstance().format(new Date());
		TextView tv = findViewById(R.id.report_area);
		tv.setText("測定結果\n");
		tv.append("-----------------\n");
		tv.append("緯度：" + loc.getLatitude() + "\n");
		tv.append("経度：" + loc.getLongitude() + "\n");
		tv.append("時刻：" + t + "\n");
//		tv.append("terminal：" + str +"\n");
		tv.append("terminal：pony \n");
		// (三項演算子)first_timeがtrueの時はdeleが2(startの最初の1回だけ),falseの時は1(通常時)
		dele=first_time?2:1;

		task = new AsyncNetworkTask(this);
    //task.execute("https://sakura.mbc.co.jp/pony_trace/store_loc/store_loc.php?lat=" + loc.getLatitude() + "&lng=" + loc.getLongitude() + "&terminal="+ str + "&stat=" + dele);
		task.execute("https://sakura.mbc.co.jp/pony_trace/store_loc/store_loc.php?lat=" + loc.getLatitude() + "&lng=" + loc.getLongitude() + "&terminal=pony" + "&stat=" + dele);
	}

	public String readFile(String file) {


		try (FileInputStream fileInputStream = openFileInput(file);
				 BufferedReader reader= new BufferedReader(
								 new InputStreamReader(fileInputStream, "UTF-8"))) {
			String lineBuffer;
			while( (lineBuffer = reader.readLine()) != null ) {
				str = lineBuffer ;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

}
