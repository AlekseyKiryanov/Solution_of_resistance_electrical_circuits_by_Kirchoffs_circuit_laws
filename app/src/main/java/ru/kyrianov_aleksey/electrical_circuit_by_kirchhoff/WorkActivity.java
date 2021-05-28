package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.data.DBCircuit;


public class WorkActivity extends Activity implements OnClickListener {
    DBCircuit DBConnector;
    final Context context = this;
    public int WIDTH;
    public int HEIGHT;
    private char TypeNewElement = 'N';
    private int stage_new_element = 1;
    private int x_new_element = 0;
    private int y_new_element = 0;
    private int x_info = 0;
    private int y_info = 0;
    private int solved = 0;
    private ImageButton[][] cells;
    private ElectricalCircuit electricalcircuit;
    private int number_new_element = 0;
    private String last_save = "";
    SimpleCursorAdapter scAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        setContentView(R.layout.cells);
        DBConnector = new DBCircuit(this);


        String table_name = "";
        Boolean need_opening = false;
        int new_height = 0;
        int new_width = 0;
        int new_solve = 0;
        try {
            Log.i("TAG", "f");
            table_name = getIntent().getExtras().getString("name_table");
            need_opening = getIntent().getExtras().getBoolean("need_opening");
            new_height = getIntent().getExtras().getInt("height");
            new_width = getIntent().getExtras().getInt("width");
            new_solve = getIntent().getExtras().getInt("solve");
        } catch (Exception e) {
            Log.i("TAG", "h");
        }


