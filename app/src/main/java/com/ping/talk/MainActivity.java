package com.ping.talk;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    myDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    String userID = "";
    Dialog SetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDBHelper = new myDBHelper(this);
        ImageButton ImgSet = (ImageButton) findViewById(R.id.ImgSet);
        Button BtnChat = (Button) findViewById(R.id.BtnChat);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM TalkTB",null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            userID = cursor.getString(cursor.getColumnIndex("userID"));
        }

        cursor.close();
        sqlDB.close();

        if(userID.equals("")) {
            Toast.makeText(this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
            Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(LoginIntent);
        }

        ImgSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog 초기화
                SetDialog = new Dialog(MainActivity.this);
                SetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
                SetDialog.setContentView(R.layout.setting);
                SetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                SettingDial();
            }
        });

        BtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userID.equals("")){
                    Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    LoginIntent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(LoginIntent);
                }else{
                    Intent ChatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    ChatIntent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(ChatIntent);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);

    }

    // 톱니바퀴 버튼 클릭시
    public void SettingDial(){
        SetDialog.show();

        // 뒤로가기 버튼
        Button BtnBack = (Button) SetDialog.findViewById(R.id.BtnBack);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDialog.dismiss();
            }
        });

        // 로그 아웃 버튼, 로그인 정보 버리기
        Button BtnLogout = (Button) SetDialog.findViewById(R.id.BtnLogout);

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                LoginIntent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(LoginIntent);
                sqlDB = myDBHelper.getWritableDatabase();
                myDBHelper.onUpgrade(sqlDB, 0,1);
                sqlDB.close();
                SetDialog.dismiss();
            }
        });

        // 대화 내역 삭제 버튼
        Button BtnRes = (Button) SetDialog.findViewById(R.id.BtnRes);

        BtnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        //초기화
                        String urlAddress = "http://pingmo.co.kr/Response.php";

                        try {
                            URL url = new URL(urlAddress);

                            InputStream is = url.openStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader reader = new BufferedReader(isr);

                            StringBuffer buffer = new StringBuffer();
                            String line = reader.readLine();
                            while (line != null) {
                                buffer.append(line + "\n");
                                line = reader.readLine();
                            }

                            String jsonData = buffer.toString();

                            JSONObject obj = new JSONObject(jsonData);

                            JSONArray ChatArr = new JSONArray(obj.getString("response"));
                            int length = ChatArr.length();
                            sqlDB = myDBHelper.getWritableDatabase();
                            sqlDB.execSQL("DROP TABLE IF EXISTS ChatLengthTB");
                            sqlDB.execSQL("CREATE TABLE ChatLengthTB (Length int default 0);");
                            sqlDB.execSQL("INSERT INTO ChatLengthTB (Length) VALUES ("+ length + ");");
                            sqlDB.close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                SetDialog.dismiss();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM TalkTB",null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            userID = cursor.getString(cursor.getColumnIndex("userID"));
        }
        cursor.close();
        sqlDB.close();

    }

}