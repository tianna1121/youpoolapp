package io.youpool.youpool;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class Fetchdata extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

    public AsyncResponse delegate = null;
    String data = "";

    public String humanReadable (String numString){
        String[] suffix = new String[]{"","k", "m", "b", "t"};
        int MAX_LENGTH = 4;

        double number = Double.parseDouble(numString);
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public String deltaTime (String delta){
        Long milisec = Long.parseLong(delta);
        String dateFormatted;

        if (milisec > 1000000000) {
            dateFormatted = "Never";
        } else if ((milisec > 1000*60*60) && (milisec <= 1000000000)) {
            dateFormatted = ">1H";
        } else {
            dateFormatted = String.format("%dm, %ds",
                    TimeUnit.MILLISECONDS.toMinutes(milisec),
                    TimeUnit.MILLISECONDS.toSeconds(milisec) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisec))
            );
        }

        Log.i(TAG, "deltaTime: dateFormatted: " + dateFormatted);

        return dateFormatted;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... voids) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        try {
            URL url = new URL("http://shop.youpool.io/data.json");

            HttpURLConnection cc = (HttpURLConnection) url.openConnection();
            InputStream inputStream = cc.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            data = bufferedReader.readLine();
            // Log.i(TAG, "doInBackground: data:" + data);

            JSONArray ja = new JSONArray(data);
            // Log.i(TAG, "doInBackground: JSONArray ja");
            Log.i(TAG, "doInBackground: size of data:" + ja.length());
            for(int i = 0; i <  ja.length(); i++){
                JSONObject jo = (JSONObject) ja.get(i);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("pool", (jo.get("name").toString().toUpperCase().split("\\.")[0]));
                hashMap.put("nblk", deltaTime(jo.get("nblk").toString()));
                hashMap.put("pblk", deltaTime(jo.get("pblk").toString()));
                hashMap.put("payt", deltaTime(jo.get("payt").toString()));
                hashMap.put("miner", jo.get("miner").toString());
                hashMap.put("nhash", humanReadable(jo.get("nhash").toString()));
                hashMap.put("phash", humanReadable(jo.get("phash").toString()));
                hashMap.put("port", jo.get("port").toString());
                // Log.i(TAG, "doInBackground: miner:" + jo.getString("miner"));
                arrayList.add(hashMap);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
        delegate.processFinish(result);
    }
}
