package com.example.chatapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText textField;
    private ImageButton sendButton;

    public static final String TAG = "MainActivity";
    public static String uniqueId;

    private String Username;
    private Boolean hasConnection = false;
    private Thread thread2;
    private boolean startTyping = true;
    private int time = 2;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("////YOUR CHAT SERVER/////////");
        } catch (URISyntaxException e) {
        }
    }

@SuppressLint("HandlerLeak")
    Handler handler2 = new Handler(){

@Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.i(TAG, "handleMessage: typing stopped " + startTyping);
            if(time == 0){
                setTitle("Chat Application");
                Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = true;
                time = 2;
        }
    }
};

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Username = getIntent().getStringExtra("username");
        uniqueId = UUID.randomUUID().toString();
        Log.i(TAG, "onCreate: " + uniqueId);

        if (savedInstanceState != null) {
            hasConnection = savedInstanceState.getBoolean("hasConnection");
        }
        if (hasConnection) {
        } else {

            JSONObject userId = new JSONObject();
            try {
                userId.put("username", Username + " Connected");

            } catch (JSONException e) {
                e.printStackTrace();
            }
}
        Log.i(TAG, "onCreate: " + hasConnection);
            hasConnection = true;
        Log.i(TAG, "onCreate: " + Username + " " + "Connected");

}

@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
}
    public void onTypeButtonEnable(){
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                JSONObject onTyping = new JSONObject();
                try {
                    onTyping.put("typing", true);
                    onTyping.put("username", Username);
                    onTyping.put("uniqueId", uniqueId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
Emitter.Listener onNewMessage = new Emitter.Listener() {

@Override
    public void call(final Object... args) {
        runOnUiThread(new Runnable() {

@Override
    public void run() {
        Log.i(TAG, "run: ");
        Log.i(TAG, "run: " + args.length);
        JSONObject data = (JSONObject) args[0];
            String username;
            String message;
            String id;

            try {

            username = data.getString("username");
            message = data.getString("message");
            id = data.getString("uniqueId");

            Log.i(TAG, "run: " + username + message + id);
            Log.i(TAG, "run:4 ");
            Log.i(TAG, "run:5 ");

            } catch (Exception e) {
                return;

                }
            }
        });
    }
};

Emitter.Listener onNewUser = new Emitter.Listener() {

@Override
    public void call(final Object... args) {
        runOnUiThread(new Runnable() {

@Override
    public void run() {
        int length = args.length;
            if (length == 0) {
                return;
}
    String username = args[0].toString();
        try {
            JSONObject object = new JSONObject(username);
            username = object.getString("username");

        } catch (JSONException e) {
            e.printStackTrace();
}
        Log.i(TAG, "run: " + username);

        thread2 = new Thread(
            new Runnable() {

  @Override
        public void run() {
            while(time > 0) {
            synchronized (this){
            try {

            wait(1000);

            Log.i(TAG, "run: typing " + time);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            time--;
            }
            handler2.sendEmptyMessage(0);
        }
    }
});
            thread2.start();
            time = 2;
            }
        });
    }
};

private void addMessage(String username, String message) {
}

public void sendMessage(View view) {
    Log.i(TAG, "sendMessage: ");
    String message = textField.getText().toString().trim();
    if (TextUtils.isEmpty(message)) {

    Log.i(TAG, "sendMessage:2 ");
        return;
}
    textField.setText("");
    JSONObject jsonObject = new JSONObject();
        try {

    jsonObject.put("message", message);
    jsonObject.put("username", Username);
    jsonObject.put("uniqueId", uniqueId);

        } catch (JSONException e) {
            e.printStackTrace();
    }
}

@Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {

        Log.i(TAG, "onDestroy: ");

        JSONObject userId = new JSONObject();

        try {

        userId.put("username", Username + " DisConnected");
        mSocket.emit("connect user", userId);

        } catch (JSONException e) {
            e.printStackTrace();
}
        mSocket.disconnect();
        mSocket.off("chat message", onNewMessage);
        mSocket.off("connect user", onNewUser);

        Username = "";

        } else {
            Log.i(TAG, "onDestroy: is rotating.....");
        }
    }
}
