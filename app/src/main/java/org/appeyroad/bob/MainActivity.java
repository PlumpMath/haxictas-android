package org.appeyroad.bob;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), this));
        viewPager.setOffscreenPageLimit(MainPagerAdapter.MAX_PAGE);

        boolean bookmarksExist =
                DatabaseHelper.getInstance(this).getBookmarkedCafeterias().size() != 0;
        viewPager.setCurrentItem((bookmarksExist ?
                MainPagerAdapter.BOOKMARKS : MainPagerAdapter.MAIN), false);

        final SlideTabBar slideTabBar = (SlideTabBar)findViewById(R.id.tab_bar);
        slideTabBar.setViewPager(viewPager);

        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = -2;
        }
        if (Preferences.getSavedAppVersion(this) != versionCode) {
            viewPager.setCurrentItem(MainPagerAdapter.ETC);
        }
        Preferences.saveAppVersion(this);
    }
}