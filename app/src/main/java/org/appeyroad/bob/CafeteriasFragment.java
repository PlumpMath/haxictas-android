package org.appeyroad.bob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CafeteriasFragment extends Fragment {

    public static ArrayList<CafeteriasAdapter> adapters = new ArrayList<>();

    public CafeteriasAdapter adapter;

    private Activity activity;

    public static CafeteriasFragment newInstance(int dataType) {
        CafeteriasFragment fragment = new CafeteriasFragment();
        Bundle args = new Bundle();
        args.putInt("DATA_TYPE", dataType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int dataType = args.getInt("DATA_TYPE");
        View view = inflater.inflate(Preferences.getItemType(activity, dataType) == CafeteriasAdapter.GRID ?
                R.layout.fragment_cafeterias_grid : R.layout.fragment_cafeterias_list,
                container, false);
        AdapterView adapterView = (AdapterView)view.findViewById(R.id.adapter_view_cafeteria);
        adapter = new CafeteriasAdapter(activity, dataType);
        adapters.add(adapter);

        TextView text = (TextView)view.findViewById(R.id.empty_view_text);
        text.setText(activity.getString(dataType == CafeteriasAdapter.ALL ?
                R.string.no_main_info : R.string.no_bookmarks));
        ImageView icon = (ImageView)view.findViewById(R.id.empty_view_icon);
        icon.setImageResource(dataType == CafeteriasAdapter.ALL ?
                R.drawable.ic_back : R.drawable.ic_action_star);
        icon.setAlpha(dataType == CafeteriasAdapter.ALL ?
                1.0f : 0.26f);
        icon.setVisibility(dataType == CafeteriasAdapter.ALL ?
                View.INVISIBLE : View.VISIBLE);
        adapterView.setEmptyView(view.findViewById(R.id.empty_view));

        adapterView.setAdapter(adapter);
        adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, InfoActivity.class);
                intent.putExtra("CAFETERIAS", new Cafeteria[] {adapter.getItem(position)});

                try {
                    intent.putExtra("COLOR", (int) view.getTag());
                } catch (NullPointerException e) {
                    intent.putExtra("COLOR", getResources().getColor(R.color.primary_light));
                }

                activity.startActivity(intent);
            }
        });
        return view;
    }

    public static void reloadAllData() {
        for (CafeteriasAdapter adapter : adapters) {
            if (adapter != null) adapter.reloadData();
        }
    }
}
