package ru.standart.digitalsignal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText durationSignal; //продолжительность сигнала (Т)
    EditText durationPeriodDeskret; // период дескретизации ( )
    EditText numberOfGarmonic; //количество гармоник
    EditText amplitude;       //амплитуда(А)
    EditText freq;       //частота(f)
    EditText phase;           //фаза гармоники(фи)
    EditText amplitudeOfRandomNoise;  //амплитуда случайного шума
    Button next;
    static final String ATTRIBUT_SIGNAL="signal";
    static final String ATTRIBUT_t="t";
    static final String ATTRIBUT_DURR_SIGNAL="dur_signal";
    static final String ATTRIBUT_AMPLITUDE_OF_NOISE="amplitude_of_noise";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        durationSignal=(EditText)findViewById(R.id.durationSignal);
        durationPeriodDeskret=(EditText)findViewById(R.id.durationPeriodDeskret);
        numberOfGarmonic=(EditText)findViewById(R.id.numberOfGarmonic);
        amplitude=(EditText)findViewById(R.id.amplitude);
        freq  =(EditText)findViewById(R.id.freq);
        phase  =(EditText)findViewById(R.id.phase);
        amplitudeOfRandomNoise=(EditText)findViewById(R.id.amplitudeOfRandomNoise);
        next=(Button)findViewById(R.id.button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, GraphActivity.class);
                intent.putExtra(ATTRIBUT_SIGNAL,getSignalArray());
                intent.putExtra(ATTRIBUT_t,getTArray());
                intent.putExtra(ATTRIBUT_DURR_SIGNAL,Integer.valueOf(durationSignal.getText().toString()));
                intent.putExtra(ATTRIBUT_AMPLITUDE_OF_NOISE, Double.valueOf(amplitudeOfRandomNoise.getText().toString()));
                startActivity(intent);
            }
        });
    }




    double[] getArrayAmplitudeOrPhaseOrPhase(EditText anyEdit)
    {
        double[] arr=new double[Integer.valueOf(numberOfGarmonic.getText().toString())];
        double amplitud=0;
        String[]temp=anyEdit.getText().toString().split("-");
        int numberGarmonic=Integer.valueOf(numberOfGarmonic.getText().toString());
        for(int i=0; i<numberGarmonic; i++)
        {
            if(temp.length<=i) {
                arr[i]=0;
            }

            else
            {
                amplitud = Double.valueOf(temp[i]);
                arr[i] = amplitud;
            }
        }

        return arr;
    }

    double getPeriodDeskret()
    {
        return 1/Double.valueOf(durationPeriodDeskret.getText().toString());
    }


    double[] converListToDouble(List<Double>list)
    {
        double[]arr=new double[list.size()];
        for (int i=0; i<list.size();i++)
        {
            arr[i]=list.get(i);
        }
        return arr;
    }


    double[] getTArray()
    {
        List<Double>list=new ArrayList<>();
        double N=Math.floor(Double.valueOf(durationSignal.getText().toString()) / getPeriodDeskret());
        double perid=getPeriodDeskret();
        int j=0;
        for(double i=0; i < Double.valueOf(durationSignal.getText().toString()); i+=perid)
        {
            list.add(i);
        }

        return converListToDouble(list);
    }

    double[] getSignalArray()
    {
        double[]tArray=getTArray();
        double[] arr=new double[tArray.length];
        double signal=0;
        int numbOfGarmonic = Integer.valueOf(numberOfGarmonic.getText().toString());
        double[]amplitudes=getArrayAmplitudeOrPhaseOrPhase(amplitude);
        double[]freqs = getArrayAmplitudeOrPhaseOrPhase(freq);
        double[]phases=getArrayAmplitudeOrPhaseOrPhase(phase);
        for(int i=0; i<tArray.length; i++)
        {
            signal=0;
            for(int j=0; j<numbOfGarmonic; j++)
            {

                signal+=amplitudes[j]*Math.cos(2 * Math.PI * freqs[j] *
                        tArray[i] + phases[j]);
                if(j==numbOfGarmonic-1)
                {
                    arr[i]=signal;

                }
            }
        }
        return arr;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
