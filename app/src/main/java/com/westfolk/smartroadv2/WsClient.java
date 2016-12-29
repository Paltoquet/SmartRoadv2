package com.westfolk.smartroadv2;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONException;
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
    private static final String BASE_URL = "http://192.168.1.71/";
    private static AsyncHttpClient client = new AsyncHttpClient(7777);

    public WsClient(Context _context){
        context = _context;
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show();

        try {
            StringEntity entity;
            if(url == "record") {
                entity = new StringEntity(params.toString().replace("\\",""));
            } else {
                entity = new StringEntity(params.toString());
            }
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
        Utils utils = new Utils();
        try {
            utils.writeToFile(new String(responseBody), "SmartRoad.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //utils.readFile("SmartRoad.json");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}

class TimingHandler extends AsyncHttpResponseHandler{

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.i("reception http",new String(responseBody));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}

class StatsHandler extends AsyncHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.i("reception http",new String(responseBody));

        Utils utils = new Utils();
        try {
            utils.writeToFile(new String(responseBody), "Stats.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}


