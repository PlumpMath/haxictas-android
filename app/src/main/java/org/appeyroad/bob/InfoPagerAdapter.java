package org.appeyroad.bob;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.Collections;

public class InfoPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Cafeteria cafeteria;
    private ArrayList<DailyMenu> dailyMenus;
    private int backgroundColor;
    private int todayPage;

    InfoPagerAdapter(Context context, Cafeteria cafeteria, int backgroundColor) {
        super(((ActionBarActivity)context).getSupportFragmentManager());
        this.context = context;
        this.cafeteria = cafeteria;
        this.backgroundColor = backgroundColor;
        cafeteria.parse(cafeteria.getName(), context);
        dailyMenus = new ArrayList<>();
        reloadData();
        for (int i = 0; i < dailyMenus.size(); i++) {
            if (Date.today().equals(dailyMenus.get(i).getDate())) {
                todayPage = i;
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return InfoPagerFragment.create(cafeteria, dailyMenus.get(position), backgroundColor);
        } catch (IndexOutOfBoundsException e) {
            return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return dailyMenus.size();
    }

    public void reloadData() {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        dailyMenus.clear();
        dailyMenus.addAll(helper.getMenus(cafeteria));
        Collections.sort(dailyMenus);
        notifyDataSetChanged();
    }

    public int getTodayPage() {
        return todayPage;
    }
}