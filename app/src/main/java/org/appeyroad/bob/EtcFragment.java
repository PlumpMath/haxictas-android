package org.appeyroad.bob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Guanadah on 2015-02-02.
 */
public class EtcFragment extends Fragment {

    private Activity activity;

    public static EtcFragment newInstance() {
        return new EtcFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credits, container, false);
        ColorPicker colorPicker = new ColorPicker(activity);

        View info = view.findViewById(R.id.info);
        View email = view.findViewById(R.id.email);
        View facebook = view.findViewById(R.id.facebook);
        View viewMode = view.findViewById(R.id.view_mode);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(activity.getString(R.string.about_details));
                stringBuilder.append("\n\n");
                stringBuilder.append(activity.getString(R.string.about_details_more));
                stringBuilder.append("\n\n");
                stringBuilder.append(activity.getString(R.string.about_jsoup));
                String message = stringBuilder.toString();

                String versionName = null;
                try {
                    versionName = activity.getPackageManager()
                            .getPackageInfo(activity.getPackageName(), 0).versionName;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setTitle(activity.getString(R.string.about_title) + " " + versionName)
                        .setMessage(message)
                        .setPositiveButton(R.string.confirm, null);
                builder.create().show();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","guanadah@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.email_subject));
                startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.choose_app)));
                */
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("fb://profile/"));
                } catch (Exception e) {
                    intent.setData(Uri.parse("http://https://www.facebook.com/AppeyRoad"));
                }
                startActivity(intent);
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("fb://page/348690058615630"));
                } catch (Exception e) {
                    intent.setData(Uri.parse("https://www.facebook.com/arandommeaninglessusername"));
                }
                startActivity(intent);
            }
        });
        final TextView viewModeText = (TextView)view.findViewById(R.id.view_mode_text);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.view_mode_full).equals(viewModeText.getText())) {
                    Preferences.setViewMode(activity, 0, CafeteriasAdapter.FULL);
                    Preferences.setViewMode(activity, 1, CafeteriasAdapter.FULL);
                } else if (getString(R.string.view_mode_simple).equals(viewModeText.getText())) {
                    Preferences.setViewMode(activity, 0, CafeteriasAdapter.SIMPLE);
                    Preferences.setViewMode(activity, 1, CafeteriasAdapter.SIMPLE);
                }
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
        });
        viewModeText.setText(Preferences.getViewMode(activity, 0) == CafeteriasAdapter.FULL ?
            getString(R.string.view_mode_simple) : getString(R.string.view_mode_full));

        return view;
    }

}
