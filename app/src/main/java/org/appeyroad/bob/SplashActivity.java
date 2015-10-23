package org.appeyroad.bob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class SplashActivity extends Activity {

    private static final int DELAY = 500;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler() {
            boolean isExpired = false;

            @Override
            public void handleMessage(Message msg) {
                if (isExpired) {
                    return;
                }
                isExpired = true;

                final Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                switch (msg.what) {
                    case PageParser.FINISHED_PARSING:
                        startActivity(intent);
                        finish();
                        break;
                    case PageParser.EXCEPTION_OCCURRED:
                        Toast.makeText(SplashActivity.this,
                                getString(R.string.loading_exception), Toast.LENGTH_LONG).show();
                    case PageParser.TODAY_DATA_EXISTS:
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                                finish();
                            }
                        }, DELAY);
                        break;
                }

                if (isConnected()) {
                    PageParser parser = new PageParser(SplashActivity.this);
                    parser.getAllData();
                }
            }
        };

        helper = DatabaseHelper.getInstance(this);
        helper.clearInvalidMenus();

        tryGettingTodayData(handler);

    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void tryGettingTodayData(final Handler handler) {
        if (helper.getMenus(Date.today()).size() < 2 && !isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_data_today)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tryGettingTodayData(handler);
                        }
                    })
                    .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();
        } else {
            PageParser parser = new PageParser(this);
            parser.getTodayDataForHandler(handler);
        }
    }
}
