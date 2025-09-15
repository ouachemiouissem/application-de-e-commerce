package com.aliwis.bytebye.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Constants {
    public final static String DATABASE_URL = "https://bytebye-10494-default-rtdb.europe-west1.firebasedatabase.app/";
    public final static String TOPIC = "/topics/Users";
    public static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String SECRET_KEY  = "key=AAAAbDOA6vc:APA91bHJQSFt9cwjXxncuafSoV_hQlwHK1NT_WgaELbu71zaOg6B1oJLvnfVUKGf_1JoWZbPgBL86Hi3nm7M6W6QU2y7v3Q89YgARHZ1bEU72PJJ08rmURhbSeW4ziROKkM4IwIZBsp2";

    public final static String CONTENT_TYPE = "application/json";

    public static Bitmap getBitmapFromEncodeString(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}

