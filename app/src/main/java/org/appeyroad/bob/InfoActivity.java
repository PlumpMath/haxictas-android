package org.appeyroad.bob;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;


public class InfoActivity extends AppCompatActivity {

    private static final int[] images = {
            R.drawable.bg01,
            R.drawable.bg02,
            R.drawable.bg03,
            R.drawable.bg04,
            R.drawable.bg05
    };

    public static InfoPagerAdapter latestAdapter;

    private int tileColor;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private Cafeteria cafeteria;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        helper = DatabaseHelper.getInstance(this);

        Intent intent = getIntent();
        cafeteria = (Cafeteria)intent.getParcelableArrayExtra("CAFETERIAS")[0];
        tileColor = intent.getIntExtra(
                "COLOR",
                getResources().getColor(R.color.black54)
        );
        int alpha = ((int)(0.15f * 255));
        int backgroundColor = (alpha << 24) | (tileColor & 0x00FFFFFF);
        findViewById(R.id.root).setBackgroundColor(backgroundColor);

        int i = Math.abs(new Random().nextInt()) % images.length;
        ((ImageView)findViewById(R.id.background)).setImageResource(images[i]);

        viewPager = (ViewPager)findViewById(R.id.info_pager);
        InfoPagerAdapter adapter = new InfoPagerAdapter(this, cafeteria, tileColor);
        latestAdapter = adapter;
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.getTodayPage());

        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(cafeteria.getName());
        toolbar.setTitleTextColor(getResources().getColor(R.color.black87));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        setBookmarkFab(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookmarkFab(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.map:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("CAFETERIAS", new Cafeteria[] {cafeteria});
                startActivity(intent);
                return true;
            */
        }
        return super.onOptionsItemSelected(item);
    }

    private void setBookmarkFab(boolean toggle) {
        if (toggle) helper.toggleBookmark(cafeteria);
        fab.setImageResource(
                helper.getBookmarkedCafeterias().contains(cafeteria) ?
                R.drawable.ic_action_star : R.drawable.ic_action_star_outline
        );
    }

}
