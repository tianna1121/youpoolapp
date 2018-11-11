package io.youpool.youpool;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FetchPool extends AsyncTask<String, Void, ArrayList<String>> {

    public AsyncForPool delegate = null;
    String data = "";
    private ArrayList<String> rArray = new ArrayList<>();

    @Override
    protected ArrayList<String> doInBackground(String... params) {

        String urlPoolJson = params[0];

        try {
            URL url = new URL(urlPoolJson);
            Log.i(TAG, "doInBackground: url: " + urlPoolJson);

            HttpURLConnection cc = (HttpURLConnection) url.openConnection();
            InputStream inputStream = cc.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            data = bufferedReader.readLine();

            JSONObject jo = new JSONObject(data);

            rArray.add(jo.getJSONObject("config").getString("poolHost"));
            rArray.add(jo.getJSONObject("pool").getString("hashrate"));
            rArray.add(jo.getJSONObject("network").getString("difficulty"));
            rArray.add(jo.getJSONObject("pool").getString("miners"));
            rArray.add(jo.getJSONObject("pool").getString("totalBlocks"));
            rArray.add(jo.getJSONObject("pool").getString("totalDiff"));

            rArray.add(jo.getJSONObject("charts").getString("hashrate"));
            rArray.add(jo.getJSONObject("charts").getString("difficulty"));
            rArray.add(jo.getJSONObject("charts").getString("miners"));

            rArray.add(jo.getJSONObject("config").getString("coinDifficultyTarget"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rArray;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        delegate.processForPool(result);
    }
}

