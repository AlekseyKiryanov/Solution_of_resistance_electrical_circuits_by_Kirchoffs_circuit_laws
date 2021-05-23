package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import java.util.ArrayList;

import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.data.DBCircuit;

public class SavesActivity extends Activity implements View.OnClickListener {

    private Button[] buttons;
    private ImageButton[] buttons_dell;
    DBCircuit DBConnector;
    int HEIGHT = 5;
    ArrayList<Save> saves = new ArrayList<Save>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        DBConnector = new DBCircuit(this);
        setContentView(R.layout.saves);

        saves = DBConnector.getSaves();
        HEIGHT = saves.size();
        Log.i("TAG", String.valueOf(HEIGHT));
        makeButtons();
    }
    public void Esc (View v){
        onBackPressed();
    }

    public void onClick(View v) {
        int tappedN = 1;
        try {
            Button tappedButton = (Button) v;
            tappedN = getN(tappedButton);
        } catch (Exception e) {

        }
        try {
            ImageButton tappedButton = (ImageButton) v;
            tappedN = getN(tappedButton);
        } catch (Exception e) {

        }

        //Toast.makeText(this, saves.get(tappedN), Toast.LENGTH_SHORT).show();
        //onBackPressed();


        if (tappedN % 10 == 1) {
            tappedN = (int) tappedN / 10;
            Intent intent = new Intent(SavesActivity.this, WorkActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            finish();
            Log.i("TAG", "h");
            String a = (String) saves.get(tappedN).getName();
            intent.putExtra("name_table", a);
            intent.putExtra("height", saves.get(tappedN).getHeight());
            intent.putExtra("width", saves.get(tappedN).getWidth());
            intent.putExtra("solve", saves.get(tappedN).getSolved());
            intent.putExtra("need_opening", true);
            startActivity(intent);

        } else {
            tappedN = (int) tappedN / 10;
            DBConnector.DellCircuit(saves.get(tappedN).getName());
            recreate();
            Log.i("TAG", "Здравствуйте, Вам БЭН");
        }
    }

    int getN(View v) {

        return ((int) v.getTag());
    }

    void makeButtons() {
        buttons = new Button[HEIGHT];
        buttons_dell = new ImageButton[HEIGHT];
        GridLayout buttonsLayout = (GridLayout) findViewById(R.id.SavesLayout);
        buttonsLayout.removeAllViews();
        buttonsLayout.setColumnCount(HEIGHT);
        buttonsLayout.setColumnCount(2);
        for (int i = 0; i < HEIGHT; i++) {
            //for (int j = 0; j < WIDTH; j++) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            buttons_dell[i] = (ImageButton) inflater.inflate(R.layout.save_dell, buttonsLayout, false);
            buttons[i] = (Button) inflater.inflate(R.layout.save, buttonsLayout, false);


            buttons[i].setTag(i * 10 + 1);
            buttons_dell[i].setTag(i * 10 + 2);
            buttonsLayout.addView(buttons_dell[i]);
            buttonsLayout.addView(buttons[i]);
            buttons[i].setOnClickListener(this);
            buttons_dell[i].setOnClickListener(this);
            buttons[i].setText(saves.get(i).getVisibly_name());
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}


