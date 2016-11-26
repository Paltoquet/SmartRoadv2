package com.westfolk.smartroadv2;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by thibault on 26/10/2016.
 */
public class WsClient{

    private Context context;
    private static final String BASE_URL = "http://192.168.1.14/";
    private static AsyncHttpClient client = new AsyncHttpClient(7777);

    public WsClient(Context _context){
        context = _context;
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        Log.i("send", params.toString());
        try {
            StringEntity entity = new StringEntity(params.toString());
            client.post(context,getAbsoluteUrl(url), entity,"application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

class CheckpointHandler extends AsyncHttpResponseHandler{

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.i("reception http",new String(responseBody));

        //Write file
        RecordActivity recordActivity = new RecordActivity();
        try {
            recordActivity.writeToFile(new String(responseBody));
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordActivity.readFile();


    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}
