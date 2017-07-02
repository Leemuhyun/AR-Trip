package com.co.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.co.NetworkCheckBroadcast;
import com.co.R;
import com.co.adapter.SearchAdapter;
import com.co.cameraf.CameraActivity;
import com.co.http.Httpsave;
import com.co.setting.SettingsActivity;
import com.co.util.ItemArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    RecyclerView recyclerView;
    SearchAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout refreshLayout;
    private LocationManager mGpsLocationManager;
    private LocationManager mNetworkLocationManager;
    private LocationListener mGpsLocationListener;
    private LocationListener mNetworkLocationListener;
    private Location location;

    ArrayList<ItemArray> itemArrays;
    String i[];
    Httpsave h;
    SharedPreferences pref;
    NetworkCheckBroadcast receiver;
    IntentFilter filter;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navi_home:
                    return true;
                case R.id.navi_ar:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    } else {
                        if (location != null && NetworkCheckBroadcast.getWhatKindOfNetwork(MainActivity.this) == true) {
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "\t네트워크와 GPS \n연결 여부를 확인 하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                case R.id.navi_map:
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        pref = getSharedPreferences("SettingsFile", 0);

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkCheckBroadcast();
        registerReceiver(receiver, filter);

        // 새로고침 레이아웃 리스너등록작업
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);

        // 리사이클러뷰 어탭터등록 초기화
        mAdapter = new SearchAdapter( getApplicationContext(), refreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.mrecycle);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        // gps, network
        mGpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mNetworkLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 위도 경도 값을 가지고옴
        mGpsLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    i[0] = Double.toString(location.getLongitude());
                    i[1] = Double.toString(location.getLatitude());
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
        mNetworkLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    i[0] = Double.toString(location.getLongitude());
                    i[1] = Double.toString(location.getLatitude());
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

        // 현재 허용된 위치 프로바이더중에서
        // GPS, NETWORK 중 허용된 프로바이더를 등록함
        // 둘다 허용되어있지 않다면 설정을 물어보는 액티비티로 이동

        List<String> providers = mGpsLocationManager.getProviders(true);

        if (providers.contains("gps") && providers.contains("network")) {
            // GPS, NETWORK 위치 프로바이더 둘다 있을때
            mGpsLocationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 5,
                            600, mGpsLocationListener);
            location = mGpsLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //퍼미션체크
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mNetworkLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5, 600,
                    mNetworkLocationListener);

        } else if (providers.contains("gps")) {
            // GPS 위치 프로바이더만 있을때
            mGpsLocationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 5,
                            600, mGpsLocationListener);
            location = mGpsLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else if (providers.contains("network")) {
            // NETWORK 위치 프로바이더만 있을때
            mNetworkLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5, 600,
                    mNetworkLocationListener);
            location = mNetworkLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            showSettingsAlert(1);
        } else {
            // GPS, NETWORK 위치 프로바이더 둘다 없을때
            showSettingsAlert(2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        i = new String[]{"", "", radius(pref.getString("DISTANCE", "20000")),
                arrange(pref.getString("ARRANGE", "E")),
                categoryP(pref.getString("CATEGORY", ""))};

        if (location != null) {
            i[0] = Double.toString(location.getLongitude());
            i[1] = Double.toString(location.getLatitude());
            h = new Httpsave(mAdapter);
            h.execute(i);
        }
    }

    // 새로고침 레이아웃 콜백메소드 (밑으로 내렸을 경우 동작하는거)
    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        if (location != null) {
            h = new Httpsave(mAdapter);
            h.execute(i);
        }
    }

    //gps사용설정 다이얼로그
    public void showSettingsAlert(int num) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("GPS 사용유무셋팅");
        switch (num) {
            case 1:
                alertDialog.setMessage("GPS위치정보를 사용하시면 더욱 \n 정확한 위치정보를 받아올 수 있습니다.");
                break;
            case 2:
                alertDialog.setMessage("위치 정보를 받아 올수 없습니다.\n원활한 서비스를 위해서 설정창으로 \n 이동 하시겠습니까?");
                break;
        }
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    /*
    * 2017-06-01 류혁훈
    * 액션바 setting
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 액티비티가 소멸될때 위치 리스너자원을 해제해줌
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGpsLocationManager.removeUpdates(mGpsLocationListener);
        mNetworkLocationManager.removeUpdates(mNetworkLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 동의 및 로직 처리
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                } else {
                    // 동의 안함
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("카메라접근 권한설정");
                    alertDialog.setMessage("이기능을 사용하기 위해서는\n카메라접근 권한이 필요합니다.");
                    // OK 를 누르게 되면 설정창으로 이동합니다.
                    alertDialog.setNegativeButton("설정", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                            }
                        }
                    });
                    // Cancle 하면 종료 합니다.
                    alertDialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                return;
            }
            // 예외 케이스
        }
    }

    // http통신하기위해서 프레퍼런스에 저장되있는 값을 바꿔줌
    public String categoryP(String ca) {
        String result = ca;
        switch (result) {
            case "ALL":
                result = "";
                break;
            case "NATURE":
                result = "12";
                break;
            case "CULTURE":
                result = "14";
                break;
            case "LEPORTS":
                result = "28";
                break;
            case "HOTEL":
                result = "32";
                break;
            case "RESTAURANT":
                result = "39";
                break;
        }
        return result;
    }

    public String arrange(String arrange) {
        String result = arrange;
        switch (result) {
            case "ABC":
                result = "A";
                break;
            case "LIKE":
                result = "B";
                break;
            case "DISTANCE":
                result = "E";
                break;
        }
        return result;
    }

    public String radius(String ra) {
        String result = ra;
        switch (result) {
            case "5km":
                result = "5000";
                break;
            case "10km":
                result = "10000";
                break;
            case "15km":
                result = "15000";
                break;
            case "20km":
                result = "20000";
                break;
        }
        return result;
    }
}
