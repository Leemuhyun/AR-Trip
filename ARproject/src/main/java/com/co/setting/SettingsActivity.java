package com.co.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.co.R;

import static com.co.R.id.rb5;

public class SettingsActivity extends AppCompatActivity {
    /* 프레퍼런스 파일명 */
    public static final String PREFS_NAME = "SettingsFile";

    SharedPreferences settings;
    Button btnAll, btnNature, btnRtr, btnCulture, btnHotel, btnLeports;
    RadioGroup rg_radius, rg_arrange;
    RadioButton rb_5, rb_10, rb_15, rb_20, rb_dist, rb_like, rb_abc;
    ImageButton btnSync;

    /* 카테고리, 거리, 정렬 각각의 속성값을 보관하는 변수 */
    public String selectedCategory, selectedDistance, selectedArrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        btnAll = (Button) findViewById(R.id.btnAll);
        btnNature = (Button) findViewById(R.id.btnNature);
        btnHotel = (Button) findViewById(R.id.btnHotel);
        btnLeports = (Button) findViewById(R.id.btnLeports);
        btnRtr = (Button) findViewById(R.id.btnRtr);
        btnCulture = (Button) findViewById(R.id.btnCulture);

        rg_radius = (RadioGroup) findViewById(R.id.rg_radius);
        rg_arrange = (RadioGroup) findViewById(R.id.rg_arrange);

        btnSync = (ImageButton) findViewById(R.id.btnSync);
        btnSync.setOnTouchListener(touch);


        rb_5 = (RadioButton) findViewById(rb5);
        rb_10 = (RadioButton) findViewById(R.id.rb10);
        rb_15 = (RadioButton) findViewById(R.id.rb15);
        rb_20 = (RadioButton) findViewById(R.id.rb20);

        rb_dist = (RadioButton) findViewById(R.id.rbDist);
        rb_like = (RadioButton) findViewById(R.id.rbLike);
        rb_abc = (RadioButton) findViewById(R.id.rbAbc);


