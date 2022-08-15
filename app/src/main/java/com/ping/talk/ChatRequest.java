package com.ping.talk;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChatRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://pingmo.co.kr/Message.php";
    private Map<String,String>map;

    public ChatRequest(String chatID, String userID, String chat,Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("chatID", chatID);
        map.put("userID",userID);
        map.put("chat",chat);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }


}
