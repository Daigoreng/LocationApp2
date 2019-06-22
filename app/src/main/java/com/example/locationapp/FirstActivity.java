package com.example.locationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//以前のバージョン
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FirstActivity extends AppCompatActivity {

	public static String text;
	public static boolean fFlag = false;
	private EditText editText;
	private String fileName = "car_data.txt";
	private int restart = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);

		editText = findViewById(R.id.edit_text);

		Intent main = getIntent();
		restart = main.getIntExtra("res",0);


		if(restart == 0){
			if (readFile(fileName) != null) {
				Intent intent = new Intent(getApplication(), MainActivity.class);
				startActivity(intent);

			}
		}
	}

	public void send (View v) {
		String text = editText.getText().toString();
		saveFile(fileName,text); //入力した文字をファイルに保存

		Intent intent = new Intent(getApplication(), MainActivity.class);
		startActivity(intent);

	}

	//ファイルを保存
	public void saveFile(String file, String str){

		try (FileOutputStream fileOutputstream = openFileOutput(file,Context.MODE_PRIVATE);){
			fileOutputstream.write(str.getBytes());

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	//ファイルを読み込む
	public String readFile(String file) {
		try (FileInputStream fileInputStream = openFileInput(file);
				 BufferedReader reader= new BufferedReader(
								 new InputStreamReader(fileInputStream, "UTF-8"))) {
			String lineBuffer;
			while( (lineBuffer = reader.readLine()) != null ) {
				text = lineBuffer ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        if ( fFlag) {
//            finish();
//        }
//    }


}
