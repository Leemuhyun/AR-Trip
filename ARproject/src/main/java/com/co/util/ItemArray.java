package com.co.util;

/**
 * Created by angus on 2017-05-17.
 */

// http를 하기위해서 만들어 놓은 저장객체
public class ItemArray {

    private String sImage;
    private String sName;
    private String sDistanse;
    private String sContentId;
    private String sContentTypeId;

    private String synopsis;
    private String adrress;
    private String contactAddress;
    private String homePage;
    private String x;
    private String y;

    public String getsContentTypeId() {
        return sContentTypeId;
    }

    public void setsContentTypeId(String sContentTypeId) {
        this.sContentTypeId = sContentTypeId;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsDistanse() {
        return sDistanse;
    }

    public void setsDistanse(String sDistanse) {
        this.sDistanse = sDistanse;
    }

    public String getsContentId() {
        return sContentId;
    }

    public void setsContentId(String sContentId) {
        this.sContentId = sContentId;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getAdrress() {
        return adrress;
    }

    public void setAdrress(String adrress) {
        this.adrress = adrress;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
