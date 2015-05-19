package org.appeyroad.bob;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public class Preferences {

    private static final String PREFS_NAME = "Prefs";

    public static final String BOOKMARK = "Bookmark";
    public static final String ALL = "All";

    private static final String ITEM_TYPE = "ItemType";
    private static final String VIEW_MODE = "ViewMode";

    public static void setItemType(Context context, int dataType, int itemType) {
        put(context, getKeyFromInteger(dataType) + ITEM_TYPE, itemType);
    }

    public static int getItemType(Context context, int dataType) {
        return get(context, getKeyFromInteger(dataType) + ITEM_TYPE,
                getKeyFromInteger(dataType).equals(ALL) ? CafeteriasAdapter.GRID : CafeteriasAdapter.LIST);
    }

    public static void setViewMode(Context context, int dataType, int viewMode) {
        put(context, getKeyFromInteger(dataType) + VIEW_MODE, viewMode);
    }

    public static int getViewMode(Context context, int dataType) {
        return get(context, getKeyFromInteger(dataType) + VIEW_MODE, CafeteriasAdapter.SIMPLE);
    }

    public static void saveAppVersion(Context context) {
        try {
            put(context, "VERSION", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {

        }
    }

    public static int getSavedAppVersion(Context context) {
        return get(context, "VERSION", -1);
    }

    private static void put(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static int get(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(key, defValue);
    }

    private static String getKeyFromInteger(int num) {
        return num == CafeteriasAdapter.ALL ? ALL : BOOKMARK;
    }
}
