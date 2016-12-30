package com.westfolk.smartroadv2;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

class TimingHandler extends AsyncHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.i("reception http",new String(responseBody));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}

class TimingHandlerText extends AsyncHttpResponseHandler {

    private TextView textView;
    private TextView textViewLast;
    private String lastPredict;

    public TimingHandlerText(TextView textViewProximity, TextView textViewProximityLast) {
        textView = textViewProximity;
        textViewLast = textViewProximityLast;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        Utils utils = new Utils();

        String response = new String(responseBody);
        long data = Long.parseLong(response);
        Log.i("reception http", String.valueOf(data));

        lastPredict = String.valueOf(textView.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        String[] separated = lastPredict.split(" : ");
        if(separated.length > 1) {
            try {
                date = sdf.parse("1970-01-01 " + separated[1]);
                textViewLast.setText("Last prediction : " + utils.getDateFromSecond(date.getTime() / 1000));
                textViewLast.invalidate();
            } catch (ParseException e) {
                textViewLast.setText("Last prediction : no last prediction yet");
                textViewLast.invalidate();
            }
        }
        textView.setText("Prediction : "+ utils.getDateFromSecond(data));
        textView.invalidate();

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


