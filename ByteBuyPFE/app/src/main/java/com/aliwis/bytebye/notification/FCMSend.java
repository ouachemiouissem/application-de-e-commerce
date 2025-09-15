package com.aliwis.bytebye.notification;

import android.content.Context;
import android.os.StrictMode;

import com.aliwis.bytebye.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    public static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "key=AAAAbDOA6vc:APA91bHJQSFt9cwjXxncuafSoV_hQlwHK1NT_WgaELbu71zaOg6B1oJLvnfVUKGf_1JoWZbPgBL86Hi3nm7M6W6QU2y7v3Q89YgARHZ1bEU72PJJ08rmURhbSeW4ziROKkM4IwIZBsp2";

    public static void pushNotification(Context context, String token, String title, String message, String bitmap) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, response -> {

            }, error -> {

            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Speciality", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void pushNotificationToAllUsers(Context context,String TOPIC, String title, String message, String bitmap) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to", "/topics/Users"); // Replace "all_users" with the desired topic name

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, response -> {

            }, error -> {

            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Speciality", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
