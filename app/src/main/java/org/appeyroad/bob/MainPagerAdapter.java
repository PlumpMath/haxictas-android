package org.appeyroad.bob;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainPagerAdapter extends FragmentPagerAdapter {

    public static final int BOOKMARKS = 0;
    public static final int MAIN = 1;
    public static final int ETC = 2;
    public static final int MAX_PAGE = 3;

    private Activity activity;
    private List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;

        fragments = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            Fragment fragment = new Fragment();
            switch (i) {
                case BOOKMARKS:
                    fragment = CafeteriasFragment.newInstance(CafeteriasAdapter.BOOKMARK);
                    break;
                case MAIN:
                    fragment = CafeteriasFragment.newInstance(CafeteriasAdapter.ALL);
                    break;
                case ETC:
                    fragment = EtcFragment.newInstance();
                    break;
            }
            fragments.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return MAX_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int page) {
        String title = null;
        switch (page) {
            case BOOKMARKS:
                title = activity.getString(R.string.bookmarks);
                break;
            case MAIN:
                title = activity.getString(R.string.main);
                break;
            case ETC:
                title = activity.getString(R.string.etc);
                break;
        }
        return title;
    }
}