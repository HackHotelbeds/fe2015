package trip.pojo;

import java.util.ArrayList;

/**
 * Created by Ibram on 27/06/2015.
 */
public class HotelOptions {
    public ArrayList<Hotel> getListHotel() {
        return listHotel;
    }

    public void setListHotel(ArrayList<Hotel> listHotel) {
        this.listHotel = listHotel;
    }

    private ArrayList<Hotel> listHotel;
    private String day;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
