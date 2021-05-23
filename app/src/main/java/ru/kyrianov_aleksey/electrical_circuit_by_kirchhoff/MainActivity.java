package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.data.DBCircuit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View v) {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.main);

    }
    public void newCircuit(View v) {
        Intent intent = new Intent(MainActivity.this, WorkActivity.class);
        startActivity(intent);
    }
    public void newOpen(View v) {
        Intent intent = new Intent(MainActivity.this, SavesActivity.class);
        startActivity(intent);
    }
    public void newHelp(View v) {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }
}
