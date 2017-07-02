package com.co.http;

import android.os.AsyncTask;
import android.util.Log;

import com.co.adapter.SearchAdapter;
import com.co.cameraf.CameraOverlayView;
import com.co.util.ItemArray;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by angus on 2017-05-18.
 */

public class Httpsave extends AsyncTask<String, String, ArrayList<ItemArray>> {

    /* 류혁훈 */
    public static ArrayList<ItemArray> arry;

    Document doc;
    ItemArray itemArray;
    ArrayList<ItemArray> itemArrays;
    SearchAdapter mSearchAdapter;
    CameraOverlayView mCameraOverlayView;

    public Httpsave(CameraOverlayView s){
        mCameraOverlayView=s;
    }
    public Httpsave(SearchAdapter s){
        mSearchAdapter=s;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ItemArray> doInBackground(String... strings) {
        String s[] = strings;
        return weather(s[0],s[1],s[2],s[3],s[4]);
    }

    @Override
    protected void onPostExecute(ArrayList<ItemArray> s) {
        if(mCameraOverlayView != null){
            mCameraOverlayView.add(s);
        }else if(mSearchAdapter != null){
            mSearchAdapter.add(s);
        }
    }

    //사용자의 위도경도값을 받아서 하는 http통신
    public ArrayList<ItemArray> weather(String x,String y,String radius,String arrange,String contentTypeId) {
        itemArrays = new ArrayList<>();
        String urladdrass =
                "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?serviceKey=NDUrRIh7HMblN%2BH0aUoYmqVwKg%2F1thDrqlbUBlv5EwYd%2FTWYdigg5qK7dO009Oiqi4zjQKgCZJ1KfrhnnMrL6A%3D%3D&MobileOS=AND&MobileApp=관광어플&mapX="+x+"&mapY="+y+"&radius="+radius+"&arrange="+arrange+"&contentTypeId="+contentTypeId+"&numOfRows=20";
        URL url;
        try {
            url = new URL(urladdrass);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
        } catch (Exception e) {}
        NodeList nodeList = doc.getElementsByTagName("item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Log.d("LMH","포문들어옴");
            itemArray = new ItemArray();
            Node node = nodeList.item(i);
            Element fstElmnt = (Element) node;
            if (node != null) {

                NodeList contentid = fstElmnt.getElementsByTagName("contentid");
                Element contentidElement = (Element) contentid.item(0);
                contentid = contentidElement.getChildNodes();

                itemArray.setsContentId(((Node) contentid.item(0)).getNodeValue());

                NodeList dist = fstElmnt.getElementsByTagName("dist");
                Element distElement1 = (Element) dist.item(0);
                dist = distElement1.getChildNodes();

                itemArray.setsDistanse(((Node) dist.item(0)).getNodeValue());

                NodeList  image = fstElmnt.getElementsByTagName("firstimage");
                Element imageElement = (Element) image.item(0);
                if(imageElement != null && !imageElement.equals("")) {
                    image = imageElement.getChildNodes();
                    itemArray.setsImage(((Node) image.item(0)).getNodeValue());
                }

                NodeList title = fstElmnt.getElementsByTagName("title");
                Element titleElement = (Element) title.item(0);
                title = titleElement.getChildNodes();

                itemArray.setsName(((Node) title.item(0)).getNodeValue());

                NodeList mapx = fstElmnt.getElementsByTagName("mapx");
                Element mapxElement = (Element) mapx.item(0);
                if(mapxElement != null) {
                    mapx = mapxElement.getChildNodes();

                    itemArray.setX(((Node) mapx.item(0)).getNodeValue());

                    NodeList mapy = fstElmnt.getElementsByTagName("mapy");
                    Element mapyElement = (Element) mapy.item(0);
                    mapy = mapyElement.getChildNodes();

                    itemArray.setY(((Node) mapy.item(0)).getNodeValue());
                }

                NodeList contenttypeid = fstElmnt.getElementsByTagName("contenttypeid");
                Element contenttypeidElement2 = (Element) contenttypeid.item(0);
                contenttypeid = contenttypeidElement2.getChildNodes();

                itemArray.setsContentTypeId(((Node) contenttypeid.item(0)).getNodeValue());

                itemArrays.add(itemArray);
            }
        }
        arry = itemArrays;                  // 구글맵에 정보 전달
        return itemArrays;
    }

}