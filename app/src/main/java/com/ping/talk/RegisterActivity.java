package com.ping.talk;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private myDBHelper myDBHelper;
    SQLiteDatabase sqlDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnReg = (Button) findViewById(R.id.btnReg);
        EditText EditRegId = (EditText) findViewById(R.id.EditRegId);
        EditText EditRegPw = (EditText) findViewById(R.id.EditRegPw);


        myDBHelper = new myDBHelper(this);



        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editText에 입력되어있는 값을 get(가져온다)해온다
                String userID = EditRegId.getText().toString();
                String userPW = EditRegPw.getText().toString();



                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Register2 php에 response
                            boolean success = jasonObject.getBoolean("success");//Register2 php에 sucess

                            if (success) {//회원등록 성공한 경우
                                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();

                                sqlDB = myDBHelper.getWritableDatabase();
                                sqlDB.execSQL("DROP TABLE IF EXISTS TalkTB");
                                sqlDB.execSQL("CREATE TABLE TalkTB ( userID VARCHAR(10) PRIMARY KEY);");
                                sqlDB.execSQL("INSERT INTO TalkTB (userID) VALUES ( '"+ userID  + "');");
                                sqlDB.close();
                                finish();
                            }
                            else{//회원등록 실패한 경우
                                Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                RegisterRequest RegisterRequest = new RegisterRequest(userID, userPW, responseListener);
                RequestQueue que = Volley.newRequestQueue(RegisterActivity.this);
                que.add(RegisterRequest);

            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);

    }

}