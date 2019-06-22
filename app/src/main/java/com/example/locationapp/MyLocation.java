package com.example.locationapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by f-lab on 2018/07/29.
 */

public class MyLocation {


	/***** 位置情報コールバック　******/
	class MyLocationCallback extends LocationCallback {
		private MainActivity main_act;

		// 生成する
		public MyLocationCallback(MainActivity ma){
			main_act=ma;
		}

		// 位置情報の結果が得られた
		public void onLocationResult(LocationResult lr){
			super.onLocationResult(lr);
			main_act.displayLocationInfo(lr);
		}
	}

	/***** 設定のチェックが成功したときのリスナー *****/
	public class MyOnSuccessListener implements OnSuccessListener<LocationSettingsResponse> {
		private MainActivity main_act;

		// 生成する
		public MyOnSuccessListener(MainActivity ma){
			main_act=ma;
		}

		// 成功したとき
		public void onSuccess(LocationSettingsResponse loc_set_res){
			if(ActivityCompat.checkSelfPermission(main_act,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(main_act,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
				return;
			}
			flpc.requestLocationUpdates(loc_req,loc_callback, Looper.myLooper());
		}
	}

	/***** 設定のチェックが失敗したときのリスナー *****/
	public class MyOnFailureListener implements OnFailureListener {
		private MainActivity main_act;

		// 生成する
		public MyOnFailureListener(MainActivity ma){
			main_act=ma;
		}

		// 失敗したとき
		public void onFailure(Exception e){
			Toast.makeText(main_act,"設定のチェックに失敗した",Toast.LENGTH_SHORT).show();
		}
	}

	/***** メインクラス *****/
	private FusedLocationProviderClient flpc;
	private SettingsClient set_cli;
	private LocationSettingsRequest loc_set_req;
	private LocationCallback loc_callback;
	private LocationRequest loc_req;


	// 生成する
	public MyLocation(MainActivity ma){
		flpc= LocationServices.getFusedLocationProviderClient(ma);
		set_cli=LocationServices.getSettingsClient(ma);
		loc_callback=new MyLocationCallback(ma);
		loc_req=new LocationRequest();
		loc_req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		loc_req.setInterval(10000);
		loc_req.setFastestInterval(5000);
		LocationSettingsRequest.Builder b=new LocationSettingsRequest.Builder();
		b.addLocationRequest(loc_req);
		loc_set_req=b.build();
	}

	// 位置更新を開始する
	public void startLocationUpdate(MainActivity ma){
		Task<LocationSettingsResponse> t=set_cli.checkLocationSettings(loc_set_req);
		MyOnSuccessListener suc=new MyOnSuccessListener(ma);
		MyOnFailureListener fail=new MyOnFailureListener(ma);
		t.addOnSuccessListener(ma,suc);
		t.addOnFailureListener(ma,fail);

	}


	//位置情報の取得を停止する
	public void stopLocationUpdate(MainActivity ma){
		flpc.removeLocationUpdates(loc_callback);
	}
}