        if (need_opening) {

            HEIGHT = new_height;
            WIDTH = new_width;
            solved = new_solve;
            if (solved == 1) {
                ImageButton solve = (ImageButton) findViewById(R.id.resolve);
                TextView solvek = (TextView) findViewById(R.id.resolvetext);
                solve.setImageResource(R.drawable.resolve_first);
                solvek.setText("Перестроить");
            }
            electricalcircuit = new ElectricalCircuit(HEIGHT, WIDTH);
            makeCells();

            Opening(table_name, HEIGHT, WIDTH);
        } else {
            setSize();
        }
    }


    public void setSize() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.set_size, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

        mDialogBuilder.setView(promptsView);

        final EditText height_EditText = (EditText) promptsView.findViewById(R.id.set_height);
        final EditText width_EditText = (EditText) promptsView.findViewById(R.id.set_width);

        mDialogBuilder
                .setCancelable(false)
                .setTitle("Укажите размеры схемы")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                HEIGHT = Integer.parseInt((String) height_EditText.getText().toString());
                                Log.i("TAG", (String) height_EditText.getText().toString());
                                WIDTH = Integer.parseInt((String) width_EditText.getText().toString());
                                electricalcircuit = new ElectricalCircuit(HEIGHT, WIDTH);
                                makeCells();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();

        alertDialog.show();

    }


    public void onClick(View v) {
        breakInfo();
        ImageButton tappedCell = (ImageButton) v;
        int tappedX = getX(tappedCell);
        int tappedY = getY(tappedCell);
        if (((TypeNewElement == 'N') && (stage_new_element != 3)) || (stage_new_element == 0)) {
            x_info = tappedX;
            y_info = tappedY;
            giveInformation();
        } else if (stage_new_element == 1) {
            if ((electricalcircuit.giveTypeElement(tappedY, tappedX) != 'N') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'X') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'Y') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'Z')) {
                Toast.makeText(this, R.string.err1, Toast.LENGTH_SHORT).show();
            } else {
                x_new_element = tappedX;
                y_new_element = tappedY;
                stage_new_element = 2;
            }

        } else if (stage_new_element == 2) {
            if ((electricalcircuit.giveTypeElement(tappedY, tappedX) != 'N') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'X') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'Y') && (electricalcircuit.giveTypeElement(tappedY, tappedX) != 'Z')) {
                Toast.makeText(this, R.string.err1, Toast.LENGTH_SHORT).show();
            } else if ((x_new_element != tappedX) && (y_new_element != tappedY)) {
                Toast.makeText(this, R.string.err2, Toast.LENGTH_SHORT).show();
            } else if (((Math.abs(x_new_element - tappedX) <= 1) && (Math.abs(y_new_element - tappedY) <= 1)) && TypeNewElement != 'X') {
                Toast.makeText(this, R.string.err3, Toast.LENGTH_SHORT).show();
            } else {
                if ((x_new_element == tappedX) && (y_new_element == tappedY)) {
                    if (electricalcircuit.giveTypeElement(tappedY, tappedX) == 'N') {
                        electricalcircuit.addUzel(tappedY, tappedX);
                    }
                    electricalcircuit.setTypeElement('X', tappedY, tappedX);


                    setPicture(tappedY, tappedX);
                    PaintButtonOK();


                } else if (x_new_element == tappedX) {
                    int c = (int) Math.min(y_new_element, tappedY) + Math.abs(y_new_element - tappedY) / 2;

                    boolean flag = true;
                    for (int i = Math.min(y_new_element, tappedY) + 1; i < Math.max(y_new_element, tappedY); i++) {
                        if ((TypeNewElement == 'X') && (electricalcircuit.giveTypeElement(i, tappedX) != 'X') && (electricalcircuit.giveTypeElement(i, tappedX) != 'N')) {
                            flag = false;
                        }
                        if ((TypeNewElement != 'X') && (electricalcircuit.giveTypeElement(i, tappedX) != 'N')) {
                            flag = false;
                        }
                    }


                    if (flag) {

                        for (int i = Math.min(y_new_element, tappedY) + 1; i < Math.max(y_new_element, tappedY); i++) {
                            electricalcircuit.setTypeElement('X', i, tappedX);
                            electricalcircuit.setUpElement(true, i, tappedX);
                            electricalcircuit.setDownElement(true, i, tappedX);
                            setPicture(i, tappedX);

                            if ((i != c) || (TypeNewElement == 'X')) {
                                electricalcircuit.addUzel(i, tappedX);
                            }
                        }


                        electricalcircuit.setTypeElement(TypeNewElement, c, tappedX);


                        if (TypeNewElement != 'X') {
                            if (y_new_element > tappedY) {
                                electricalcircuit.setUpElement(true, c, tappedX);
                                electricalcircuit.setDownElement(false, c, tappedX);
                            } else {
                                electricalcircuit.setDownElement(true, c, tappedX);
                                electricalcircuit.setUpElement(false, c, tappedX);
                            }
                            PaintElementOK(c, tappedX);
                            electricalcircuit.addElement(c, tappedX);


                            electricalcircuit.resetNumbersElement();

                        }
                        PaintButtonOK();


                        electricalcircuit.setDownElement(true, Math.min(y_new_element, tappedY), tappedX);
                        if (electricalcircuit.giveTypeElement(Math.min(y_new_element, tappedY), tappedX) == 'N') {
                            electricalcircuit.addUzel(Math.min(y_new_element, tappedY), tappedX);
                        }
                        electricalcircuit.setTypeElement('X', Math.min(y_new_element, tappedY), tappedX);

                        setPicture(Math.min(y_new_element, tappedY), tappedX);


                        electricalcircuit.setUpElement(true, Math.max(y_new_element, tappedY), tappedX);
                        if (electricalcircuit.giveTypeElement(Math.max(y_new_element, tappedY), tappedX) == 'N') {
                            electricalcircuit.addUzel(Math.max(y_new_element, tappedY), tappedX);
                        }
                        electricalcircuit.setTypeElement('X', Math.max(y_new_element, tappedY), tappedX);

                        setPicture(Math.max(y_new_element, tappedY), tappedX);
                        TypeNewElement = 'N';
                        stage_new_element = 0;
                    } else {
                        Toast.makeText(this, "Нельзя пересекать другие элементы!", Toast.LENGTH_SHORT).show();
                        stage_new_element = 2;
                    }
                } else if (y_new_element == tappedY) {
                    int c = (int) Math.min(x_new_element, tappedX) + Math.abs(x_new_element - tappedX) / 2;


                    boolean flag = true;
                    for (int i = Math.min(x_new_element, tappedX) + 1; i < Math.max(x_new_element, tappedX); i++) {
                        if ((TypeNewElement == 'X') && (electricalcircuit.giveTypeElement(tappedY, i) != 'X') && (electricalcircuit.giveTypeElement(tappedY, i) != 'N')) {
                            flag = false;
                        }
                        if ((TypeNewElement != 'X') && (electricalcircuit.giveTypeElement(tappedY, i) != 'N')) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        for (int i = Math.min(x_new_element, tappedX) + 1; i < Math.max(x_new_element, tappedX); i++) {
                            electricalcircuit.setTypeElement('X', tappedY, i);
                            electricalcircuit.setRightElement(true, tappedY, i);
                            electricalcircuit.setLeftElement(true, tappedY, i);
                            setPicture(tappedY, i);

                            if ((i != c) || (TypeNewElement == 'X')) {
                                electricalcircuit.addUzel(tappedY, i);
                            }
                        }


                        electricalcircuit.setTypeElement(TypeNewElement, tappedY, c);

                        if (TypeNewElement != 'X') {
                            if (x_new_element > tappedX) {
                                electricalcircuit.setRightElement(false, tappedY, c);
                                electricalcircuit.setLeftElement(true, tappedY, c);
                            } else {
                                electricalcircuit.setRightElement(true, tappedY, c);
                                electricalcircuit.setLeftElement(false, tappedY, c);
                            }
                            PaintElementOK(tappedY, c);
                             number_new_element++;
                            electricalcircuit.addElement(tappedY, c);

                            electricalcircuit.resetNumbersElement();
                        }
                        PaintButtonOK();

                        electricalcircuit.setRightElement(true, tappedY, Math.min(x_new_element, tappedX));
                        if (electricalcircuit.giveTypeElement(tappedY, Math.min(x_new_element, tappedX)) == 'N') {
                            electricalcircuit.addUzel(tappedY, Math.min(x_new_element, tappedX));
                        }
                        electricalcircuit.setTypeElement('X', tappedY, Math.min(x_new_element, tappedX));
                        setPicture(tappedY, Math.min(x_new_element, tappedX));


                        electricalcircuit.setLeftElement(true, tappedY, Math.max(x_new_element, tappedX));
                        if (electricalcircuit.giveTypeElement(tappedY, Math.max(x_new_element, tappedX)) != 'X') {
                            electricalcircuit.addUzel(tappedY, Math.max(x_new_element, tappedX));
                        }
                        electricalcircuit.setTypeElement('X', tappedY, Math.max(x_new_element, tappedX));
                        setPicture(tappedY, Math.max(x_new_element, tappedX));

                        TypeNewElement = 'N';
                        stage_new_element = 0;
                    } else {
                        Toast.makeText(this, "Нельзя пересекать другие элементы!", Toast.LENGTH_SHORT).show();
                        stage_new_element = 2;
                    }

                }


            }


        } else if (stage_new_element == 3) {
            cells[tappedY][tappedX].setImageResource(R.drawable.white);
            PaintButtonOK();

            if (electricalcircuit.giveTypeElement(tappedY, tappedX) == 'X') {
                electricalcircuit.dellUsel(tappedY, tappedX);
            } else if ((electricalcircuit.giveTypeElement(tappedY, tappedX) == 'R') || (electricalcircuit.giveTypeElement(tappedY, tappedX) == 'E') || (electricalcircuit.giveTypeElement(tappedY, tappedX) == 'J')) {
                electricalcircuit.dellElement(electricalcircuit.getNumberElement(tappedY, tappedX));
                electricalcircuit.resetNumbersElement();
            }

            electricalcircuit.setTypeElement('N', tappedY, tappedX);

            electricalcircuit.setDownElement(false, tappedY, tappedX);
            electricalcircuit.setUpElement(false, tappedY, tappedX);
            electricalcircuit.setLeftElement(false, tappedY, tappedX);
            electricalcircuit.setRightElement(false, tappedY, tappedX);
            electricalcircuit.setPicturElement(0, tappedY, tappedX);
            electricalcircuit.setIElement(0, tappedY, tappedX);
            electricalcircuit.setEElement(0, tappedY, tappedX);
            electricalcircuit.setRElement(0, tappedY, tappedX);
            electricalcircuit.setUElement(0, tappedY, tappedX);
            electricalcircuit.setJElement(0, tappedY, tappedX);


            if ((tappedY - 1 > 0) && ((electricalcircuit.giveTypeElement(tappedY - 1, tappedX) == 'X'))) {
                electricalcircuit.setDownElement(false, tappedY - 1, tappedX);
                setPicture(tappedY - 1, tappedX);
            }
            if ((tappedY + 1 < HEIGHT) && ((electricalcircuit.giveTypeElement(tappedY + 1, tappedX) == 'X'))) {
                electricalcircuit.setUpElement(false, tappedY + 1, tappedX);
                setPicture(tappedY + 1, tappedX);
            }
            if ((tappedX - 1 > 0) && ((electricalcircuit.giveTypeElement(tappedY, tappedX - 1) == 'X'))) {
                electricalcircuit.setRightElement(false, tappedY, tappedX - 1);
                setPicture(tappedY, tappedX - 1);
            }
            if ((tappedX + 1 < WIDTH) && ((electricalcircuit.giveTypeElement(tappedY, tappedX + 1) == 'X'))) {
                electricalcircuit.setLeftElement(false, tappedY, tappedX + 1);
                setPicture(tappedY, tappedX + 1);
            }


            PaintButtonOK();
            stage_new_element = 0;
        }


    }


    private void setPicture(int y, int x) {
        if (electricalcircuit.isDownElement(y, x)) {
            if (electricalcircuit.isUpElement(y, x)) {
                if (electricalcircuit.isLeftElement(y, x)) {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.urdl);
                        electricalcircuit.setPicturElement(11, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.udl);
                        electricalcircuit.setPicturElement(12, y, x);
                    }
                } else {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.urd);
                        electricalcircuit.setPicturElement(13, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.ud);
                        electricalcircuit.setPicturElement(14, y, x);
                    }
                }
            } else {
                if (electricalcircuit.isLeftElement(y, x)) {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.rdl);
                        electricalcircuit.setPicturElement(15, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.dr);
                        electricalcircuit.setPicturElement(16, y, x);
                    }
                } else {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.rd);
                        electricalcircuit.setPicturElement(17, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.end_d);
                        electricalcircuit.setPicturElement(18, y, x);
                    }
                }
            }
        } else {
            if (electricalcircuit.isUpElement(y, x)) {
                if (electricalcircuit.isLeftElement(y, x)) {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.url);
                        electricalcircuit.setPicturElement(19, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.ul);
                        electricalcircuit.setPicturElement(20, y, x);
                    }
                } else {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.ur);
                        electricalcircuit.setPicturElement(21, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.end_u);
                        electricalcircuit.setPicturElement(22, y, x);
                    }
                }
            } else {
                if (electricalcircuit.isLeftElement(y, x)) {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.rl);
                        electricalcircuit.setPicturElement(23, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.end_l);
                        electricalcircuit.setPicturElement(24, y, x);
                    }
                } else {
                    if (electricalcircuit.isRightElement(y, x)) {
                        cells[y][x].setImageResource(R.drawable.end_r);
                        electricalcircuit.setPicturElement(25, y, x);
                    } else {
                        cells[y][x].setImageResource(R.drawable.end);
                        electricalcircuit.setPicturElement(26, y, x);
                    }
                }
            }
        }
    }


    private void givePicture(int y, int x) {
        int a = electricalcircuit.getPictureElement(y, x);
        switch (a) {
            case (0):
                cells[y][x].setImageResource(R.drawable.white);
                break;
            case (1):
                cells[y][x].setImageResource(R.drawable.rezistor_rl);
                break;
            case (2):
                cells[y][x].setImageResource(R.drawable.rezistor_ud);
                break;
            case (3):
                cells[y][x].setImageResource(R.drawable.ist_rl);
                break;
            case (4):
                cells[y][x].setImageResource(R.drawable.ist_lr);
                break;
            case (5):
                cells[y][x].setImageResource(R.drawable.ist_ud);
                break;
            case (6):
                cells[y][x].setImageResource(R.drawable.ist_du);
                break;
            case (7):
                cells[y][x].setImageResource(R.drawable.generator_lr);
                break;
            case (8):
                cells[y][x].setImageResource(R.drawable.generator_rl);
                break;
            case (9):
                cells[y][x].setImageResource(R.drawable.generator_ud);
                break;
            case (10):
                cells[y][x].setImageResource(R.drawable.generator_du);
                break;
            case (11):
                cells[y][x].setImageResource(R.drawable.urdl);
                break;
            case (12):
                cells[y][x].setImageResource(R.drawable.udl);
                break;
            case (13):
                cells[y][x].setImageResource(R.drawable.urd);
                break;
            case (14):
                cells[y][x].setImageResource(R.drawable.ud);
                break;
            case (15):
                cells[y][x].setImageResource(R.drawable.rdl);
                break;
            case (16):
                cells[y][x].setImageResource(R.drawable.dr);
                break;
            case (17):
                cells[y][x].setImageResource(R.drawable.rd);
                break;
            case (18):
                cells[y][x].setImageResource(R.drawable.end_d);
                break;
            case (19):
                cells[y][x].setImageResource(R.drawable.url);
                break;
            case (20):
                cells[y][x].setImageResource(R.drawable.ul);
                break;
            case (21):
                cells[y][x].setImageResource(R.drawable.ur);
                break;
            case (22):
                cells[y][x].setImageResource(R.drawable.end_u);
                break;
            case (23):
                cells[y][x].setImageResource(R.drawable.rl);
                break;
            case (24):
                cells[y][x].setImageResource(R.drawable.end_l);
                break;
            case (25):
                cells[y][x].setImageResource(R.drawable.end_r);
                break;
            case (26):
                cells[y][x].setImageResource(R.drawable.end);
                break;
        }
    }

    public void
    Opening(String name_table, int height, int width) {
        Log.i("i", "d");
        ArrayList<ElectricElement> elements = new ArrayList<ElectricElement>();
        elements = DBConnector.getElements(name_table);
        Log.i("i", String.valueOf(elements.size()));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                electricalcircuit.setElement(elements.get(i * width + j), i, j);
                givePicture(i, j);
                if (electricalcircuit.giveTypeElement(i, j) == 'X') {
                    electricalcircuit.addUzel(i, j);
                }
                if ((electricalcircuit.giveTypeElement(i, j) == 'R') || (electricalcircuit.giveTypeElement(i, j) == 'E') || (electricalcircuit.giveTypeElement(i, j) == 'J')) {
                    electricalcircuit.addElement(i, j);
                    electricalcircuit.resetNumbersElement();
                }
            }
        }
    }


    int getX(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]);
    }

    int getY(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    void makeCells() {
        cells = new ImageButton[HEIGHT][WIDTH];
        GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(HEIGHT);
        cellsLayout.setColumnCount(WIDTH);
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cells[i][j] = (ImageButton) inflater.inflate(R.layout.cell, cellsLayout, false);

                cells[i][j].setTag(i + "," + j);
                cellsLayout.addView(cells[i][j]);
                cells[i][j].setOnClickListener(this);
            }
    }


    public void PaintElementOK(int y, int x) {

        if (electricalcircuit.giveTypeElement(y, x) == 'R') {
            if (electricalcircuit.isLeftElement(y, x) || electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.rezistor_rl);
                electricalcircuit.setPicturElement(1, y, x);
            } else {
                cells[y][x].setImageResource(R.drawable.rezistor_ud);
                electricalcircuit.setPicturElement(2, y, x);
            }

        }

        if (electricalcircuit.giveTypeElement(y, x) == 'E') {
            if (electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.ist_rl);
                electricalcircuit.setPicturElement(3, y, x);
            } else if (electricalcircuit.isLeftElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.ist_lr);
                electricalcircuit.setPicturElement(4, y, x);
            } else if (electricalcircuit.isUpElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.ist_ud);
                electricalcircuit.setPicturElement(5, y, x);
            } else {
                cells[y][x].setImageResource(R.drawable.ist_du);
                electricalcircuit.setPicturElement(6, y, x);
            }
        }

        if (electricalcircuit.giveTypeElement(y, x) == 'J') {
            if (electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.generator_lr);
                electricalcircuit.setPicturElement(7, y, x);
            } else if (electricalcircuit.isLeftElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.generator_rl);
                electricalcircuit.setPicturElement(8, y, x);
            } else if (electricalcircuit.isUpElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.generator_ud);
                electricalcircuit.setPicturElement(9, y, x);
            } else {
                cells[y][x].setImageResource(R.drawable.generator_du);
                electricalcircuit.setPicturElement(10, y, x);
            }
        }
    }

    public void PaintElementChoosen(int y, int x) {

        if (electricalcircuit.giveTypeElement(y, x) == 'R') {
            if (electricalcircuit.isLeftElement(y, x) || electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.chhosen_rezistor_rl);
            } else {
                cells[y][x].setImageResource(R.drawable.choosen_rezistor_ud);
            }

        }

        if (electricalcircuit.giveTypeElement(y, x) == 'E') {
            if (electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_ist_rl);
            } else if (electricalcircuit.isLeftElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_ist_lr);
            } else if (electricalcircuit.isUpElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_ist_ud);
            } else {
                cells[y][x].setImageResource(R.drawable.chosen_ist_du);
            }
        }

        if (electricalcircuit.giveTypeElement(y, x) == 'J') {
            if (electricalcircuit.isRightElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_generator_lr);
            } else if (electricalcircuit.isLeftElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_generator_rl);
            } else if (electricalcircuit.isUpElement(y, x)) {
                cells[y][x].setImageResource(R.drawable.choosen_generator_ud);
            } else {
                cells[y][x].setImageResource(R.drawable.choosen_generator_du);
            }
        }
    }

    public void PaintButtonOK() {
        ImageButton new_resistor = (ImageButton) findViewById(R.id.ANewResist);
        ImageButton new_ist = (ImageButton) findViewById(R.id.ANewIst);
        ImageButton new_generator = (ImageButton) findViewById(R.id.ANewGenerator);
        ImageButton new_legth = (ImageButton) findViewById(R.id.ANewLength);
        ImageButton dell = (ImageButton) findViewById(R.id.ADell);
        new_resistor.setImageResource(R.drawable.resistor);
        new_ist.setImageResource(R.drawable.ist);
        new_generator.setImageResource(R.drawable.generator);
        new_legth.setImageResource(R.drawable.lenght);
        dell.setImageResource(R.drawable.dell);
    }


    public void NewResistor(View view) {
        if (solved == 0) {
            breakInfo();
            ImageButton new_resistor = (ImageButton) findViewById(R.id.ANewResist);
            if (TypeNewElement != 'R') {
                PaintButtonOK();
                TypeNewElement = 'R';
                stage_new_element = 1;
                new_resistor.setImageResource(R.drawable.resistor_choosen);
            } else {
                TypeNewElement = 'N';
                stage_new_element = 0;
                PaintButtonOK();
            }
        } else {
            Toast.makeText(this, R.string.err5, Toast.LENGTH_LONG).show();
        }
    }

    public void NewIst(View view) {
        if (solved == 0) {
            breakInfo();
            ImageButton new_ist = (ImageButton) findViewById(R.id.ANewIst);
            if (TypeNewElement != 'E') {
                PaintButtonOK();
                TypeNewElement = 'E';
                stage_new_element = 1;
                new_ist.setImageResource(R.drawable.ist_choosen);
            } else {
                TypeNewElement = 'N';
                stage_new_element = 0;
                PaintButtonOK();
            }
        } else {
            Toast.makeText(this, R.string.err5, Toast.LENGTH_LONG).show();
        }
    }

    public void NewGenerator(View view) {
        if (solved == 0) {
            breakInfo();
            ImageButton new_generator = (ImageButton) findViewById(R.id.ANewGenerator);
            if (TypeNewElement != 'J') {
                PaintButtonOK();
                TypeNewElement = 'J';
                stage_new_element = 1;
                new_generator.setImageResource(R.drawable.generator_choosen);
            } else {
                TypeNewElement = 'N';
                stage_new_element = 0;
                PaintButtonOK();
            }
        } else {
            Toast.makeText(this, R.string.err5, Toast.LENGTH_LONG).show();
        }
    }

    public void NewLength(View view) {
        if (solved == 0) {
            breakInfo();
            ImageButton new_length = (ImageButton) findViewById(R.id.ANewLength);
            if (TypeNewElement != 'X') {
                PaintButtonOK();
                TypeNewElement = 'X';
                stage_new_element = 1;
                new_length.setImageResource(R.drawable.lenght_choosen);
            } else {
                TypeNewElement = 'N';
                stage_new_element = 0;
                PaintButtonOK();
            }
        } else {
            Toast.makeText(this, R.string.err5, Toast.LENGTH_LONG).show();
        }
    }

    public void giveInformation() {

        PaintElementChoosen(y_info, x_info);


        EditText ReditView = findViewById(R.id.RRR);
        TextView RtextView = findViewById(R.id.R);
        ImageButton Rimagebutton = findViewById(R.id.RR);
        EditText UeditView = findViewById(R.id.UUU);
        TextView UtextView = findViewById(R.id.U);
        ImageButton Uimagebutton = findViewById(R.id.UU);
        EditText IeditView = findViewById(R.id.III);
        TextView ItextView = findViewById(R.id.I);
        ImageButton Iimagebutton = findViewById(R.id.II);
        ImageButton IIimagebutton = findViewById(R.id.IIII);
        ReditView.setEnabled(true);
        UeditView.setEnabled(true);
        IeditView.setEnabled(true);


        if (solved == 0) {
            if (electricalcircuit.giveTypeElement(y_info, x_info) == 'X') {
                Toast.makeText(this, R.string.info1, Toast.LENGTH_SHORT).show();

            } else if (electricalcircuit.giveTypeElement(y_info, x_info) == 'N') {
                Toast.makeText(this, R.string.info2, Toast.LENGTH_SHORT).show();
            } else if (electricalcircuit.giveTypeElement(y_info, x_info) == 'R') {
                ReditView.setVisibility(View.VISIBLE);
                RtextView.setVisibility(View.VISIBLE);
                Rimagebutton.setVisibility(View.VISIBLE);

                RtextView.setText(" R =");
                if ((electricalcircuit.getRElement(y_info, x_info) < 1) && (electricalcircuit.getRElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                } else if ((electricalcircuit.getRElement(y_info, x_info) <= 1000) && (electricalcircuit.getRElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                } else if (electricalcircuit.getRElement(y_info, x_info) == 0) {
                    ReditView.setText(String.format("%.0f", electricalcircuit.getRElement(y_info, x_info)));
                } else {
                    ReditView.setText(String.format("%.2e", electricalcircuit.getRElement(y_info, x_info)));
                }
            } else if (electricalcircuit.giveTypeElement(y_info, x_info) == 'E') {
                ReditView.setVisibility(View.VISIBLE);
                RtextView.setVisibility(View.VISIBLE);
                Rimagebutton.setVisibility(View.VISIBLE);
                UeditView.setVisibility(View.VISIBLE);
                UtextView.setVisibility(View.VISIBLE);
                Uimagebutton.setVisibility(View.VISIBLE);

                RtextView.setText(" r =");
                UtextView.setText(" E =");
                if ((electricalcircuit.getRElement(y_info, x_info) < 1) && (electricalcircuit.getRElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                }else if ((electricalcircuit.getRElement(y_info, x_info) <= 1000) && (electricalcircuit.getRElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                } else if (electricalcircuit.getRElement(y_info, x_info) == 0) {
                    ReditView.setText(String.format("%.0f", electricalcircuit.getRElement(y_info, x_info)));
                } else {
                    ReditView.setText(String.format("%.2e", electricalcircuit.getRElement(y_info, x_info)));
                }
                if ((electricalcircuit.getEElement(y_info, x_info) < 1) && (electricalcircuit.getEElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    UeditView.setText(decimalFormat.format(electricalcircuit.getEElement(y_info, x_info)));
                }else if ((electricalcircuit.getEElement(y_info, x_info) <= 1000) && (electricalcircuit.getEElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    UeditView.setText(decimalFormat.format(electricalcircuit.getEElement(y_info, x_info)));
                } else if (electricalcircuit.getEElement(y_info, x_info) == 0) {
                    UeditView.setText(String.format("%.0f", electricalcircuit.getEElement(y_info, x_info)));
                } else {
                    UeditView.setText(String.format("%.2e", electricalcircuit.getEElement(y_info, x_info)));
                }

            } else if (electricalcircuit.giveTypeElement(y_info, x_info) == 'J') {
                ReditView.setVisibility(View.VISIBLE);
                RtextView.setVisibility(View.VISIBLE);
                Rimagebutton.setVisibility(View.VISIBLE);
                IeditView.setVisibility(View.VISIBLE);
                ItextView.setVisibility(View.VISIBLE);
                Iimagebutton.setVisibility(View.VISIBLE);

                RtextView.setText(" r =");
                ItextView.setText(" J =");
                if ((electricalcircuit.getRElement(y_info, x_info) < 1) && (electricalcircuit.getRElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                }else if ((electricalcircuit.getRElement(y_info, x_info) <= 1000) && (electricalcircuit.getRElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                } else if (electricalcircuit.getRElement(y_info, x_info) == 0) {
                    ReditView.setText(String.format("%.0f", electricalcircuit.getRElement(y_info, x_info)));
                } else {
                    ReditView.setText(String.format("%.2e", electricalcircuit.getRElement(y_info, x_info)));
                }
                if ((electricalcircuit.getJElement(y_info, x_info) < 1) && (electricalcircuit.getJElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    IeditView.setText(decimalFormat.format(electricalcircuit.getJElement(y_info, x_info)));
                } else if ((electricalcircuit.getJElement(y_info, x_info) <= 1000) && (electricalcircuit.getJElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    IeditView.setText(decimalFormat.format(electricalcircuit.getJElement(y_info, x_info)));
                }else if (electricalcircuit.getJElement(y_info, x_info) == 0) {
                    IeditView.setText(String.format("%.0f", electricalcircuit.getJElement(y_info, x_info)));
                } else {
                    IeditView.setText(String.format("%.2e", electricalcircuit.getJElement(y_info, x_info)));
                }
            }
        }

        if (solved == 1) {
            if (electricalcircuit.giveTypeElement(y_info, x_info) == 'X') {
                Toast.makeText(this, R.string.info1, Toast.LENGTH_SHORT).show();

            } else if (electricalcircuit.giveTypeElement(y_info, x_info) == 'N') {
                Toast.makeText(this, R.string.info2, Toast.LENGTH_SHORT).show();
            } else {
                ReditView.setVisibility(View.VISIBLE);
                RtextView.setVisibility(View.VISIBLE);

                IeditView.setVisibility(View.VISIBLE);
                ItextView.setVisibility(View.VISIBLE);

                IIimagebutton.setVisibility(View.VISIBLE);

                UeditView.setVisibility(View.VISIBLE);
                UtextView.setVisibility(View.VISIBLE);


                RtextView.setText(" R =");
                UtextView.setText(" U =");
                ItextView.setText(" I =");

                ReditView.setEnabled(false);
                UeditView.setEnabled(false);
                IeditView.setEnabled(false);


                if ((electricalcircuit.getRElement(y_info, x_info) < 1) && (electricalcircuit.getRElement(y_info, x_info) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    ReditView.setText(decimalFormat.format(Math.abs(electricalcircuit.getRElement(y_info, x_info))));
                }else if ((electricalcircuit.getRElement(y_info, x_info) <= 1000) && (electricalcircuit.getRElement(y_info, x_info) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    ReditView.setText(decimalFormat.format(electricalcircuit.getRElement(y_info, x_info)));
                } else if (electricalcircuit.getRElement(y_info, x_info) == 0) {
                    ReditView.setText(String.format("%.0f", electricalcircuit.getRElement(y_info, x_info)));
                } else {
                    ReditView.setText(String.format("%.2e", electricalcircuit.getRElement(y_info, x_info)));
                }

                if ((Math.abs(electricalcircuit.getUElement(y_info, x_info)) < 1) && (Math.abs(electricalcircuit.getUElement(y_info, x_info)) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    UeditView.setText(decimalFormat.format(Math.abs(electricalcircuit.getUElement(y_info, x_info))));
                } else if ((Math.abs(electricalcircuit.getUElement(y_info, x_info)) <= 1000) && Math.abs(electricalcircuit.getUElement(y_info, x_info)) >= 1) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    UeditView.setText(decimalFormat.format(Math.abs(electricalcircuit.getUElement(y_info, x_info))));
                }else if (Math.abs(electricalcircuit.getUElement(y_info, x_info)) == 0) {
                    UeditView.setText(String.format("%.0f", Math.abs(electricalcircuit.getUElement(y_info, x_info))));
                } else {
                    UeditView.setText(String.format("%.2e", Math.abs(electricalcircuit.getUElement(y_info, x_info))));
                }

                if ((Math.abs(electricalcircuit.getIElement(y_info, x_info)) < 1) && (Math.abs(electricalcircuit.getIElement(y_info, x_info)) >= 0.001)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    IeditView.setText(decimalFormat.format(Math.abs(electricalcircuit.getIElement(y_info, x_info))));
                }else if ((Math.abs(electricalcircuit.getIElement(y_info, x_info)) <= 1000) && (Math.abs(electricalcircuit.getIElement(y_info, x_info)) >= 1)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    IeditView.setText(decimalFormat.format(Math.abs(electricalcircuit.getIElement(y_info, x_info))));
                } else if (Math.abs(electricalcircuit.getIElement(y_info, x_info)) == 0) {
                    IeditView.setText(String.format("%.0f", Math.abs(electricalcircuit.getIElement(y_info, x_info))));
                } else {
                    IeditView.setText(String.format("%.2e", Math.abs(electricalcircuit.getIElement(y_info, x_info))));
                }


                if (electricalcircuit.getIElement(y_info, x_info) != 0) {
                    if (((electricalcircuit.isUpElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) > 0)) || ((electricalcircuit.isDownElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) < 0))) {
                        IIimagebutton.setImageResource(R.drawable.i_up);
                    } else if (((electricalcircuit.isDownElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) > 0)) || ((electricalcircuit.isUpElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) < 0))) {
                        IIimagebutton.setImageResource(R.drawable.i_down);
                    } else if (((electricalcircuit.isLeftElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) > 0)) || ((electricalcircuit.isRightElement(y_info, x_info)) && (electricalcircuit.getIElement(y_info, x_info) < 0))) {
                        IIimagebutton.setImageResource(R.drawable.i_left);
                    } else {
                        IIimagebutton.setImageResource(R.drawable.i_right);
                    }
                }

            }

        }


        //    ItextView.setText(String.valueOf(electricalcircuit.isUpElement(y_info, x_info)));
    }

    public void breakInfo() {
        PaintElementOK(y_info, x_info);
        EditText ReditView = findViewById(R.id.RRR);
        TextView RtextView = findViewById(R.id.R);
        ImageButton Rimagebutton = findViewById(R.id.RR);
        EditText UeditView = findViewById(R.id.UUU);
        TextView UtextView = findViewById(R.id.U);
        ImageButton Uimagebutton = findViewById(R.id.UU);
        EditText IeditView = findViewById(R.id.III);
        TextView ItextView = findViewById(R.id.I);
        ImageButton Iimagebutton = findViewById(R.id.II);
        ImageButton IIimagebutton = findViewById(R.id.IIII);

        ReditView.setVisibility(View.INVISIBLE);
        RtextView.setVisibility(View.INVISIBLE);
        Rimagebutton.setVisibility(View.INVISIBLE);
        UeditView.setVisibility(View.INVISIBLE);
        UtextView.setVisibility(View.INVISIBLE);
        Uimagebutton.setVisibility(View.INVISIBLE);
        IeditView.setVisibility(View.INVISIBLE);
        ItextView.setVisibility(View.INVISIBLE);
        Iimagebutton.setVisibility(View.INVISIBLE);
        Iimagebutton.setVisibility(View.INVISIBLE);
        IIimagebutton.setVisibility(View.INVISIBLE);
    }

    public void setR(View view) {
        try {
            EditText RtextView = findViewById(R.id.RRR);
            electricalcircuit.setRElement(Double.parseDouble(RtextView.getText().toString()), y_info, x_info);
        } catch (Exception e) {
            Toast.makeText(this, R.string.err6, Toast.LENGTH_LONG).show();
        }
    }

    public void setU(View view) {
        try {
            EditText UtextView = findViewById(R.id.UUU);
            electricalcircuit.setEElement(Double.parseDouble(UtextView.getText().toString()), y_info, x_info);
        } catch (Exception e) {
            Toast.makeText(this, R.string.err6, Toast.LENGTH_LONG).show();
        }
    }

    public void setI(View view) {
        try {
            EditText ItextView = findViewById(R.id.III);
            electricalcircuit.setJElement(Double.parseDouble(ItextView.getText().toString()), y_info, x_info);
        } catch (Exception e) {
            Toast.makeText(this, R.string.err6, Toast.LENGTH_LONG).show();
        }
    }

    public void Solve_first(View view) {
        PaintButtonOK();
        if (solved == 0) {
            try {
                electricalcircuit.solve();
                solved = 1;
                breakInfo();
                ImageButton solve = (ImageButton) findViewById(R.id.resolve);
                TextView solvek = (TextView) findViewById(R.id.resolvetext);
                solve.setImageResource(R.drawable.resolve_first);
                solvek.setText("Перестроить");
                Toast.makeText(this, "Схема успешно расчитана", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.err, Toast.LENGTH_LONG).show();
            }

        } else {
            solved = 0;
            breakInfo();
            ImageButton solve = (ImageButton) findViewById(R.id.resolve);
            TextView solvek = (TextView) findViewById(R.id.resolvetext);
            solve.setImageResource(R.drawable.solve_first);
            solvek.setText("Рассчитать");
        }
    }

    public void Help(View view) {
        Log.i("TAG", "1");
        Save();

    }

    public void newHelp(View v) {
        PaintButtonOK();
        Intent intent = new Intent(WorkActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    public void Info(View view) {
        Intent saves = new Intent(WorkActivity.this, SavesActivity.class);
        startActivity(saves);
        //finish();
    }


    public void Dell(View view) {
        PaintButtonOK();
        if (solved == 0) {
            if (stage_new_element != 3) {
                stage_new_element = 3;
                ImageButton dell = (ImageButton) findViewById(R.id.ADell);
                dell.setImageResource(R.drawable.dell_choosen);
            } else {
                stage_new_element = 0;
                ImageButton dell = (ImageButton) findViewById(R.id.ADell);
                dell.setImageResource(R.drawable.dell);
            }
        } else {
            Toast.makeText(this, R.string.err5, Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(WorkActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void New(View view) {
        PaintButtonOK();
        Intent intent = new Intent(WorkActivity.this, WorkActivity.class);
        startActivity(intent);
        //recreate();
       /* Log.i("TAG", "1");
        breakInfo();
        electricalcircuit = null;
        electricalcircuit = new ElectricalCircuit(HEIGHT, WIDTH);
        number_new_element = 0;
        TypeNewElement = 'N';
        x_info = 0;
        stage_new_element = 1;
        x_new_element = 0;
        y_new_element = 0;
        x_info = 0;
        y_info = 0;
        solved = 0;
        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setImageResource(R.drawable.white);
            }*/
    }

    public void Save() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM_dd_yyyy_hh_mm_ss");
        String dateFormat = simpleDateFormat.format(calendar.getTime());
        if (dateFormat.equals(last_save)) {
            last_save = dateFormat;
            Toast.makeText(this, R.string.err4, Toast.LENGTH_SHORT).show();

        } else {
            last_save = dateFormat;

            DBConnector.AddCircuit(dateFormat, HEIGHT, WIDTH, solved, dateFormat, 0);

            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    int TYPE = 0;
                    switch (electricalcircuit.giveTypeElement(i, j)) {
                        case ('N'):
                            TYPE = 0;
                            break;
                        case ('X'):
                            TYPE = 1;
                            break;
                        case ('R'):
                            TYPE = 2;
                            break;
                        case ('E'):
                            TYPE = 3;
                            break;
                        case ('J'):
                            TYPE = 4;
                            break;
                    }

                    int UP = electricalcircuit.isUpElement(i, j) ? 1 : 0;
                    int DOWN = electricalcircuit.isDownElement(i, j) ? 1 : 0;
                    int LEFT = electricalcircuit.isLeftElement(i, j) ? 1 : 0;
                    int RIGHT = electricalcircuit.isRightElement(i, j) ? 1 : 0;

                    DBConnector.AddElement(dateFormat, TYPE, electricalcircuit.getIElement(i, j), electricalcircuit.getRElement(i, j), electricalcircuit.getUElement(i, j), electricalcircuit.getJElement(i, j), electricalcircuit.getEElement(i, j), electricalcircuit.getPictureElement(i, j), UP, DOWN, RIGHT, LEFT);
                }
            }

        }


    }
}