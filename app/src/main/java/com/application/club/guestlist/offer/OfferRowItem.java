package com.application.club.guestlist.offer;

/**
 * Created by vichi on 07/03/18.
 */

public class OfferRowItem {

    //			INSERT INTO offers ( clubid ,clubname, city, location, offerid, offerName, OfferForTable, offerForPass, eventName,
//					 djname, music, date, starttime,
//					      imageURL, timetoexpire, priority)

    private String clubid;
    private String clubname;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOfferForPass() {
        return offerForPass;
    }

    public void setOfferForPass(String offerForPass) {
        this.offerForPass = offerForPass;
    }

    public String getOfferForTable() {
        return offerForTable;
    }

    public void setOfferForTable(String offerForTable) {
        this.offerForTable = offerForTable;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDjname() {
        return djname;
    }

    public void setDjname(String djname) {
        this.djname = djname;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    private String city;
    private String location;

    private String offerid;
    private String offerName;



    private String offerForPass;
    private String offerForTable;
    private String eventName;

    private String djname;
    private String music;

    private String eventdate;
    private String starttime;
    private String imageURL;
    private String timetoexpire;


    public String getClubid() {
        return clubid;
    }

    public void setClubid(String clubid) {
        this.clubid = clubid;
    }

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getOfferid() {
        return offerid;
    }

    public void setOfferid(String offerid) {
        this.offerid = offerid;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }



    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTimetoexpire() {
        return timetoexpire;
    }

    public void setTimetoexpire(String timetoexpire) {
        this.timetoexpire = timetoexpire;
    }










}

