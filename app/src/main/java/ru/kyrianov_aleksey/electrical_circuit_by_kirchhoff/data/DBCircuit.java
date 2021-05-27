package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.ElectricElement;
import ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff.Save;

public class DBCircuit {
    private static final String TAG = "MyApp";

    private static final String DATABASE_NAME = "ElectricalCircuit.db";
    private static final String TABLE_NAME = "saves";

    private final SQLiteDatabase mDataBase;

    public DBCircuit(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    //public ElectricElement(char type, double i, double r, double u, double j, double e, int picture, boolean up, boolean down, boolean right, boolean left)

    public void AddCircuit(String name, int height, int width, int solved, String visibly_name, int liked) {


        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("height", height);
        cv.put("width", width);
        cv.put("solved", solved);
        cv.put("visiblyname", visibly_name);
        cv.put("liked", liked);

        String query = "CREATE TABLE " + name + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, type FLOAT, i FLOAT, r FLOAT, u FLOAT, j FLOAT, e FLOAT, picture INTEGER, up INTEGER, " +
                "down INTEGER, righ INTEGER, lef INTEGER);";
        mDataBase.execSQL(query);
        mDataBase.insert(TABLE_NAME, null, cv);



    }

    public void DellCircuit(String name){
        mDataBase.execSQL("DROP TABLE IF EXISTS " + name);
        mDataBase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE name = \'"+name+"\'");
    }

    public void AddElement(String name_table, int type, double i, double r, double u, double j, double e, int picture, int up, int down, int right, int left) {

        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("i", i);
        cv.put("r", r);
        cv.put("u", u);
        cv.put("j", j);
        cv.put("e", e);
        cv.put("picture", picture);
        cv.put("up", up);
        cv.put("down", down);
        cv.put("righ", right);
        cv.put("lef", left);

        mDataBase.insert(name_table, null, cv);
    }

    public ArrayList<ElectricElement> getElements(String table_name) {
        Cursor mCursor = mDataBase.query(table_name, null, null, null, null, null, null);
        Log.i(TAG, "213");
        ArrayList<ElectricElement> arr = new ArrayList<ElectricElement>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                int type = mCursor.getInt(1);
                char TYPE = 0;
                switch (type) {
                    case (0):
                        TYPE = 'N';
                        break;
                    case (1):
                        TYPE = 'X';
                        break;
                    case (2):
                        TYPE = 'R';
                        break;
                    case (3):
                        TYPE = 'E';
                        break;
                    case (4):
                        TYPE = 'J';
                        break;
                }
                double i = mCursor.getDouble(2);
                double r = mCursor.getDouble(3);
                double u = mCursor.getDouble(4);
                double j = mCursor.getDouble(5);
                double e = mCursor.getDouble(6);
                int picture = mCursor.getInt(7);
                int up = mCursor.getInt(8);
                int down = mCursor.getInt(9);
                int right = mCursor.getInt(10);
                int left = mCursor.getInt(11);
                boolean UP = up == 1;
                boolean DOWN = down == 1;
                boolean LEFT = left == 1;
                boolean RIGHT = right == 1;

                Log.i(TAG, "21313232");
                arr.add(new ElectricElement(TYPE, i, r, u, j, e, picture, UP, DOWN, RIGHT, LEFT));
            } while (mCursor.moveToNext());
        }
        return arr;
    }


    public ArrayList<Save> getSaves() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Save> arr = new ArrayList<Save>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                String name = mCursor.getString(1);
                int height = mCursor.getInt(2);
                int width = mCursor.getInt(3);
                int solved = mCursor.getInt(4);
                String visiblyname = mCursor.getString(5);
                int liked = mCursor.getInt(6);
                Log.i(TAG, "4");
                arr.add(0, new Save(name, height, width, solved, visiblyname, liked));
            } while (mCursor.moveToNext());
        }
        return arr;
    }


    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE saves(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, height INTEGER, width INTEGER, solved INTEGER, visiblyname TEXT, liked INTEGER);";
            db.execSQL(query);
        }

        //public Save(String name, int height, int width, int solved, String visibly_name, int liked)

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
