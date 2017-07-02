package com.co.cameraf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.co.R;
import com.co.activity.ScrollingActivity;
import com.co.util.ItemArray;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by angus on 2017-05-23.
 */

public class CameraOverlayView extends View implements SensorEventListener {

    private static float mXCompassDegree;
    private static float mYCompassDegree;
    private SensorManager mSensorManager;
    private Sensor mOriSensor;
    private int mWidth;
    private int mHeight;
    private LatLng mCurrentGeoPoint = null;
    private boolean mCurrentGeoPointUpdated = false;
    private Paint mPaint;
    private Bitmap mPalaceIconBitmap;
    private String mAdressString;
    private String mProviders;
    private String mCurrentProvider;
    private List<PointF> mPointFList = null;
    private HashMap<Integer, String> mPointHashMap;
    private CameraActivity mContext;
    private int mVisibleDistance = 200;
    private float mTouchedY;
    private float mTouchedX;
    private boolean mScreenTouched = false;
    private int mCounter = 0;
    private Paint mTouchEffectPaint;
    private Bitmap mCultureIconBitmap;
    private Bitmap mLandscapesIconBitmap;
    private Bitmap mMuseumIconBitmap;
    private Bitmap mShoppingIconBitmap;
    private Bitmap mOtherIconBitmap;
    private ArrayList<ItemArray> itemArray;
    private ItemArray item;
    private int x,y, y1;

    public CameraOverlayView(Context context,int width, int height) {
        super(context);
        // TODO Auto-generated constructor stub

        mContext = (CameraActivity) context;

        // 디스플레이 크기 설정
        mWidth = width;
        mHeight = height;
        if(mHeight > 900){
            x =  135; y = 70; y1 = 30;
        }else {
            x = 85; y = 25; y1 = 7;
        }

        // 비트맵, 센서, 페인트 초기화
        initBitamaps();
        initSensor(context);
        initPaints();
    }

    // onSensorChanged에서 센서값 TYPE_ORIENTATION이 일정한 시간마다 INVALIDATE되어 실행됨
    // 정보를 계속 그리면서 표현, DB의 레코드를 해석하면서 아이템을 그림
    public void onDraw(Canvas canvas) {

        canvas.save();

        // 안드로이드 2.1이하에서는 카메라 화면이 오른쪽으로 90도 돌아간 화면으로 나옴
        // 화면을 돌리기 위하여 사용
        canvas.rotate(270, mWidth / 2, mHeight / 2);

        // 현재 위치 정보를 그림
        drawCurrentLocationInfo(canvas);

        // DB의 레코드를 읽어들이고, drawGrid를 실행시켜 랜드마크 아이템들을 그림
        interpretDB(canvas);

        // 회전된 카메라를 원상복귀함
        canvas.restore();

        // 스크린이 터치되었을때 효과를 그림
        if (mScreenTouched == true && mCounter < 15) {
            drawTouchEffect(canvas);
            mCounter++;
        } else {
            mScreenTouched = false;
            mCounter = 0;
        }
    }

    // 스크린이 터치될때의 효과를 그림 원 3개를 물결처럼 그림
    private void drawTouchEffect(Canvas pCanvas) {
        // TODO Auto-generated method stub
        pCanvas.drawCircle(mTouchedX, mTouchedY, mCounter * 1,
                mTouchEffectPaint);
    }

    // 스크린이 터치되었을때 좌표를 해석하고 처리
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        // 화면이 회전되었기에 좌표도 변환함
        float convertedX, convertedY, temp;
        convertedX = event.getX();
        convertedY = event.getY();
        convertedX = convertedX - mWidth / 2;
        convertedY = convertedY - mHeight / 2;
        temp = convertedX;
        convertedX = -convertedY;
        convertedY = temp;

        mTouchedX = event.getX();
        mTouchedY = event.getY();

