package com.ping.talk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private myDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    private String userID = "";
    private String chat = "";
    String chatID = "";
    ListView ListChat;
    String urlAddress = "http://pingmo.co.kr/Response.php";
    Thread Refresh;
    Boolean star = true;
    int Start = 1;

    ArrayList<String> ChatList = new ArrayList<>();
    int TmpList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView TxtID = (TextView) findViewById(R.id.TxtID);
        ListChat = (ListView) findViewById(R.id.ListChat);
        EditText EditChat = (EditText) findViewById(R.id.EditChat);
        Button BtnSend =(Button) findViewById(R.id.BtnSend);

        final Bundle bundle = new Bundle();

        ArrayAdapter<String> ChatAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.listlayout, ChatList);



        myDBHelper = new myDBHelper(this);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM TalkTB", null);
        cursor.moveToFirst();
        userID = cursor.getString(cursor.getColumnIndex("userID"));
        cursor.close();
        sqlDB.close();

        sqlDB = myDBHelper.getReadableDatabase();
        cursor = sqlDB.rawQuery("SELECT * FROM ChatLengthTB",null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            Start = cursor.getInt(cursor.getColumnIndex("Length"));
            cursor.close();
            sqlDB.close();
        }

        TxtID.setText(userID + "님 환영 합니다.");
        Thread Refresh = new Thread() {
            @Override
            public void run() {
                try {
                    while (true && star) {
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
                        String ChatText = "";
                        String Chat = "";
                        TmpList = ChatList.size();
                        ChatList.clear();
                        if(Start > ChatArr.length()){
                            sqlDB = myDBHelper.getWritableDatabase();
                            sqlDB.execSQL("DROP TABLE IF EXISTS ChatLengthTB");
                            sqlDB.close();
                            Start = 0;
                        }
                        for (int i = Start; i < ChatArr.length(); i++) {
                            JSONObject jsontmp = (JSONObject) ChatArr.get(i);
                            ChatText = (String) jsontmp.get("chat");
                            Chat = (String) jsontmp.get("userID");
                            ChatText = "[" + Chat + "]\n" + ChatText;
                            ChatList.add(ChatText);
                        }
                        if(TmpList < ChatList.size()) {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                        TmpList = 0;
                        Thread.sleep(1000);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    };
        Refresh.start();




        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EditChat.length() > 0) {
                    chat = EditChat.getText().toString();
                    EditChat.setText("");
                    new Thread() {
                        @Override
                        public void run() {
                            //초기화
                            String urlCount = "http://pingmo.co.kr/MessageResponse.php";

                            try {
                                Document doc = Jsoup.connect(urlCount).get();

                                String tmp = doc.toString();
                                int tmp2 = Integer.parseInt(tmp.replaceAll("[^0-9]", ""));
                                chatID = Integer.toString(tmp2 + 1);

                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jasonObject = new JSONObject(response);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                };
                                //서버로 volley를 이용해서 요청을 함
                                ChatRequest chatRequest = new ChatRequest(chatID, userID, chat, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
                                queue.add(chatRequest);


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
                                    String ChatText = "";
                                    String Chat = "";
                                    ChatList.clear();

                                    if (Start > ChatArr.length()) {
                                        sqlDB = myDBHelper.getWritableDatabase();
                                        sqlDB.execSQL("DROP TABLE IF EXISTS ChatLengthTB");
                                        sqlDB.close();
                                        Start = 0;
                                    }
                                    for (int i = Start; i < ChatArr.length(); i++) {
                                        JSONObject jsontmp = (JSONObject) ChatArr.get(i);
                                        ChatText = (String) jsontmp.get("chat");
                                        Chat = (String) jsontmp.get("userID");
                                        ChatText = "[" + Chat + "]\n" + ChatText;
                                        ChatList.add(ChatText);
                                    }
                                    Message msg = handler.obtainMessage();
                                    handler.sendMessage(msg);

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();

                }else{
                    Toast.makeText(ChatActivity.this, "메세지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MyArrayAdapter adapter = new MyArrayAdapter(ChatActivity.this, ChatList, userID);
            ListChat.setAdapter(adapter);

        }

    };


}
