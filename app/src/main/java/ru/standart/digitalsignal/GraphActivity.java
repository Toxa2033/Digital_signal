package ru.standart.digitalsignal;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener {

    Button withNoiseGraph;
    Button onlyNoiseGraph;
    Button normalGraph;
    Button graphPower;
    GraphView graph;
    int durSignal=0;
    double[]signals;
    double[]t;
    double[]noise;
    double amplitudeOfNoise=0;
    final String TAB_1="tag1";
    final String TAB_2="tag2";
    TabHost tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        onlyNoiseGraph=(Button)findViewById(R.id.noiseOnlyGraph);
        withNoiseGraph=(Button)findViewById(R.id.withNoiseGraph);
        normalGraph=(Button)findViewById(R.id.normalGraph);
        graphPower=(Button)findViewById(R.id.graphPower);
         tabs=(TabHost)findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec(TAB_1);

        spec.setContent(R.id.tab1);
        spec.setIndicator("Графики");
        tabs.addTab(spec);

        spec = tabs.newTabSpec(TAB_2);
        spec.setContent(R.id.tab2);
        spec.setIndicator("Расчеты");
        tabs.addTab(spec);


        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals(TAB_2))
                {
                    TextView midPower=(TextView)findViewById(R.id.midPower);
                    TextView signalEnergy=(TextView)findViewById(R.id.signalEnergy);
                    TextView maxAmplitude=(TextView)findViewById(R.id.maxAmplitude);
                    midPower.setText(String.valueOf(getMidPower()));
                    signalEnergy.setText(String.valueOf(getSignalEnergy()));
                    maxAmplitude.setText(String.valueOf(getMaxAmplitude()));

                    TextView mathMid=(TextView)findViewById(R.id.mathMid);
                    TextView dispers=(TextView)findViewById(R.id.dispersion);
                    TextView standartDeviation=(TextView)findViewById(R.id.standartDeviation);
                    standartDeviation.setText(String.valueOf(getStandartDeviation()));
                    dispers.setText(String.valueOf(getDispers()));
                    mathMid.setText(String.valueOf(getMathMid()));

                }
            }
        });

        Intent intent =getIntent();
        signals=intent.getDoubleArrayExtra(MainActivity.ATTRIBUT_SIGNAL);
        t=intent.getDoubleArrayExtra(MainActivity.ATTRIBUT_t);
        durSignal=intent.getIntExtra(MainActivity.ATTRIBUT_DURR_SIGNAL, durSignal);
        amplitudeOfNoise=intent.getDoubleExtra(MainActivity.ATTRIBUT_AMPLITUDE_OF_NOISE, amplitudeOfNoise);

        graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(createDataPoint(t, signals));
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        withNoiseGraph.setOnClickListener(this);
        onlyNoiseGraph.setOnClickListener(this);
        normalGraph.setOnClickListener(this);
        graphPower.setOnClickListener(this);

    }

    LineGraphSeries<DataPoint> createDataPoint(double[]x, double[]y)
    {
        DataPoint[] dataPoint=new DataPoint[x.length];
        for(int i=0; i<x.length;i++)
        {
            dataPoint[i]=new DataPoint(x[i],y[i]);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoint);

        return series;
    }

    double getMathMid()
    {
        double[]signalWithNoise=getNoisesSignal();
        double result=0;

        for(int i=0; i<signalWithNoise.length; i++)
        {
            result+=signalWithNoise[i];
        }

        return result/signalWithNoise.length;
    }

    double getDispers()
    {
        double[]signalWithNoise=getNoisesSignal();
        double result=0;

        for(int i=0; i<signalWithNoise.length; i++)
        {
            result+=Math.pow(signalWithNoise[i],2);
        }
        result=result/signalWithNoise.length;
        result-=Math.pow(getMathMid(),2);
        return result;
    }

    double getStandartDeviation()
    {
        return new BigDecimal(Math.sqrt(getDispers())).setScale(2, RoundingMode.UP).doubleValue();

    }

    double getMidPower()
    {
        double result=0;
        result= getSignalEnergy();
        return result/signals.length;
    }

    double getMaxAmplitude()
    {
        double max=0;
        for(int i=0; i<signals.length; i++)
        {
            double max_t=signals[i];
            for (int j=i; j<signals.length; j++)
            {
                if(signals[j]>max_t)
                {
                    max=signals[j];
                }
            }
        }

        return max;
    }

    double getSignalEnergy()
    {
        double result=0;
        for(int i=0; i<signals.length; i++)
        {
            result+=Math.pow(signals[i],2);
        }
        return result;
    }

    double[]getNoisesSignal()
    {
        double[]noiseSignal=new double[t.length];
        if(noise==null){
            generateNoise();
        }
        for(int i=0; i<t.length; i++)
        {
            noiseSignal[i]=noise[i]+signals[i];
        }

        return noiseSignal;
    }

    void generateNoise()
    {
            noise = new double[t.length];
            Random random = new Random();
            for (int i = 0; i < t.length; i++) {
                noise[i] = -amplitudeOfNoise + (amplitudeOfNoise - (-amplitudeOfNoise)) * random.nextDouble();
            }
    }

    double[]getCurrentPower()
    {
        double[]currPower=new double[t.length];

        for(int i=0; i<t.length; i++)
        {
            currPower[i]=Math.pow(signals[i],2);
        }

        return currPower;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.withNoiseGraph:
            {
                graph.removeAllSeries();
                graph.addSeries(createDataPoint(t,getNoisesSignal()));
                break;
            }

            case R.id.noiseOnlyGraph:
            {
                graph.removeAllSeries();
                generateNoise();
                graph.addSeries(createDataPoint(t,noise));
                break;
            }

            case R.id.normalGraph:
            {
                graph.removeAllSeries();
                graph.addSeries(createDataPoint(t,signals));
                break;
            }

            case R.id.graphPower:
            {
                graph.removeAllSeries();
                graph.addSeries(createDataPoint(t,getCurrentPower()));
                tabs.setCurrentTab(0);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
