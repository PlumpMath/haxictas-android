package org.appeyroad.bob;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    private Cafeteria[] cafeterias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra("CAFETERIAS");
        cafeterias = new Cafeteria[parcelables.length];
        System.arraycopy(parcelables, 0, cafeterias, 0, parcelables.length);

        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        if (!isConnected) {
            Toast.makeText(this,
                    getString(R.string.no_connection_to_map), Toast.LENGTH_LONG).show();
        }

        GoogleMapOptions options = new GoogleMapOptions();
        options
                .mapType(GoogleMap.MAP_TYPE_NORMAL)
                .tiltGesturesEnabled(false)
                .zoomControlsEnabled(true)
                .compassEnabled(false);

        MapFragment mapFragment = MapFragment.newInstance(options);
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_frame, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double latSum = 0;
        double lngSum = 0;
        for (Cafeteria cafeteria : cafeterias) {
            double lat = Double.parseDouble(cafeteria.getCoordinate().split(",")[0]);
            double lng = Double.parseDouble(cafeteria.getCoordinate().split(",")[1]);
            latSum += lat;
            lngSum += lng;
            LatLng coordinate = new LatLng(lat, lng);
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(coordinate)
                            .title(cafeteria.getName())
                            .alpha(0.7f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place))
            ).showInfoWindow();
        }

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latSum / cafeterias.length, lngSum / cafeterias.length))
                .zoom(cafeterias.length == 1 ? 17 : 15)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager =
                        (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                boolean locationEnabled =
                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!locationEnabled) {
                    Toast.makeText(MapActivity.this,
                            getString(R.string.location_disabled),
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
