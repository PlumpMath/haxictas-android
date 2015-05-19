package org.appeyroad.bob;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class Cafeteria implements Parcelable {

    private static final int NAME = 0;
    private static final int COORDINATE = 1;
    private static final int BREAKFAST_TIME = 2;
    private static final int LUNCH_TIME = 3;
    private static final int DINNER_TIME = 4;
    private static final int FIELDS_NUMBER = 5;

    private static HashMap<String, Integer> hashMap;

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

    {
        hashMap = new HashMap<>();
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

        if (source.contains("학생회관") || source.contains("63")) {
            dataSet = context.getResources().getStringArray(R.array.학식);
        } else if (source.contains("3식당") || source.contains("전망대")) {
            dataSet = context.getResources().getStringArray(R.array.농식);
        } else if (source.contains("아워홈")) {
            dataSet = context.getResources().getStringArray(R.array.아워홈);
        } else if (source.contains("기숙사") || source.contains("919")) {
            dataSet = context.getResources().getStringArray(R.array.긱식);
        } else if (source.contains("자하연") || source.contains("농협") || source.contains("109")) {
            dataSet = context.getResources().getStringArray(R.array.자식);
        } else if (source.contains("제2공학관") || source.contains("302")) {
            dataSet = context.getResources().getStringArray(R.array.삼백이);
        } else if (source.contains("솔밭") || source.contains("110")) {
            dataSet = context.getResources().getStringArray(R.array.솔밭);
        } else if (source.contains("동원관")) {
            dataSet = context.getResources().getStringArray(R.array.동원관);
        } else if (source.contains("감골") || source.contains("101")) {
            dataSet = context.getResources().getStringArray(R.array.감골);
        } else if (source.contains("서당골") || source.contains("4식당") || source.contains("76")) {
            dataSet = context.getResources().getStringArray(R.array.사식);
        } else if (source.contains("두레미담")) {
            dataSet = context.getResources().getStringArray(R.array.두레);
        } else if (source.contains("제1공학관") || source.contains("301")) {
            dataSet = context.getResources().getStringArray(R.array.삼백일);
        } else if (source.contains("교수회관") || source.contains("65")) {
            dataSet = context.getResources().getStringArray(R.array.교수회관);
        } else if (source.contains("비비고") || source.contains("501")) {
            dataSet = context.getResources().getStringArray(R.array.비비고);
        } else if (source.contains("공대간이") || source.contains("30-2")) {
            dataSet = context.getResources().getStringArray(R.array.공깡);
        } else if (source.contains("소담마루")) {
            dataSet = context.getResources().getStringArray(R.array.소담마루);
        } else if (source.contains("연구동") || source.contains("220")) {
            dataSet = context.getResources().getStringArray(R.array.이백이십);
        } else if (source.contains("라운지오")) {
            dataSet = context.getResources().getStringArray(R.array.라운지오);
        } else if (source.contains("예술계")) {
            dataSet = context.getResources().getStringArray(R.array.예술계);
        } else {
            Log.w("C", "unknown: " + source);
            dataSet = context.getResources().getStringArray(R.array.불명);
            if (source.contains(dataSet[NAME])) {
                dataSet[NAME] = source;
            } else {
                if (!hashMap.containsKey(source))
                    hashMap.put(source, hashMap.size() + 1);
                dataSet[NAME] += hashMap.get(source);
            }
        }

        if (dataSet != null && dataSet.length == FIELDS_NUMBER) {
            name = dataSet[NAME];
            coordinate = dataSet[COORDINATE];
            breakfastTime = dataSet[BREAKFAST_TIME];
            lunchTime = dataSet[LUNCH_TIME];
            dinnerTime = dataSet[DINNER_TIME];
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
