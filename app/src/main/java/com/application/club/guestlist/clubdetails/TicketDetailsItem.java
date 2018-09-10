package com.application.club.guestlist.clubdetails;

/**
 * Created by vichi on 03/12/17.
 */

public class TicketDetailsItem {

    private String clubid;
    private String clubname;
    private String type;
    private String category;
    private String cost;
    private String size;
    private String details;
    private String Day;
    private String date;
    private String totaltickets;
    private String availbletickets;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCost() {
        return cost;
    }



    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotaltickets() {
        return totaltickets;
    }

    public void setTotaltickets(String totaltickets) {
        this.totaltickets = totaltickets;
    }

    public String getAvailbletickets() {
        return availbletickets;
    }

    public void setAvailbletickets(String availbletickets) {
        this.availbletickets = availbletickets;
    }


}
