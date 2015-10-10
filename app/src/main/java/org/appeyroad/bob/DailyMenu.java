package org.appeyroad.bob;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

public class DailyMenu implements Comparable<DailyMenu>, Parcelable {

    public static final int BREAKFAST = 0;
    public static final int LUNCH = 1;
    public static final int DINNER = 2;

    public static final String SEPARATOR = "|";
    private static final String DUMMY = "-";

    private String cafeteriaName;
    private Date date;
    private String[] contents = new String[3];

    public DailyMenu() {}

    private DailyMenu(Parcel source) {
        cafeteriaName = source.readString();
        date = source.readParcelable(Date.class.getClassLoader());
        contents = source.createStringArray();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public DailyMenu createFromParcel(Parcel source) {
            return new DailyMenu(source);
        }

        @Override
        public DailyMenu[] newArray(int size) {
            return new DailyMenu[size];
        }
    };

    public String get(int dayTime) {
        try {
            return contents[dayTime];
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return e.getClass().getName();
        }
    }

    public void setContents(String rawString, String unknown) {
        String[] contents = rawString
                .replace(SEPARATOR, DUMMY + SEPARATOR + DUMMY)
                .split("\\" + SEPARATOR);
        setContents(contents, unknown);
    }

    public void setContents(String[] contents, String unknown) {
        try {
            if (contents.length != 3) {
                throw new Exception("InvalidMenuArgumentException: " + contents.length);
            }
            for (int i = 0; i < contents.length; i++) {
                String item = format(contents[i], unknown);
                this.contents[i] = item;
            }
        } catch (Exception e) {
            Log.e("Menu", e.toString(), e);
        }
    }

    public void setContent(int order, String content, String unknown) {
        if (contents[order] == null) {
            contents[order] = format(content, unknown);
        } else if (!format(content, unknown).equals(unknown)){
            contents[order] += " " + format(content, unknown);
        }
    }

    private String format(String string, String unknown) {
        if (string == null) return unknown;
        string = string
                .replaceAll(" ([0-9]+) ", " <$1> ")
                .replace("ⓐ", "<1700>")
                .replace("ⓑ", "<2000>")
                .replace("ⓒ", "<2500>")
                .replace("ⓓ", "<3000>")
                .replace("ⓔ", "<3500>")
                .replace("ⓕ", "<4000>")
                .replace("ⓖ", "<4500>")
                .replace("ⓗ", "<기타>")
                .replaceAll("[,/()&]", " ")
                .replaceAll("( |>) +", "$1")
                .replaceAll("[^가-힣0-9<> ]", "")
                .replaceAll("  ", " ")
                .trim();
        return string.equals("") ? unknown : string;
    }

    public String getContents() {
        return contents[BREAKFAST] + SEPARATOR + contents[LUNCH] + SEPARATOR + contents[DINNER];
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public void setCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }

    public String toString() {return null;}

    @Override
    public int compareTo(@NonNull DailyMenu another) {
        return date.compareTo(another.date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(cafeteriaName);
        destination.writeParcelable(date, 1);
        destination.writeStringArray(contents);
    }
}
