package com.co.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.co.MarkerItem;
import com.co.R;
import com.co.http.Httpsave;
import com.co.util.ItemArray;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    View marker_root_view;
    TextView txtMarker;
    Marker selectedMarker;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    ArrayList<MarkerItem> arry;
    ArrayList<ItemArray> ia;

    // 사용자 위도,경도 저장 변수
    private double usersLat, usersLng;

    // 사용자 위도,경도 체크 변수
    private boolean checkLatLng = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);

        setCustomMarkerView();

        Log.d("체크확인", String.valueOf(checkLatLng));
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                usersLat = location.getLatitude();
                usersLng = location.getLongitude();
                Log.d("리스너", "로그 확인");

                // getMapAsync() 사용자 위도,경도를 얻은 후 한 번만 호출되게 하기 위함.
                if (usersLat != 0 && checkLatLng) {
                    mapFragment.getMapAsync(MapsActivity.this);
                    checkLatLng = false;
                    Log.d("getMapAsync 확인", "로그 확인");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }


    /*
    * 구글맵 콜백 메소드
    **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng usersLatLng = new LatLng(usersLat, usersLng);
        Log.d("위도&경도 체크2", String.valueOf(usersLat));
        Log.d("위도&경도 체크2", String.valueOf(usersLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLatLng, 16));

        mMap.setOnMarkerClickListener(this);

        getSampleMarkerItems();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    /*
    * 2017-05-20 류혁훈
    * 위도&경도를 이용하여 거리 계산
    * 리턴값의 단위는 km
    * */
    private double getDistance(double lat, double lng) {
        Location locationA = new Location("locationOfUser");
        locationA.setLatitude(usersLat);
        locationA.setLongitude(usersLng);

        Location locationB = new Location("destination");
        locationB.setLatitude(lat);
        locationB.setLongitude(lng);

        double dist = locationA.distanceTo(locationB) / 1000;
        Log.d("거리 계산", String.valueOf(dist));
        return dist;
    }

    /*
    * 2017-05-26 류혁훈
    * 커스텀 마커 세팅 메소드
    * */
    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        txtMarker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    /*
    * 2017-05-26 류혁훈
    * 마커 클래스 객체 생성 후 마커생성 메소드 호출
    * */
    private void getSampleMarkerItems() {
        ArrayList<MarkerItem> sampleList = new ArrayList<>();
        ia = Httpsave.arry;
        int size = ia.size();

        for(int i=0; i<size; i++) {
            double lat = Double.valueOf(ia.get(i).getY());
            double lng = Double.valueOf(ia.get(i).getX());
            sampleList.add(new MarkerItem(lat, lng, ia.get(i).getsName(), getDistance(lat, lng)));
        }
        for (MarkerItem markerItem : sampleList) {
            addMarker(markerItem, false);
        }
        arry = sampleList;
    }

    /* 2017-05-26 류혁훈
    * 커스텀 마커 생성 메소트
    * */
    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLng());
        String name = markerItem.getName();
        MarkerOptions markerOptions = new MarkerOptions();

        if (isSelectedMarker) {
            txtMarker.setText(name + "\n" + String.format("%.3f", markerItem.getDist()) + "km");
            txtMarker.setBackgroundResource(R.drawable.ic_marker_phone);
            txtMarker.setTextColor(Color.BLACK);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        }
        markerOptions.position(position);
        return mMap.addMarker(markerOptions);
    }


    /*
    * 2017-05-26 류혁훈
    * view를 bitmap으로 변환하는 메소드
    * */
    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        changeSelectedMarker(marker);
        Log.d("onMarkerClick", "메소드 호출 확인");
        return false;
    }

    private void changeSelectedMarker(Marker marker) {
        if(marker != null) {
            if(selectedMarker == null) {
                for (MarkerItem markerItem : arry) {
                    if (markerItem.getLat() == marker.getPosition().latitude
                            && markerItem.getLng() == marker.getPosition().longitude) {
                        selectedMarker = addMarker(markerItem, true);
                    }
                }
                marker.remove();
            } else {                // 이미 선택된 마커가 있는 경우
                double lat = selectedMarker.getPosition().latitude;
                double lng = selectedMarker.getPosition().longitude;
                /*
                * 2017-06-07 류혁훈
                * 마커 선택시 상세정보 보여주기
                * */
                if(marker.getPosition().latitude == lat && marker.getPosition().longitude == lng) {
                    int size = ia.size();
                    for(int i=0; i<size; i++) {
                        if(lat == Double.valueOf(ia.get(i).getY()) && lng == Double.valueOf(ia.get(i).getX())) {
                            Intent intent = new Intent(this, ScrollingActivity.class);
                            intent.putExtra("ID",ia.get(i).getsContentId());
                            intent.putExtra("IMAGE",ia.get(i).getsImage());
                            startActivity(intent);
                        }
                    }
                } else {
                    selectedMarker.remove();
                    setUpMarker(lat, lng);
                    for (MarkerItem markerItem : arry) {
                        if (markerItem.getLat() == marker.getPosition().latitude
                                && markerItem.getLng() == marker.getPosition().longitude) {
                            selectedMarker = addMarker(markerItem, true);
                        }
                    }
                }
            }
        }
    }

    /*
    * 2017-05-30 류혁훈
    * 기본 마커 생성 메소드
    * */
    private void setUpMarker(double lat, double lng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        mMap.addMarker(markerOptions);
    }
}
