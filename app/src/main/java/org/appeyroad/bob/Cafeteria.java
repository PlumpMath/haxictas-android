package org.appeyroad.bob;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Cafeteria implements Parcelable {

    private static final int CODE = 0;
    private static final int NAME = 1;
    private static final int COORDINATE = 2;
    private static final int BREAKFAST_TIME = 3;
    private static final int LUNCH_TIME = 4;
    private static final int DINNER_TIME = 5;
    private static final int FIELDS_NUMBER = 6;

    private String code;
    private String name;
    private String coordinate;
    private String breakfastTime;
    private String lunchTime;
    private String dinnerTime;

    public Cafeteria() {}

    private Cafeteria(Parcel source) {
        code = source.readString();
        name = source.readString();
        coordinate = source.readString();
        breakfastTime = source.readString();
        lunchTime = source.readString();
        dinnerTime = source.readString();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Cafeteria createFromParcel(Parcel source) {
            return new Cafeteria(source);
        }

        @Override
        public Cafeteria[] newArray(int size) {
            return new Cafeteria[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getBreakfastTime() {
        return breakfastTime;
    }

    public void setBreakfastTime(String breakfastTime) {
        this.breakfastTime = breakfastTime;
    }

    public String getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }

    public String getDinnerTime() {
        return dinnerTime;
    }

    public void setDinnerTime(String dinnerTime) {
        this.dinnerTime = dinnerTime;
    }

    public void identifyBy(String source, Context context) {
        String[] dataSet;
        String[] unknown = context.getResources().getStringArray(R.array.UNK);

        if (source.contains("학생회관") || source.equals("A01")) {
            dataSet = context.getResources().getStringArray(R.array.A01);
        } else if (source.contains("3식당") || source.equals("A02")) {
            dataSet = context.getResources().getStringArray(R.array.A02);
        } else if (source.contains("아워홈") || source.equals("B09")) {
            dataSet = context.getResources().getStringArray(R.array.B09);
        } else if (source.contains("919") || source.equals("A03")) {
            dataSet = context.getResources().getStringArray(R.array.A03);
        } else if (source.contains("자하연") || source.equals("A04")) {
            dataSet = context.getResources().getStringArray(R.array.A04);
        } else if ( source.contains("302") || source.equals("A05")) {
            dataSet = context.getResources().getStringArray(R.array.A05);
        } else if (source.contains("솔밭") || source.equals("A06")) {
            dataSet = context.getResources().getStringArray(R.array.A06);
        } else if (source.contains("동원관") || source.equals("A07")) {
            dataSet = context.getResources().getStringArray(R.array.A07);
        } else if (source.contains("감골") || source.equals("A08")) {
            dataSet = context.getResources().getStringArray(R.array.A08);
        } else if (source.contains("4식당") || source.equals("B01")) {
            dataSet = context.getResources().getStringArray(R.array.B01);
        } else if (source.contains("두레미담") || source.equals("B02")) {
            dataSet = context.getResources().getStringArray(R.array.B02);
        } else if (source.contains("301") || source.equals("B03")) {
            dataSet = context.getResources().getStringArray(R.array.B03);
        } else if (source.contains("샤반") || source.equals("B04")) {
            dataSet = context.getResources().getStringArray(R.array.B04);
        } else if (source.contains("공대간이") || source.equals("B05")) {
            dataSet = context.getResources().getStringArray(R.array.B05);
        } else if (source.contains("소담마루") || source.equals("B06")) {
            dataSet = context.getResources().getStringArray(R.array.B06);
        } else if (source.contains("220") || source.equals("B07")) {
            dataSet = context.getResources().getStringArray(R.array.B07);
        } else if (source.contains("라운지오") || source.equals("B08")) {
            dataSet = context.getResources().getStringArray(R.array.B08);
        } else if (source.contains("예술계") || source.equals("B10")) {
            dataSet = context.getResources().getStringArray(R.array.B10);
        } else {
            dataSet = unknown;
        }

        if (dataSet != null && dataSet.length == FIELDS_NUMBER) {
            code = dataSet[CODE];
            name = dataSet[NAME];
            coordinate = dataSet[COORDINATE];
            breakfastTime = dataSet[BREAKFAST_TIME];
            lunchTime = dataSet[LUNCH_TIME];
            dinnerTime = dataSet[DINNER_TIME];
            if (dataSet == unknown) {
                name = source;
                code += name.length();
            }
        } else {
            throw new RuntimeException("WrongParseException: " + source);
        }
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Cafeteria && code.equals(((Cafeteria) another).getCode());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(code);
        destination.writeString(name);
        destination.writeString(coordinate);
        destination.writeString(breakfastTime);
        destination.writeString(lunchTime);
        destination.writeString(dinnerTime);
    }
}
