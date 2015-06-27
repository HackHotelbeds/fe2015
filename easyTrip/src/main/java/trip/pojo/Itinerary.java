package trip.pojo;

import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class Itinerary {
    List<Vuelo> ida;
    List<Car> listCar;
    List<HotelOptions> hotelOptionDays;
    List<TicketOptions> ticketOptionDays;
    List<Vuelo> vuelta;

    public List<Vuelo> getIda() {
        return ida;
    }

    public void setIda(List<Vuelo> ida) {
        this.ida = ida;
    }

    public List<Car> getListCar() {
        return listCar;
    }

    public void setListCar(List<Car> listCar) {
        this.listCar = listCar;
    }

    public List<HotelOptions> getHotelOptionDays() {
        return hotelOptionDays;
    }

    public void setHotelOptionDays(List<HotelOptions> hotelOptionDays) {
        this.hotelOptionDays = hotelOptionDays;
    }

    public List<TicketOptions> getTicketOptionDays() {
        return ticketOptionDays;
    }

    public void setTicketOptionDays(List<TicketOptions> ticketOptionDays) {
        this.ticketOptionDays = ticketOptionDays;
    }

    public List<Vuelo> getVuelta() {
        return vuelta;
    }

    public void setVuelta(List<Vuelo> vuelta) {
        this.vuelta = vuelta;
    }
}
