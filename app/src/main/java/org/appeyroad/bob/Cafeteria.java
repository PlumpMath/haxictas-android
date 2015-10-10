package org.appeyroad.bob;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Cafeteria implements Parcelable {

    private static final int NAME = 0;
    private static final int COORDINATE = 1;
    private static final int BREAKFAST_TIME = 2;
    private static final int LUNCH_TIME = 3;
    private static final int DINNER_TIME = 4;
    private static final int FIELDS_NUMBER = 5;

    private String name;
    private String coordinate;
    private String breakfastTime;
    private String lunchTime;
    private String dinnerTime;

    public Cafeteria() {}

    private Cafeteria(Parcel source) {
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

    public void parse(String source, Context context) {
        String[] dataSet;
        String[] unknown = context.getResources().getStringArray(R.array.ERR);

        if (source.contains("학생회관")) {
            dataSet = context.getResources().getStringArray(R.array.A01);
        } else if (source.contains("3식당")) {
            dataSet = context.getResources().getStringArray(R.array.A02);
        } else if (source.contains("아워홈")) {
            dataSet = context.getResources().getStringArray(R.array.B09);
        } else if (source.contains("919")) {
            dataSet = context.getResources().getStringArray(R.array.A03);
        } else if (source.contains("자하연")) {
            dataSet = context.getResources().getStringArray(R.array.A04);
        } else if ( source.contains("302")) {
            dataSet = context.getResources().getStringArray(R.array.A05);
        } else if (source.contains("솔밭")) {
            dataSet = context.getResources().getStringArray(R.array.A06);
        } else if (source.contains("동원관")) {
            dataSet = context.getResources().getStringArray(R.array.A07);
        } else if (source.contains("감골")) {
            dataSet = context.getResources().getStringArray(R.array.A08);
        } else if (source.contains("4식당")) {
            dataSet = context.getResources().getStringArray(R.array.B01);
        } else if (source.contains("두레미담")) {
            dataSet = context.getResources().getStringArray(R.array.B02);
        } else if (source.contains("301")) {
            dataSet = context.getResources().getStringArray(R.array.B03);
        } else if (source.contains("샤반")) {
            dataSet = context.getResources().getStringArray(R.array.B04);
        } else if (source.contains("공대간이")) {
            dataSet = context.getResources().getStringArray(R.array.B05);
        } else if (source.contains("소담마루")) {
            dataSet = context.getResources().getStringArray(R.array.B06);
        } else if (source.contains("220")) {
            dataSet = context.getResources().getStringArray(R.array.B07);
        } else if (source.contains("라운지오")) {
            dataSet = context.getResources().getStringArray(R.array.B08);
        } else if (source.contains("예술계")) {
            dataSet = context.getResources().getStringArray(R.array.B10);
        } else {
            dataSet = unknown;
        }

        if (dataSet != null && dataSet.length == FIELDS_NUMBER) {
            name = dataSet[NAME];
            coordinate = dataSet[COORDINATE];
            breakfastTime = dataSet[BREAKFAST_TIME];
            lunchTime = dataSet[LUNCH_TIME];
            dinnerTime = dataSet[DINNER_TIME];
            if (dataSet == unknown) {
                name = source;
            }
        } else {
            try {
                throw new Exception("WrongParseException: " + source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Cafeteria && name.equals(((Cafeteria) another).getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(name);
        destination.writeString(coordinate);
        destination.writeString(breakfastTime);
        destination.writeString(lunchTime);
        destination.writeString(dinnerTime);
    }
}
