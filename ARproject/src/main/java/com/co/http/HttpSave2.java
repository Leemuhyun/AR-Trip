package com.co.http;


import android.os.AsyncTask;

import com.co.activity.ScrollingActivity;
import com.co.util.ItemArray;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by angus on 2017-05-23.
 */

public class HttpSave2 extends AsyncTask<String, String, ItemArray> {

    Document doc;
    ItemArray itemArray;
    ScrollingActivity scrollingActivity;

    public HttpSave2(ScrollingActivity scrollingActivity) {
        this.scrollingActivity = scrollingActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ItemArray doInBackground(String... strings) {
        String s[] = strings;
        return weather(s[0]);
    }

    @Override
    protected void onPostExecute(ItemArray s) {
        scrollingActivity.add(s);
    }

    // 콘텐츠아이디를 받아서 그콘텐츠에 관한 상세정보 뿌려주는 http
    public ItemArray weather(String id) {

        String urladdrass =
                "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?serviceKey=NDUrRIh7HMblN%2BH0aUoYmqVwKg%2F1thDrqlbUBlv5EwYd%2FTWYdigg5qK7dO009Oiqi4zjQKgCZJ1KfrhnnMrL6A%3D%3D&MobileOS=AND&MobileApp=관광어플&contentId=" + id + "&defaultYN=Y&firstImageYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y";
        URL url;
        try {
            url = new URL(urladdrass);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
        }
        NodeList nodeList = doc.getElementsByTagName("item");

        itemArray = new ItemArray();
        Node node = nodeList.item(0);
        Element fstElmnt = (Element) node;
        if (node != null) {

            String resultsys = "";
                NodeList contentid = fstElmnt.getElementsByTagName("overview");
                Element contentidElement = (Element) contentid.item(0);
            if (contentid != null) {
                contentid = contentidElement.getChildNodes();
                resultsys = ((Node) contentid.item(0)).getNodeValue().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
                resultsys = resultsys.replaceAll("[a-zA-Z&;]","");
                itemArray.setSynopsis(resultsys);
            }

                NodeList dist = fstElmnt.getElementsByTagName("addr1");
                Element distElement1 = (Element) dist.item(0);
            if (distElement1 != null) {
                dist = distElement1.getChildNodes();
                itemArray.setAdrress(((Node) dist.item(0)).getNodeValue());
            }

            String result = "";
            NodeList homepage = fstElmnt.getElementsByTagName("homepage");
            Element homepageElement = (Element) homepage.item(0);
            if (homepageElement != null) {
                homepage = homepageElement.getChildNodes();
                String regex = "(http|https|ftp)://[^\\s^\\.]+(\\.[^\\s^\\.^\"^\']+)*";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(((Node) homepage.item(0)).getNodeValue());
                if (m.find()) {
                    result = m.group(0);
                    itemArray.setHomePage(result);
                }
            }else{
                itemArray.setHomePage("홈페이지가 없습니다.");
            }

                NodeList title = fstElmnt.getElementsByTagName("tel");
                Element titleElement = (Element) title.item(0);
                if (titleElement != null) {
                    title = titleElement.getChildNodes();
                    itemArray.setContactAddress(((Node) title.item(0)).getNodeValue());
                }else {
                    itemArray.setContactAddress("전화번호가 없습니다.");
                }
            }
            return itemArray;
        }
}
