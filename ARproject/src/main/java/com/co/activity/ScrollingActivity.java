package com.co.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.co.R;
import com.co.UnityPlayerActivity;
import com.co.http.HttpSave2;
import com.co.util.ItemArray;


public class ScrollingActivity extends AppCompatActivity {

    String id;
    String image;
    ImageView imageView;
    TextView sytext, teltext, homepagetext, adresstext;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        settings = getSharedPreferences("Scroll", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("SID", getIntent().getStringExtra("ID").toString());
        if (getIntent().getStringExtra("IMAGE") != null) {
            editor.putString("SIMAGE", getIntent().getStringExtra("IMAGE"));
        }
        editor.commit();

        imageView = (ImageView) findViewById(R.id.scrollImage);
        sytext = (TextView)findViewById(R.id.sytext);
        teltext = (TextView)findViewById(R.id.teltext);
        homepagetext = (TextView)findViewById(R.id.homepagetext);
        adresstext = (TextView)findViewById(R.id.addresstext);

        // 이미지파일에 경우 일정 용량을 넘어가면 안드로이드에서 이미지를 로딩할수없음
        // 그문제를 해결하고자 Glide라는 오픈소스라이브러리 사용함
        if (getIntent().getStringExtra("IMAGE") != null) {
            image = getIntent().getStringExtra("IMAGE").toString();
            Glide.with(getApplicationContext()).load(image).thumbnail(0.1f).into(imageView);
        }else {
            Glide.with(getApplicationContext()).load(R.drawable.no_image).into(imageView);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScrollingActivity.this, UnityPlayerActivity.class));
            }
        });

        id = getIntent().getStringExtra("ID").toString();
            HttpSave2 httpSave2 = new HttpSave2(this);
            httpSave2.execute(id);
    }

    public void onClickEvent(View view){
        switch (view.getId()){
            case R.id.teltext:
                Intent intenttel = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+teltext.getText().toString()));
                startActivity(intenttel);
                break;
            case R.id.homepagetext:
                Intent intenthome = new Intent(Intent.ACTION_VIEW, Uri.parse(homepagetext.getText().toString()));
                startActivity(intenthome);
                break;
        }
    }

    // httpSave2의 작업을 끝내면 여기서 text를 설정해줌
    public void add(ItemArray itemArray){
        sytext.setText(itemArray.getSynopsis());
        teltext.setText(itemArray.getContactAddress());
        homepagetext.setText(itemArray.getHomePage());
        adresstext.setText(itemArray.getAdrress());
    }


}
