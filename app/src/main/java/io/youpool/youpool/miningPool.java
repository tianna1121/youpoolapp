package io.youpool.youpool;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class miningPool extends AppCompatActivity implements AsyncForPool {
    private static final String TAG = "tianna1121";
    FetchPool process = new FetchPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining_pool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        process.delegate = this;

        Bundle mBundle = this.getIntent().getExtras();
        final String param1 = mBundle.getString("param1");

        process.execute(param1);

    }

    public void fillInfo (String output, int tvId) {
        TextView tv = findViewById(tvId);
        tv.setText(output);

    }

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

    public void drawChart(String output, int chartID, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        output = output.substring(1, output.length());
        output = output.substring(0, output.length()-1);
        String[] tmp = output.split("],");
        Log.i(TAG, "drawChart: output:" + output);
        //Log.i(TAG, "drawChart: output:" + tmp.length + ", First: " + tmp[0]);

        for (int i = 0; i < tmp.length; i++){
            String singleEntry = tmp[i];
            singleEntry = singleEntry.substring(1, singleEntry.length());
            String [] singleElement = singleEntry.split(",");
            entries.add(new Entry(Float.parseFloat(singleElement[0]), Float.parseFloat(singleElement[1])));
        }

        LineChart poolchart = findViewById(chartID);

        LineDataSet setComp1 = new LineDataSet(entries, label);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setCircleRadius(2f);
        setComp1.setDrawValues(false);
        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(dataSets);
        poolchart.setData(data);
        poolchart.getXAxis().setDrawLabels(false);
        poolchart.getAxisRight().setDrawLabels(false);
        poolchart.invalidate(); // refresh
    }

    @Override
    public void processForPool(ArrayList<String> output) {
        fillInfo("Ticket: " + output.get(0).toUpperCase().split("\\.")[0], R.id.poolname);
        fillInfo("Pool Hash: " + humanReadable(output.get(1)), R.id.poolhash);

        float i = Float.parseFloat(output.get(2)) / Float.parseFloat(output.get(9));
        fillInfo("Net Hash: " + humanReadable(Float.toString(i)), R.id.nethash);
        fillInfo("Miner: " + output.get(3), R.id.miner);
        fillInfo("Block Found: " + output.get(4), R.id.blockfound);
        fillInfo("Difficulty: " + humanReadable(output.get(5)), R.id.diff);

        drawChart(output.get(6), R.id.poolhashchart, "Hash Rate");
        drawChart(output.get(7), R.id.pooldiffchart, "Difficulty");
        drawChart(output.get(8), R.id.poolminerchart, "Miners");

    }
}
