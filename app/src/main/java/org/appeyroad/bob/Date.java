package org.appeyroad.bob;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Date extends GregorianCalendar implements Comparable<Calendar>, Parcelable {

    private Date() {}

    private Date(int year, int month, int day) {
        super(year, month, day);
    }

    public Date(Date date) {
        this(date.get(YEAR), date.get(MONTH), date.get(DATE));
    }

    public Date(String source) {
        super();
        source = source.replaceAll("[^0-9]", "");
        try {
            if (source.length() != 8) {
                throw new Exception("CannotParseStringException: the string was " + source);
            }
            int year = Integer.parseInt(source.substring(0, 4));
            int month = Integer.parseInt(source.substring(4, 6)) - 1;
            int day = Integer.parseInt(source.substring(6, 8));
            set(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Date createFromParcel(Parcel source) {
            int year = source.readInt();
            int month = source.readInt();
            int day = source.readInt();
            return new Date(year, month, day);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };

    @Override
     public String toString() {
        String month = String.format("%02d", get(MONTH) + 1);
        String monthDay = String.format("%02d", get(DAY_OF_MONTH));

        String weekDay = "요일";
        switch (get(DAY_OF_WEEK)) {
            case MONDAY:
                weekDay = "월" + weekDay;
                break;
            case TUESDAY:
                weekDay = "화" + weekDay;
                break;
            case WEDNESDAY:
                weekDay = "수" + weekDay;
                break;
            case THURSDAY:
                weekDay = "목" + weekDay;
                break;
            case FRIDAY:
                weekDay = "금" + weekDay;
                break;
            case SATURDAY:
                weekDay = "토" + weekDay;
                break;
            case SUNDAY:
                weekDay = "일" + weekDay;
                break;
        }

        return get(YEAR) + "-" + month + "-" + monthDay + "　" + weekDay;
    }

    public static Date today() {
        return new Date();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Date) && (hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return get(YEAR) * 365 + get(DAY_OF_YEAR);
    }

    @Override
    public int compareTo(@NonNull Calendar other) {
        try {
            if (!(other instanceof GregorianCalendar)) {
                throw new Exception("CannotCompareException: another object was a " + other.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashCode() - other.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeInt(get(YEAR));
        destination.writeInt(get(MONTH));
        destination.writeInt(get(DAY_OF_MONTH));
    }
}