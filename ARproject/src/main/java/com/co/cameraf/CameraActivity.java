package com.co.cameraf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.co.http.Httpsave;
import com.co.util.KeyValue;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.co.util.KeyValue.KEY_PREFERENCE_ARRANGE;
import static com.co.util.KeyValue.KEY_PREFERENCE_CATEGORY;
import static com.co.util.KeyValue.KEY_PREFERENCE_CLASS;
import static com.co.util.KeyValue.KEY_PREFERENCE_RADIUS;

public class CameraActivity extends AppCompatActivity {

    CameraOverlayView mCameraOverlayView;
    CameraPreview mCameraPreview;
    private LocationListener mGpsLocationListener;
    private LocationListener mNetworkLocationListener;
    private LocationManager mGpsLocationManager;
    private LocationManager mNetworkLocationManager;
    private LatLng mCurrentGpsGeoPoint;
    private LatLng mCurrentNetworkGeoPoint;
    private Geocoder mGpsGeoCoder;
    private Geocoder mNetworkGeoCoder;
    private int mGpsStatus = LocationProvider.OUT_OF_SERVICE;

    String i[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 제목 표시줄, 상태표시줄을 없애 전체화면으로 보여줌
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Display display = ((WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        // 카메라 프리뷰와 카메라 프리뷰 위에 보여줄 카메라 오버레이뷰 생성
        mCameraPreview = new CameraPreview(this);
        mCameraOverlayView = new CameraOverlayView(this, width, height);

        SharedPreferences pref = getSharedPreferences(KEY_PREFERENCE_CLASS, Activity.MODE_PRIVATE);

        i = new String[]{"", "", radius(pref.getString(KEY_PREFERENCE_RADIUS,"20000")),
                arrange(pref.getString(KEY_PREFERENCE_ARRANGE,"E")),
                categoryP(pref.getString(KEY_PREFERENCE_CATEGORY,""))};

        setContentView(mCameraPreview, new ViewGroup.LayoutParams(/*(int) (height * 1.7)*/width,
                height));
        addContentView(mCameraOverlayView, new ViewGroup.LayoutParams(/*(int) (height * 1.5)*/width,
                height));

        // 위치 서비스를 관리할 메니져 2개
        // GPS, NETWORK 두가지 위치 정보를 따로 관리

        mGpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // GPS 위치 리스너
        // 위도와 경도를 이용 주소를 알아냄

        mGpsLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub

                try {

                    if (location != null && mCameraOverlayView != null) {
                        double latitude, longitude;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        i[0] = Double.toString(longitude);
                        i[1] = Double.toString(latitude);

                        Log.d("LMH", "위도경도"+i[0]+i[1] );
                        http();

                        mGpsGeoCoder = new Geocoder(getBaseContext(),
                                Locale.KOREA);

                        List<Address> addresses = mGpsGeoCoder.getFromLocation(
                                latitude, longitude, 1);
                        Address address = addresses.get(0);
                        String addressString = address.getAddressLine(0);

                        mCurrentGpsGeoPoint = new LatLng(
                                (latitude), (longitude));

                        // 현재 위치와 현재 선택된 프로바이더를 카메라 오버레이 뷰에 알려줌

                        mCameraOverlayView.setCurrentGeoPoint(mCurrentGpsGeoPoint,
                                addressString);
                        mCameraOverlayView.setCurrentProvider("G");
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
                mGpsStatus = status;
            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

        };

        // NETWORK 위치 리스너
        // GPS STATUS가 이용가능하지 않을때만 위치 최신화
        // GPS가 켜져있지 않거나, 건물안에 들어왔을때 등 일시적으로 GPS 신호가 잡히지 않을때는 NETWORK 신호 이용
        // 위도와 경도를 이용 주소를 알아냄
        mNetworkLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mNetworkLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub

                try {

                    if (location != null && mCameraOverlayView != null
                            && mGpsStatus != LocationProvider.AVAILABLE) {

                        double latitude, longitude;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        i[0] = Double.toString(longitude);
                        i[1] = Double.toString(latitude);
                        Log.d("LMH", "위도경도"+i[0]+i[1] );

                        http();

                        mNetworkGeoCoder = new Geocoder(getBaseContext(),
                                Locale.KOREA);

                        List<Address> addresses = mNetworkGeoCoder
                                .getFromLocation(latitude, longitude, 1);
                        Address address = addresses.get(0);
                        String addressString = address.getAddressLine(0);
                        mCurrentNetworkGeoPoint = new LatLng(
                                 (latitude),  (longitude));

                        // 현재 위치와 현재 선택된 프로바이더를 카메라 오버레이 뷰에 알려줌
                        mCameraOverlayView.setCurrentGeoPoint(
                                mCurrentNetworkGeoPoint, addressString);
                        mCameraOverlayView.setCurrentProvider("N");
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

        };

        // 현재 허용된 위치 프로바이더중에서
        // GPS, NETWORK 중 허용된 프로바이더를 등록함
        // 둘다 허용되어있지 않다면 설정을 물어보는 액티비티로 이동

        List<String> providers = mGpsLocationManager.getProviders(true);

        if (providers.contains("gps") && providers.contains("network")) {
            // GPS, NETWORK 위치 프로바이더 둘다 있을때
            mGpsLocationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                            3, mGpsLocationListener);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mNetworkLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 3,
                    mNetworkLocationListener);

        } else if (providers.contains("gps")) {
            // GPS 위치 프로바이더만 있을때
            mGpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, mGpsLocationListener);

        } else if (providers.contains("network")) {
            // NETWORK 위치 프로바이더만 있을때
            mNetworkLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 3,
                    mNetworkLocationListener);
        } else {
            // GPS, NETWORK 위치 프로바이더 둘다 없을때
            finish();
        }
    }

    // 액티비티가 소멸될때 위치 리스너와 오버레이 뷰의 자원을 해제해줌
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraOverlayView.viewDestory();
        mGpsLocationManager.removeUpdates(mGpsLocationListener);
        mNetworkLocationManager.removeUpdates(mNetworkLocationListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public synchronized void http(){
        if(KeyValue.NETWORKCHECK == true) {
            Httpsave httpsave = new Httpsave(mCameraOverlayView);
            httpsave.execute(i);
        }
    }

    public String categoryP(String ca){
        String result = ca;
        switch (result){
            case "All":
                result = "";
                break;
            case "관광지":
                result = "12";
                break;
            case "문화시설":
                result = "14";
                break;
            case "행사/공연/축제":
                result = "15";
                break;
            case "여행코스":
                result = "25";
                break;
            case "레포츠":
                result = "28";
                break;
            case "숙박":
                result = "32";
                break;
            case "쇼핑":
                result = "38";
                break;
            case "음식점":
                result = "39";
                break;
        }
        return result;
    }

    public String arrange(String arrange){
        String result =arrange;
        switch (result){
            case "제목순":
                result = "A";
                break;
            case "인기순":
                result = "B";
                break;
            case "거리순":
                result = "E";
                break;
        }
        return result;
    }

    public String radius(String ra){
        String result = ra;
        switch (result){
            case "5Km":
                result = "50000";
                break;
            case "10Km":
                result = "100000";
                break;
            case "15Km":
                result = "150000";
                break;
            case "20Km":
                result = "200000";
                break;
        }
        return result;
    }
}
