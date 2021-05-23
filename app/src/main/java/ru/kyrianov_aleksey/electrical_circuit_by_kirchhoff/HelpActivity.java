package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.data.DBCircuit;

public class HelpActivity extends Activity implements View.OnClickListener {
    public ImageView picture;
    public TextView infotext;
    public int k = 1;

    @Override
    public void onClick(View v) {

    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        picture = (ImageView) findViewById(R.id.picture);
        infotext = (TextView) findViewById(R.id.infoinfoinfo);

    }

    public void Right(View v) {
        if (k < 9) {
            k++;
            Print(k);
        }
    }

    public void Left(View v) {
        if (k > 1) {
            k--;
            Print(k);
        }

    }

    public void Print(int i) {
        switch (i) {
            case (1):
                picture.setImageResource(R.drawable.a_stage_1);
                infotext.setText(R.string.help1);
                break;
            case (2):
                picture.setImageResource(R.drawable.a_stage_2);
                infotext.setText(R.string.help2);
                break;
            case (3):
                picture.setImageResource(R.drawable.a_stage_3);
                infotext.setText(R.string.help3);
                break;
            case (4):
                picture.setImageResource(R.drawable.a_stage_4);
                infotext.setText(R.string.help4);
                break;
            case (5):
                picture.setImageResource(R.drawable.a_stage_5);
                infotext.setText(R.string.help5);
                break;
            case (6):
                picture.setImageResource(R.drawable.a_stage_6);
                infotext.setText(R.string.help6);
                break;
            case (7):
                picture.setImageResource(R.drawable.a_stage_7);
                infotext.setText(R.string.help7);
                break;
            case (8):
                picture.setImageResource(R.drawable.a_stage_8);
                infotext.setText(R.string.help8);
                break;
            case (9):
                picture.setImageResource(R.drawable.a_stage_9);
                infotext.setText(R.string.help9);
                break;

        }
    }

}