        rg_radius.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb5:
                        selectedDistance = "5km";
                        Log.d("라디오버튼 체크 확인", selectedDistance);
                        break;
                    case R.id.rb10:
                        selectedDistance = "10km";
                        Log.d("라디오버튼 체크 확인", selectedDistance);
                        break;
                    case R.id.rb15:
                        selectedDistance = "15km";
                        Log.d("라디오버튼 체크 확인", selectedDistance);
                        break;
                    case R.id.rb20:
                        selectedDistance = "20km";
                        Log.d("라디오버튼 체크 확인", selectedDistance);
                        break;
                }
            }
        });

        rg_arrange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbDist:
                        selectedArrange = "DISTANCE";
                        Log.d("정렬 체크 확인", selectedArrange);
                        break;
                    case R.id.rbLike:
                        selectedArrange = "LIKE";
                        Log.d("정렬 체크 확인", selectedArrange);
                        break;
                    case R.id.rbAbc:
                        selectedArrange = "ABC";
                        Log.d("정렬 체크 확인", selectedArrange);
                        break;
                }
            }
        });
        /*
        * 2017-06-02 류혁훈
        * 프레퍼런스 값 얻기
        * */
        settings = getSharedPreferences(PREFS_NAME, 0);
        getCategory(settings.getString("CATEGORY", ""));
        getDistance(settings.getString("DISTANCE", ""));
        getArrange(settings.getString("ARRANGE", ""));
    }

    /*
    * 2017-06-02 류혁훈
    * Setting에서 선택된 Category 얻기 + SharedPreference
    * */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAll:
                btnAll.setBackgroundResource(R.drawable.all);
                btnHotel.setBackgroundResource(R.drawable.hotel2);
                btnCulture.setBackgroundResource(R.drawable.culture2);
                btnLeports.setBackgroundResource(R.drawable.leports2);
                btnRtr.setBackgroundResource(R.drawable.restaurant2);
                btnNature.setBackgroundResource(R.drawable.nature2);
                selectedCategory = "ALL";
                break;
            case R.id.btnCulture:
                btnAll.setBackgroundResource(R.drawable.all2);
                btnHotel.setBackgroundResource(R.drawable.hotel2);
                btnCulture.setBackgroundResource(R.drawable.culture);
                btnLeports.setBackgroundResource(R.drawable.leports2);
                btnRtr.setBackgroundResource(R.drawable.restaurant2);
                btnNature.setBackgroundResource(R.drawable.nature2);
                selectedCategory = "CULTURE";
                break;
            case R.id.btnHotel:
                btnAll.setBackgroundResource(R.drawable.all2);
                btnHotel.setBackgroundResource(R.drawable.hotel);
                btnCulture.setBackgroundResource(R.drawable.culture2);
                btnLeports.setBackgroundResource(R.drawable.leports2);
                btnRtr.setBackgroundResource(R.drawable.restaurant2);
                btnNature.setBackgroundResource(R.drawable.nature2);
                selectedCategory = "HOTEL";
                break;
            case R.id.btnLeports:
                btnAll.setBackgroundResource(R.drawable.all2);
                btnHotel.setBackgroundResource(R.drawable.hotel2);
                btnCulture.setBackgroundResource(R.drawable.culture2);
                btnLeports.setBackgroundResource(R.drawable.leports);
                btnRtr.setBackgroundResource(R.drawable.restaurant2);
                btnNature.setBackgroundResource(R.drawable.nature2);
                selectedCategory = "LEPORTS";
                break;
            case R.id.btnNature:
                btnAll.setBackgroundResource(R.drawable.all2);
                btnHotel.setBackgroundResource(R.drawable.hotel2);
                btnCulture.setBackgroundResource(R.drawable.culture2);
                btnLeports.setBackgroundResource(R.drawable.leports2);
                btnRtr.setBackgroundResource(R.drawable.restaurant2);
                btnNature.setBackgroundResource(R.drawable.nature);
                selectedCategory = "NATURE";
                break;
            case R.id.btnRtr:
                btnAll.setBackgroundResource(R.drawable.all2);
                btnHotel.setBackgroundResource(R.drawable.hotel2);
                btnCulture.setBackgroundResource(R.drawable.culture2);
                btnLeports.setBackgroundResource(R.drawable.leports2);
                btnRtr.setBackgroundResource(R.drawable.restaurant);
                btnNature.setBackgroundResource(R.drawable.nature2);
                selectedCategory = "RESTAURANT";
                break;
            case R.id.btnSync:
                settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("CATEGORY", selectedCategory);
                editor.putString("DISTANCE", selectedDistance);
                editor.putString("ARRANGE", selectedArrange);
                editor.commit();
                finish();
        }
    }
    /*
    * 2017-06-02 류혁훈
    * SharedPreference 카테고리 값 얻기
    * */
    private void getCategory(String name) {
        if(name.equals("ALL")) {
            btnAll.setBackgroundResource(R.drawable.all);
            selectedCategory = "ALL";
        } else if(name.equals("CULTURE")) {
            btnCulture.setBackgroundResource(R.drawable.culture);
            selectedCategory = "CULTURE";
        } else if(name.equals("LEPORTS")) {
            btnLeports.setBackgroundResource(R.drawable.leports);
            selectedCategory = "LEPORTS";
        } else if(name.equals("NATURE")) {
            btnNature.setBackgroundResource(R.drawable.nature);
            selectedCategory = "NATURE";
        } else if(name.equals("RESTAURANT")) {
            btnRtr.setBackgroundResource(R.drawable.restaurant);
            selectedCategory = "RESTAURANT";
        } else if(name.equals("HOTEL")) {
            btnHotel.setBackgroundResource(R.drawable.hotel);
            selectedCategory = "HOTEL";
        }
    }

    /*
   * 2017-06-02 류혁훈
   * SharedPreference 거리 값 얻기
   * */
    private void getDistance(String name) {
        if(name.equals("5km")) {
            selectedDistance = "5km";
            rb_5.setChecked(true);
        } else if(name.equals("10km")) {
            selectedDistance = "10km";
            rb_10.setChecked(true);
        } else if(name.equals("15km")) {
            selectedDistance = "15km";
            rb_15.setChecked(true);
        } else if(name.equals("20km")) {
            selectedDistance = "20km";
            rb_20.setChecked(true);
        }
    }

    /*
  * 2017-06-02 류혁훈
  * SharedPreference 정렬 값 얻기
  * */
    private void getArrange(String name) {
        if(name.equals("DISTANCE")) {
            selectedArrange = "DISTANCE";
            rb_dist.setChecked(true);
        } else if(name.equals("LIKE")) {
            selectedArrange = "LIKE";
            rb_like.setChecked(true);
        } else if(name.equals("ABC")) {
            selectedArrange = "ABC";
            rb_abc.setChecked(true);
        }
    }

    /*
  * 2017-06-04 류혁훈
  * 버튼 클릭효과 발생
  * */
    View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton view = (ImageButton) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setPadding(6, 6, 6, 6);
                    break;
                case MotionEvent.ACTION_UP:
                    view.setPadding(0, 0, 0, 0);
                    break;
            }
            return false;
        }
    };

}
