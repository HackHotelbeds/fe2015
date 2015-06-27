package trip.pojo;

import java.util.ArrayList;

/**
 * Created by Ibram on 27/06/2015.
 */
public class TicketOptions {
    public ArrayList<Ticket> getListTicket() {
        return listTicket;
    }

    public void setListTicket(ArrayList<Ticket> listTicket) {
        this.listTicket = listTicket;
    }

    ArrayList<Ticket> listTicket;
    private String day;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
