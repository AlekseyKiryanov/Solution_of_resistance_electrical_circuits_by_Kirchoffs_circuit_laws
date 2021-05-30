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
    private static final String TABLE_NAME2 = "save";

    private final SQLiteDatabase mDataBase;

    public DBCircuit(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }


    public void AddCircuit(String name, int height, int width, int solved, int liked) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("height", height);
        cv.put("width", width);
        cv.put("solved", solved);
        cv.put("liked", liked);
        cv.put("start", numberLastElement());


        mDataBase.insert(TABLE_NAME, null, cv);
    }

    public void DellCircuit(String name, int start, int k) {

        mDataBase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE name = \'" + name + "\';");
        mDataBase.execSQL("DELETE FROM " + TABLE_NAME2 + " WHERE _id BETWEEN " + String.valueOf(start + 1) + " AND " + String.valueOf(start + k) + ";");
        //   for (int i = (start + 1); i < (start + k + 1); i++) {
        //      mDataBase.execSQL("DELETE FROM " + TABLE_NAME2 + " WHERE _id = " + String.valueOf(i));
        //                         DELETE from tablename WHERE id BETWEEN 1 AND 254;
        //   }
    }

    public void AddElement(int type, double i, double r, double u, double j, double e, int picture, int up, int down, int right, int left) {

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

        mDataBase.insert(TABLE_NAME2, null, cv);
    }


    private int findIndexById(int id) throws Exception {
        Cursor mCursor = mDataBase.query(TABLE_NAME2, null, null, null, null, null, null);
        int max = mCursor.getCount() - 1;
        int c = (int) Math.sqrt(max);
        int last = 0;
        int next = c;
        int ans = -1;
        if (next <= max) {
            mCursor.moveToPosition(next);
        }

        while ((next < max) && (mCursor.getInt(0) < id)) {
            last = next;
            next += c;
            if (next <= max) {
                mCursor.moveToPosition(next);
            }
        }

        for (int i = last; i <= Math.min(next, max); i++) {
            mCursor.moveToPosition(i);
            if (mCursor.getInt(0) == id) {
                ans = i;
            }
        }

        if (ans == -1) {
            throw new Exception();
        }
        return ans;
    }

    public ArrayList<ElectricElement> getElements(int start, int k) throws Exception {
        Cursor mCursor = mDataBase.query(TABLE_NAME2, null, null, null, null, null, null);

        ArrayList<ElectricElement> arr = new ArrayList<ElectricElement>();
        start = findIndexById(start + 1);

        for (int i = start; i < (start + k); i++) {
            mCursor.moveToPosition(i);
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
            double I = mCursor.getDouble(2);
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

            arr.add(new ElectricElement(TYPE, I, r, u, j, e, picture, UP, DOWN, RIGHT, LEFT));
        }
        mCursor.close();
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
                int liked = mCursor.getInt(5);
                int start = mCursor.getInt(6);
                Log.i(TAG, "4");
                arr.add(0, new Save(name, height, width, solved, liked, start));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return arr;
    }

    public int numberLastElement() {
        Cursor mCursor = mDataBase.query(TABLE_NAME2, null, null, null, null, null, null);

        int k = 0;
        if (mCursor.getCount() > 0) {
            mCursor.moveToLast();
            k = mCursor.getInt(0);

        }
        Log.i("TAG", "Number last element =" + String.valueOf(k));
        return k;

    }


    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE saves(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, height INTEGER, width INTEGER, solved INTEGER, liked INTEGER, start INTEGER);";
            db.execSQL(query);
            query = "CREATE TABLE save(_id INTEGER PRIMARY KEY AUTOINCREMENT, type FLOAT, i FLOAT, r FLOAT, u FLOAT, j FLOAT, e FLOAT, picture INTEGER, up INTEGER, " +
                    "down INTEGER, righ INTEGER, lef INTEGER);";
            db.execSQL(query);

        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