        mScreenTouched = true;

//         아이템을 터치시 처리
//         터치시 플래그, 터치된 아이템 번호 설정
        PointF tPoint = new PointF();
        Iterator<PointF> pointIterator = mPointFList.iterator();
        for (int i = 0; i < mPointFList.size(); i++) {
            tPoint = pointIterator.next();

            if (convertedX > tPoint.x - (mPalaceIconBitmap.getWidth() / 2 )+10
                    && convertedX < tPoint.x
                    + (mPalaceIconBitmap.getWidth() / 2)+10
                    && convertedY > tPoint.y
                    - (mPalaceIconBitmap.getHeight() / 2)+10
                    && convertedY < tPoint.y
                    + (mPalaceIconBitmap.getHeight() / 2)+10) {

                Intent intent = new Intent(mContext, ScrollingActivity.class);
                intent.putExtra("ID", itemArray.get(i).getsContentId());
                intent.putExtra("IMAGE", itemArray.get(i).getsImage());
                mContext.startActivity(intent);
            }
        }

        return super.onTouchEvent(event);

    }

    // 테마 아이콘 비트맵 초기화
    private void initBitamaps() {
        int size = 0;
        if(mHeight > 900){
            size = 110;
        }else {
            size = 50;
        }
        mPalaceIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.hotel);
        mPalaceIconBitmap = Bitmap.createScaledBitmap(mPalaceIconBitmap, size,
                size, true);

        mCultureIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.culture);
        mCultureIconBitmap = Bitmap.createScaledBitmap(mCultureIconBitmap, size,
                size, true);

        mLandscapesIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.cloud);
        mLandscapesIconBitmap = Bitmap.createScaledBitmap(
                mLandscapesIconBitmap, size, size, true);

        mMuseumIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.leports);
        mMuseumIconBitmap = Bitmap.createScaledBitmap(mMuseumIconBitmap, size,
                size, true);

        mShoppingIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.shopping);
        mShoppingIconBitmap = Bitmap.createScaledBitmap(mShoppingIconBitmap,
                size, size, true);

        mOtherIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.other_image);
        mOtherIconBitmap = Bitmap.createScaledBitmap(mOtherIconBitmap,
                size, size, true);

    }

    // 센서 초기화
    // TYPE_ORIENTATION 사용할수 있게 설정
    private void initSensor(Context context) {
        // TODO Auto-generated method stub
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mOriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mOriSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    // 페인트 초기화
    // 그려질 여러 메뉴, 아이템의 페인트 설정
    private void initPaints() {
        // TODO Auto-generated method stub

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(238, 229, 222));
        if(mHeight > 900) {
            mPaint.setTextSize(40);
        }else {
            mPaint.setTextSize(18);
        }
        mPaint.setStyle(Paint.Style.STROKE);

        mTouchEffectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchEffectPaint.setColor(Color.rgb(193, 205, 192));
        mTouchEffectPaint.setStrokeWidth(5);
        mTouchEffectPaint.setStyle(Paint.Style.STROKE);
    }

    // 현재 보여지는 범위, 위치 정보를 보여줌
    private void drawCurrentLocationInfo(Canvas pCanvas) {
        // TODO Auto-generated method stub

        // 현재 위치를 알수 있을 때 보여줌
        if (mCurrentGeoPointUpdated) {
        } else {
            pCanvas.drawText("---현재 위치 찾는 중---",
                    mWidth / 2 - (mPaint.measureText("---현재 위치 찾는 중---") / 2),
                    mHeight / 2 - 20, mPaint);
        }
    }

    // 선택된 테마의 랜드마크를 그림
    // 현재의 위치정보와 랜드마크의 위치정보를 이용하여 두 위치간의 각도를 계산하고,
    // 현재 기기의 방향이 동쪽 기준 각도가 몇인지를 참고로
    // 기기 화면에 계속 새로고침됨
    // 두 위치간의 거리 또한 표시
    // 정면이 90도라하였을때 75도에서 105도 사이 30도가 시야각
    private PointF drawGrid(double tAx, double tAy, double tBx, double tBy,
                            Canvas pCanvas, Paint pPaint, String name, String dist, String theme, int hei) {
        // TODO Auto-generated method stub

        // 현재 위치와 랜드마크의 위치를 계산하는 공식
        double mXDegree = (double) (Math.atan((double) (tBy - tAy)
                / (double) (tBx - tAx)) * 180.0 / Math.PI);
        float mYDegree = mYCompassDegree; // 기기의 기울임각도

        // 4/4분면을 고려하여 0~360도가 나오게 설정
        if (tBx > tAx && tBy > tAy) {
            ;
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
        }

        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }

        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        // 정중앙은 90도, 시야각은 30도로 75 ~ 105 사이일때만 화면에 나타남
        float mX = 0;
        float mY = 0;

        if (mXDegree > 75 && mXDegree < 105) {
            if (mYDegree > -180 && mYDegree < 0) {

                mX = (float) mWidth
                        - (float) ((mXDegree - 75) * ((float) mWidth / 30));

                mYDegree = -(mYDegree);

                mY = (float) (mYDegree * ((float) mHeight / 180));

            }

        }

        Bitmap tIconBitmap = null;
        if (theme.equals("32")) {
            tIconBitmap = mPalaceIconBitmap;
        } else if (theme.equals("38")) {
            tIconBitmap = mCultureIconBitmap;
        } else if (theme.equals("39")) {
            tIconBitmap = mShoppingIconBitmap;
        } else if (theme.equals("12")) {
            tIconBitmap = mLandscapesIconBitmap;
        } else if (theme.equals("28")) {
            tIconBitmap = mMuseumIconBitmap;
        } else {
            tIconBitmap = mOtherIconBitmap;
        }

        int iconWidth, iconHeight;
        iconWidth = tIconBitmap.getWidth();
        iconHeight = tIconBitmap.getHeight();

        int mdist = Integer.parseInt(dist);

        int mhei = hei(hei);
        Log.d("LMH",mhei+"");


        // 랜드마크에 해당하는 테마 아이콘과 이름, 거리를 그림
        // 거리는 1000미터 이하와 초과로 나누어 m, Km로 출력
        if (mdist <= mVisibleDistance * 1000) {
            if (mdist < 1000) {

                pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), (mY
                        - (iconHeight / 2)) + mhei, pPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2 + x, (mY
                        + iconHeight / 2 - y) + mhei, pPaint);

                pCanvas.drawText(mdist + "m",
                        mX - pPaint.measureText(mdist + "m") / 2 + x, (mY
                                + iconHeight / 2 - y1) + mhei, pPaint);

            } else if (mdist >= 1000) {
                float fDistance = (float) mdist / 1000;
                fDistance = (float) Math.round(fDistance * 10) / 10;

                pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), (mY
                        - (iconHeight / 2)) + mhei, pPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2 + x, (mY
                        + iconHeight / 2 - y) + mhei, pPaint);

                pCanvas.drawText(fDistance + "Km",
                        mX - pPaint.measureText(fDistance + "Km") / 2 + x, (mY
                                + iconHeight / 2 - y1) + mhei, pPaint);

            }
        }

        // 현재의 회전되기전의 좌표를 회전된 좌표로 변환한후 반환함
        PointF tPoint = new PointF();

        tPoint.set(mX - mWidth / 2, (mY - mHeight / 2)+mhei);
        return tPoint;
    }

    // DB를 해석하여 레코드를 하나씩 읽어
    // 그리는 함수를 호출
    private void interpretDB(Canvas pCanvas) {

        // TODO Auto-generated method stub
        double tAx, tAy, tBx, tBy;

        // 현재 위치를 알수 없을때는 임의의 위치를 현재 위치로 설정
        //변경함
        if (mCurrentGeoPoint != null) {
            tAx = (double) mCurrentGeoPoint.longitude;
            tAy = (double) mCurrentGeoPoint.latitude;
        } else {
            tAx = 127.03962206840515;
            tAy = 37.501284929467126;
        }

        mPointFList = new ArrayList<PointF>();
        mPointHashMap = new HashMap<Integer, String>();

        String tName;
        PointF tPoint;
        String dist;
        String theme;

        // DB를 하나씩 읽어 랜드마크를 화면에 그리는 함수 호출
        if (itemArray != null) {
            for (int i = 0; i < itemArray.size(); i++) {
                item = itemArray.get(i);
                if (item != null) {
                    tName = item.getsName();
                    tBx = Double.parseDouble(item.getX());
                    tBy = Double.parseDouble(item.getY());
                    dist = item.getsDistanse();
                    theme = item.getsContentTypeId();

                    // 화면에 그림 35.851998, 128.483561
                    tPoint = drawGrid(tAx, tAy, tBx, tBy, pCanvas, mPaint, tName, dist,
                            theme, i);

                    // 랜드마크 아이탬의 화면 위치를 리스트로 저장
                    // 해시맵으로 아이템 번호와 이름을 저장
                    // 랜드마크 아이템이 터치되었을때 어떤 아이템이 터치 되었는지 확인하기 위함
                    mPointFList.add(tPoint);
                    mPointHashMap.put(0, item.getsContentId());
                }
            }
        }
    }

    public void add(ArrayList<ItemArray> s) {
        itemArray = s;
        Log.d("LMH", "카메라오버레이"+s.size());
    }

    // 센서가 바뀔때마다 실행됨
    // 기기의 방향중 X, Y값을 저장하고 오버레이 화면을 다시 그리게 함

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            mXCompassDegree = event.values[0];
            mYCompassDegree = event.values[1];

            this.invalidate();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    // 카메라 액티비티가 소멸될때 센서 리스너를 해제
    public void viewDestory() {
        mSensorManager.unregisterListener(this);

    }

    // 카메라 액티비티에서 현재 위치 정보를 알려줌
    public void setCurrentGeoPoint(LatLng currentGeoPoint,
                                   String addressString) {
        // TODO Auto-generated method stub
        mCurrentGeoPoint = currentGeoPoint;
        mCurrentGeoPointUpdated = true;
        mAdressString = addressString;
    }

    // 카메라 액티비티에서 현재 사용하는 프로바이더를 알려줌
    public void setCurrentProvider(String provider) {
        // TODO Auto-generated method stub
        mCurrentProvider = provider;
    }

    public int hei(int i) {
        int heightim = 0;
        int arrangenum = 0;
        if(mHeight > 900){
            arrangenum = 100;
        }else if(mHeight < 900 ){
            arrangenum = 50;
        }
        switch (i) {
            case 0:
                heightim = arrangenum;
                break;
            case 1:
                heightim = arrangenum*2;
                break;
            case 2:
                heightim = arrangenum*3;
                break;
            case 3:
                heightim = arrangenum*4;
                break;
            case 4:
                heightim = -arrangenum;
                break;
            case 5:
                heightim = -arrangenum*2;
                break;
            case 6:
                heightim = -arrangenum*3;
                break;
            case 7:
                heightim = -arrangenum*4;
                break;
            case 8:
                heightim = arrangenum*5;
                break;
            case 9:
                heightim =-arrangenum*5;
                break;
            case 10:
                heightim =-arrangenum+460;
                break;
            case 11:
                heightim =arrangenum+460;
                break;
            case 12:
                heightim =-arrangenum+260;
                break;
            case 13:
                heightim =arrangenum+360;
                break;
            case 14:
                heightim =arrangenum-arrangenum;
                break;
            case 15:
                heightim =arrangenum+50;
                break;
            case 16:
                heightim =-arrangenum+50;
                break;
            case 17:
                heightim =-arrangenum+160;
                break;
            case 18:
                heightim =arrangenum+160;
                break;
            case 19:
                heightim =arrangenum+260;
                break;

        }
        return heightim;
    }


}
