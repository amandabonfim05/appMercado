package com.example.paginainicial;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "WalkLibrary.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "my_walk";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_START_DATA = "_data";
    public static final String COLUMN_AVERAGE_SPEED = "_AvgSpeed";
    public static final String COLUMN_KILOMETER = "_distance";
    public static final String COLUMN_TIME = "_duration";

    public BancoDeDados(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_START_DATA + " INTEGER, "
                + COLUMN_AVERAGE_SPEED + " REAL, "
                + COLUMN_KILOMETER + " REAL, "
                + COLUMN_TIME + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deletarTrilha(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }


    public List<Trilha> pegarTodasTrilhas() {
        List<Trilha> trilhas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int dataIndex = cursor.getColumnIndex(COLUMN_START_DATA);
        int velocidadeIndex = cursor.getColumnIndex(COLUMN_AVERAGE_SPEED);
        int kilometerIndex = cursor.getColumnIndex(COLUMN_KILOMETER);
        int tempoIndex = cursor.getColumnIndex(COLUMN_TIME);

        if (idIndex == -1 || dataIndex == -1 || velocidadeIndex == -1 || kilometerIndex == -1 || tempoIndex == -1) {
            throw new IllegalStateException("Database has not been created correctly. Missing columns.");
        }

        while (cursor.moveToNext()) {
            trilhas.add(new Trilha(
                    cursor.getInt(idIndex),
                    cursor.getLong(dataIndex),
                    cursor.getDouble(velocidadeIndex),
                    cursor.getDouble(kilometerIndex),
                    cursor.getLong(tempoIndex)
            ));
        }
        cursor.close();
        db.close();
        return trilhas;
    }
}
