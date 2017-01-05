package com.westfolk.smartroadv2;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by thibault on 26/10/2016.
 */
public class WsClient{

    private Context context;
    private static final String BASE_URL = "http://10.188.47.94/";
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
    private TextView textViewHour;
    private TextView arrivedInText;
    private Long time;

    public TimingHandlerText(TextView textViewProximity, TextView textViewProximityHour, TextView arrivedInProximityText, Long timeProximity) {
        textView = textViewProximity;
        textViewHour = textViewProximityHour;
        arrivedInText = arrivedInProximityText;
        time = timeProximity;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        Utils utils = new Utils();

        String response = new String(responseBody);
        long data = Long.parseLong(response);
        Log.i("reception http", String.valueOf(data));

        //lastPredict = String.valueOf(textView.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        /*
        Date date = null;
        String[] separated = lastPredict.split(" : ");
        if(separated.length > 1) {
            try {
                date = sdf.parse("1970-01-01 " + separated[1]);
                textViewLast.setText("Last arrived in : " + utils.getDateFromSecond(date.getTime() / 1000));
                textViewLast.invalidate();
            } catch (ParseException e) {
                textViewLast.setText("Last arrived in : no last prediction yet");
                textViewLast.invalidate();
            }
        }
        */
        textView.setText("Prediction : "+ utils.getDateFromSecond(data));
        textView.invalidate();

        Date d = new Date();
        int nowTime =  d.getSeconds() + d.getMinutes()*60 + d.getHours()*60*60;

        textViewHour.setText("Arrived at : "+  utils.getDateFromSecond((nowTime) + (data)));
        textViewHour.invalidate();

        Date date = null;
        String lastArrived = String.valueOf(arrivedInText.getText());
        String[] separated = lastArrived.split(" : ");
        if(separated.length > 1) {
            try {
                date = sdf.parse("1970-01-01 " + separated[1]);
                arrivedInText.setText("Arrived in : "+utils.getDateFromSecond( (date.getTime() / 1000) - time));
                arrivedInText.invalidate();
            } catch (ParseException e) {
                arrivedInText.setText("Arrived in : Error");
                arrivedInText.invalidate();
            }
        } else {
            arrivedInText.setText("Arrived in : "+utils.getDateFromSecond(data - time));
            arrivedInText.invalidate();
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}

class StatsHandler extends AsyncHttpResponseHandler {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private TextView moyen;
    private Button refresh;
    private Context context;

    public StatsHandler(ExpandableListAdapter listAdapter, ExpandableListView expListView, List<String> listDataHeader, HashMap<String, List<String>> listDataChild, TextView moyen, Button refresh, Context context) {
        this.listAdapter = listAdapter;
        this.expListView = expListView;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.moyen = moyen;
        this.refresh = refresh;
        this.context = context;
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.i("reception http",new String(responseBody));

        /* Store the file, so we can read it when it's off line (next times)*/
        Utils utils = new Utils();
        try {
            utils.writeToFile(new String(responseBody), "Stats.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Load file */
        load((new String(responseBody)));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
        moyen.setText("Error, please load the old file -->");
        refresh.setVisibility(View.VISIBLE);
    }

    public void load(String datas) {
        try {
            prepareListData(datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // preparing list data
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    protected void prepareListData(String datas) throws JSONException {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> data;

        Utils utils = new Utils();

        JSONObject dataJson = new JSONObject(datas);
        JSONArray meanByDayArray = new JSONArray();
        JSONArray minByDayArray = new JSONArray();
        JSONArray maxByDayArray;
        int Moyenne = dataJson.getInt("mean");

        meanByDayArray = dataJson.getJSONArray("meanByDayArray");
        minByDayArray = dataJson.getJSONArray("minByDayArray");
        maxByDayArray = dataJson.getJSONArray("maxByDayArray");

        moyen.setText("Temps moyen : "+utils.getDateFromSecond(Moyenne));

        for (int i = 0; i < meanByDayArray.length(); i++) {
            listDataHeader.add( String.valueOf(meanByDayArray.getJSONObject(i).get("Day")));

            data = new ArrayList<String>();
            String mean = String.valueOf(meanByDayArray.getJSONObject(i).get("Value"));
            String min = String.valueOf(minByDayArray.getJSONObject(i).get("Value"));
            String minHour = String.valueOf(minByDayArray.getJSONObject(i).get("Hour"));
            String max  = String.valueOf(maxByDayArray.getJSONObject(i).get("Value"));
            String maxHour = String.valueOf(maxByDayArray.getJSONObject(i).get("Hour"));

            if(!mean.equals("error")) {
                data.add("Average time : " + utils.getDateFromSecond(Long.parseLong(mean)));
            } else {
                data.add("Average time : " + mean);
            }
            if(!min.equals("error")) {
                data.add("Minimum time : " + utils.getDateFromSecond(Long.parseLong(min)));
            } else {
                data.add("Minimum time : " + min);
            }
            if(!minHour.equals("error")) {
                data.add("Minimum at : " + utils.getDateFromSecond(Long.parseLong(minHour)));
            } else {
                data.add("Minimum at : " + minHour);
            }
            if(!max.equals("error")) {
                data.add("Maximum time : " + utils.getDateFromSecond(Long.parseLong(max)));
            } else {
                data.add("Maximum time : " + max);
            }
            if(!maxHour.equals("error")) {
                data.add("Maximum at : " + utils.getDateFromSecond(Long.parseLong(maxHour)));
            } else {
                data.add("Maximum at : " + maxHour);
            }

            listDataChild.put(listDataHeader.get(i), data);
        }
    }

}
class LeaveHandler extends AsyncHttpResponseHandler {

    private TextView leaveTimeText;
    private TextView leaveHourText;

    public LeaveHandler(TextView leaveTimeText, TextView leaveHourText) {
        this.leaveTimeText = leaveTimeText;
        this.leaveHourText = leaveHourText;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        Utils utils = new Utils();

        String response = new String(responseBody);
        long data = Long.parseLong(response);
        Log.i("reception http", String.valueOf(data));

        leaveTimeText.setText("You'll arrived in : "+ utils.getDateFromSecond(data));
        leaveTimeText.invalidate();

        Date d = new Date();
        int nowTime =  d.getSeconds() + d.getMinutes()*60 + d.getHours()*60*60;

        leaveHourText.setText("You'll arrived at : "+  utils.getDateFromSecond((nowTime) + (data)));
        leaveHourText.invalidate();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.i("reception http","fail");
    }
}


