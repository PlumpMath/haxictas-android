package org.appeyroad.bob;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class InfoActivity extends ActionBarActivity {

    public static InfoPagerAdapter latestAdapter;

    private static final int TODAY_INDEX = PageParser.DAYS_RANGE;

    private int backgroundColor;
    private ViewPager viewPager;
    private Cafeteria cafeteria;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        helper = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        cafeteria = (Cafeteria)intent.getParcelableArrayExtra("CAFETERIAS")[0];
        backgroundColor = intent.getIntExtra("COLOR",
                getResources().getColor(R.color.primary_light));

        getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));

        viewPager = (ViewPager)findViewById(R.id.info_pager);
        InfoPagerAdapter adapter = new InfoPagerAdapter(this, cafeteria, backgroundColor);
        latestAdapter = adapter;
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.getTodayPage());

        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(cafeteria.getName());
        toolbar.setTitleTextColor(getResources().getColor(R.color.black87));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        setBookmarkIcon(menu.findItem(R.id.bookmark));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("CAFETERIAS", new Cafeteria[] {cafeteria});
                startActivity(intent);
                return true;
            case R.id.bookmark:
                helper.toggleBookmark(cafeteria);
                setBookmarkIcon(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean setBookmarkIcon(MenuItem icon) {
        boolean isBookmarked = helper.getBookmarkedCafeterias().contains(cafeteria);
        icon.setIcon(
                isBookmarked ?
                        R.drawable.ic_action_star : R.drawable.ic_action_star_outline
        );
        return isBookmarked;
    }

}
