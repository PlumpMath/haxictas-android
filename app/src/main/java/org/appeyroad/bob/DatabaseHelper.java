package org.appeyroad.bob;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CAFETERIA = "cafeteria";
    public static final String BOOKMARK = "bookmark";
    public static final String MENU = "menu";

    private static final String CAFETERIA_COLUMNS
            = "(id INTEGER PRIMARY KEY, code TEXT, coordinate TEXT, " +
            "breakfastTime TEXT, lunchTime TEXT, dinnerTime TEXT)";
    private static final String MENU_COLUMNS
            = "(id INTEGER PRIMARY KEY, cafeteriaCode TEXT, date TEXT, contents TEXT)";

    private static final String DATABASE = "Haxictas";
    private static final int VERSION = 2;

    private static DatabaseHelper instance;
    private static Context latestContext;

    public static DatabaseHelper getInstance(Context context) {
        latestContext = context;
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context latestContext) {
        super(latestContext, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", CAFETERIA, CAFETERIA_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", BOOKMARK, CAFETERIA_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", MENU, MENU_COLUMNS));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CAFETERIA + ";");
        db.execSQL("DROP TABLE IF EXISTS " + BOOKMARK + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MENU + ";");
        onCreate(db);
    }

    public void insert(Cafeteria cafeteria) {
        insert(cafeteria, CAFETERIA);
    }

    public void delete(Cafeteria cafeteria){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format(
                "DELETE FROM %s WHERE code = '%s';",
                BOOKMARK, cafeteria.getCode()
        ));
        db.execSQL(String.format(
                "DELETE FROM %s WHERE code = '%s';",
                CAFETERIA, cafeteria.getCode()
        ));
        db.execSQL(String.format(
                "DELETE FROM %s WHERE cafeteriaCode = '%s';",
                MENU, cafeteria.getCode()
        ));
    }

    public void toggleBookmark(Cafeteria cafeteria) {
        if (getBookmarkedCafeterias().contains(cafeteria)) {
            getWritableDatabase().execSQL(String.format(
                    "DELETE FROM %s WHERE code = '%s';",
                    BOOKMARK, cafeteria.getCode()
            ));
        } else {
            insert(cafeteria, BOOKMARK);
        }

        if (latestContext instanceof Activity) {
            ((Activity) latestContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CafeteriasFragment.reloadAllData();
                }
            });
        }
    }


    private void insert(Cafeteria cafeteria, String tableName) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE code = '%s';",
                    tableName, cafeteria.getCode()
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                db.execSQL(String.format(
                        "UPDATE %s SET breakfastTime = '%s', lunchTime = '%s', " +
                                "dinnerTime = '%s', coordinate = '%s' WHERE code = '%s'",
                        tableName, cafeteria.getBreakfastTime(), cafeteria.getLunchTime(),
                        cafeteria.getDinnerTime(), cafeteria.getCoordinate(), cafeteria.getCode()
                ));
            } else {
                db.execSQL(String.format(
                        "DELETE FROM %s WHERE code = '%s';",
                        tableName, cafeteria.getCode()
                ));
                ContentValues values = new ContentValues();
                values.put("code", cafeteria.getCode());
                values.put("breakfastTime", cafeteria.getBreakfastTime());
                values.put("lunchTime", cafeteria.getLunchTime());
                values.put("dinnerTime", cafeteria.getDinnerTime());
                values.put("coordinate", cafeteria.getCoordinate());
                db.insert(tableName, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public ArrayList<Cafeteria> getAllCafeterias() {
        return getCafeterias(CAFETERIA);
    }

    public ArrayList<Cafeteria> getBookmarkedCafeterias() {
        return getCafeterias(BOOKMARK);
    }

    private ArrayList<Cafeteria> getCafeterias(String tableName) {
        Cursor cursor = null;
        ArrayList<Cafeteria> list = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + tableName + ";", null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                do {
                    Cafeteria cafeteria = new Cafeteria();
                    cafeteria.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    cafeteria.setCoordinate(cursor.getString(cursor.getColumnIndex("coordinate")));
                    cafeteria.setBreakfastTime(cursor.getString(cursor.getColumnIndex("breakfastTime")));
                    cafeteria.setLunchTime(cursor.getString(cursor.getColumnIndex("lunchTime")));
                    cafeteria.setDinnerTime(cursor.getString(cursor.getColumnIndex("dinnerTime")));
                    cafeteria.identifyBy(cafeteria.getCode(), latestContext);
                    list.add(cafeteria);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public void insert(DailyMenu dailyMenu) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cafeteriaCode", dailyMenu.getCafeteriaCode());
        values.put("date", dailyMenu.getDate().toString());
        values.put("contents", dailyMenu.getContents());
        db.execSQL(String.format(
                "DELETE FROM %s WHERE cafeteriaCode = '%s' AND date = '%s';",
                MENU, dailyMenu.getCafeteriaCode(), dailyMenu.getDate().toString()
        ));
        db.insert(MENU, null, values);

        if (InfoActivity.latestAdapter != null && latestContext instanceof Activity) {
            ((Activity) latestContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InfoActivity.latestAdapter.reloadData();
                }
            });
        }
    }

    public void clearInvalidMenus() {
        final int edgeOffset = 0;
        final int range = PageParser.DAYS_RANGE - edgeOffset;

        ArrayList<String> validDateStrings = new ArrayList<>();
        for (int i = -range; i <= range; i++) {
            Date date = Date.today();
            date.add(Date.DAY_OF_MONTH, i);
            validDateStrings.add(date.toString());
        }

        Cursor cursor = null;
        SQLiteDatabase db = getWritableDatabase();
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT date FROM %s;",
                    MENU
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (!validDateStrings.contains(cursor.getString(cursor.getColumnIndex("date")))) {
                        db.execSQL(String.format(
                                "DELETE FROM %s WHERE date = '%s';",
                                MENU, cursor.getString(cursor.getColumnIndex("date"))
                        ));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public DailyMenu getMenu(Cafeteria cafeteria, Date date) {
        Cursor cursor = null;
        DailyMenu dailyMenu = null;
        try {
            cursor = getReadableDatabase().rawQuery(String.format(
                    "SELECT * FROM %s WHERE cafeteriaCode = '%s' AND date = '%s'",
                    MENU, cafeteria.getCode(), date.toString()
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                dailyMenu = new DailyMenu();
                dailyMenu.setCafeteriaCode(cursor.getString(cursor.getColumnIndex("cafeteriaCode")));
                dailyMenu.setDate(new Date(cursor.getString(cursor.getColumnIndex("date"))));
                dailyMenu.setContents(cursor.getString(cursor.getColumnIndex("contents")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dailyMenu;
    }

    public List<DailyMenu> getMenus(Cafeteria cafeteria) {
        Cursor cursor = null;
        ArrayList<DailyMenu> dailyMenus = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format(
                    "SELECT * FROM %s WHERE cafeteriaCode = '%s';",
                    MENU, cafeteria.getCode()
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DailyMenu dailyMenu = new DailyMenu();
                    dailyMenu.setCafeteriaCode(cursor.getString(cursor.getColumnIndex("cafeteriaCode")));
                    dailyMenu.setDate(new Date(cursor.getString(cursor.getColumnIndex("date"))));
                    dailyMenu.setContents(cursor.getString(cursor.getColumnIndex("contents")));
                    dailyMenus.add(dailyMenu);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dailyMenus;
    }

    public List<DailyMenu> getMenus(Date date) {
        Cursor cursor = null;
        ArrayList<DailyMenu> dailyMenus = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format(
                    "SELECT * FROM %s WHERE date = '%s';",
                    MENU, date.toString()
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                do {
                    DailyMenu dailyMenu = new DailyMenu();
                    dailyMenu.setCafeteriaCode(cursor.getString(cursor.getColumnIndex("cafeteriaCode")));
                    dailyMenu.setDate(new Date(cursor.getString(cursor.getColumnIndex("date"))));
                    dailyMenu.setContents(cursor.getString(cursor.getColumnIndex("contents")));
                    dailyMenus.add(dailyMenu);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dailyMenus;
    }

}