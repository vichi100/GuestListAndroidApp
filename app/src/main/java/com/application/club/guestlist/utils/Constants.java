package com.application.club.guestlist.utils;

import com.application.club.guestlist.clubdetails.TicketDetailsItem;

import java.util.ArrayList;

/**
 * Created by vichi on 23/11/17.
 */

public class Constants {

    public static final String PREFS_NAME = "LoginPrefs";

    //public static String HTTP_URL = "http://198.167.140.169:3030";

    public static String HTTP_URL = "http://199.180.133.121:3030";
    public static String HTTP_VIDEO_URL = "http://199.180.133.121:3000";
    public static String HTTP_LAYOUT_HOST = "http://192.168.43.64";
    public static String TABLE_LAYOUT_URL = "/imagemap/layouthtml/";


    public static String CLUB_ID = "clubid";
    public static String CLUB_NAME = "clubname";
    public static String CITY = "city";
    public static String LOACTION = "location";
    public static String ADDRESS = "address";
    public static String IMAGE_URL = "imageURL";
    public static String VIDEO_URL= "videoURL";
    public static String LAT_LONG = "latlong";
    public static String RATING = "rating";

    //ticketdetails
    //public static String CLUB_ID = "clubid";
    //public static String CLUB_NAME = "clubname";
    public static String TICKET_TYPE = "type";
    public static String CATEGORY = "category";
    public static String COST = "cost";
    public static String PAID_AMOUNT = "paidamount";
    public static String REMAINING_AMOUNT = "remainingamount";
    public static String  DISCOUNT = "discount";
    public static String COSTAFTERDISCOUNT = "costafterdiscount";
    public static String DETAILS = "details";
    public static String DAY = "Day";
    public static String EVENTDATE = "date";
    public static String EVENT_DATE = "eventDate";
    public static String TOTAL_TICKETS = "totaltickets";
    public static String AVAILBLE_TICKETS = "availbletickets";
    public static String TABLE_SIZE = "size";

    public static  String RESPONSE_FROM_PAYMENTGETWAY = "paymentgetwayResponse";


    // eventdetails ( clubid, clubname,djname, music, date, imageURL)
    //public static String CLUB_ID = "clubid";
    //public static String CLUB_NAME = "clubname";
    public static String DJ_NAME = "djname";
    public static String MUSIC_TYPE = "music";
    //public static String DATE = "date";
    //public static String IMAGE_URL = "imageURL";

    public static String BOOKING_TYPE = "bookingType";

    public static String GIRL_COST = "girlCost";
    public static String STAG_COST = "stagCost";

    public static String TICKET_DETAILS = "ticketDetails";

    public static String CUSTOMERID = "customerId";
    public static String EMAIL = "email";


    public static String TOTAL_COST = "totalCost";

    public static String SELECTED_GUEST_TYPE = "selectedGuestType";

    // booking details table

    public static String CUSTOMERNAME = "cutomername";
    public static String MOBILE = "mobile";
    public static String  CLUBNME = "clubname";
    public static String QRNUMBER = "QRnumber";
    public static String TICKETTYPE ="tickettype";
    //public static String COST = "cost";
    public static String BOOKINGTIME = "bookingtime";
    public static String BOOKINGDATE = "bookingdate";


    // Offers

//    PRIVATE STRING CLUBID;
//    PRIVATE STRING CLUBNAME;
    public static String OFFERID = "offerid";

    public static String OFFERNAME= "offername";
    public static String OFFERVALUE= "offervalue";
    public static String OFFERFOR= "offerfor";
    public static String DJNAME= "djname";
    public static String MUSIC= "music";
    //public static String EVENTDATE= "eventdate";
    public static String STARTTIME= "starttime";
    //public static String IMAGE_URL= "imageURL";
    public static String TIME_TO_EXPIRE= "timetoexpire";
    public static String PRIORITY= "priority";
    public static String IS_NOTIFICATION= "isNotification";
    public static String PASS_DISCOUNT= "passdiscount";
    public static String TABLE_DISCOUNT= "tablediscount";
    public static String OFFERS_DETAILS = "offersDetails";


    public static String OFFERFORTABLE= "OfferForTable";
    public static String OFFERFORPASS= "offerForPass";
    public static String EVENTNAME= "eventName";

    public static String TABLE_ID = "tableid";
    public static String TABLE_TYPE = "tabletype";
    public static String SIZE = "size";
    public static String TABLE_NUMBER = "tablenumber";
    public static String LAYOUT_URL = "layoutURL";
    public static String ISBOOKED = "booked";

    public static String PAYMENT_FAILURE_MESSAGE = "If the payment gets debited from your account. The amount gets automatically refunded in 2-3 working days.";





    private static  ArrayList<TicketDetailsItem> ticketDetailsItemList;

    public static  ArrayList<TicketDetailsItem> getTicketDetailsItemList() {
        return ticketDetailsItemList;
    }

    public static void setTicketDetailsItemList(ArrayList<TicketDetailsItem> ticketDetailsItemListx) {
        ticketDetailsItemList = ticketDetailsItemListx;
    }



}
