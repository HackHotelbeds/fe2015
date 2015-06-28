package trip.Services;

import trip.pojo.Car;
import trip.pojo.Vuelo;
import trip.utils.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibram on 28/06/2015.
 */
public class FlightServices {

    public List<Vuelo> getVueloIda(final String nearAirport, final String startAirport,
                                   final String startDate, final String endDate, final int numberOfPassenger, final Connection connection){
        String formatStartDate= startDate.substring(4,8)+"-"+startDate.substring(2,4)+"-"+startDate.substring(0,2);
        String formatEndDate= endDate.substring(4,8)+"-"+endDate.substring(2,4)+"-"+endDate.substring(0,2);

        String url="https://api.test.sabre.com/v1/shop/flights?origin="+nearAirport+"&destination="+startAirport+"&departuredate="+formatStartDate+"&returndate="+formatEndDate+"" +
                "&onlineitinerariesonly=N&limit=10&offset=1&eticketsonly=N&sortby=totalfare&order=asc&sortby2=departuretime&order2=asc&pointofsalecountry=US";

        //String result=connection.sendRequest(url,connection.getRestToken());
        ArrayList<Vuelo> listVuelos= new ArrayList<Vuelo>();
        for (int i=0;i<3;i++){
            Vuelo vuelo= new Vuelo();
            vuelo.setFlightNumber("AB7"+ String.valueOf(Math.random() * 100));
            vuelo.setPrice(String.valueOf(Math.random()*1000*134.2));
            vuelo.setArrivalHour("14:00") ;
            vuelo.setArrivalDate(startDate);
            vuelo.setDepartureHour("10:00");
            vuelo.setDepartureDate(startDate);
            vuelo.setStartAirport(nearAirport);
            vuelo.setEndAirport(startAirport);
            listVuelos.add(vuelo);
        }
        return listVuelos;

    }

    public List<Vuelo> getVueloVuelta(final String finishAirport, final String nearAirport,
                                   final String startDate, final String endDate, final int numberOfPassenger, final Connection connection){
        String formatStartDate= startDate.substring(4,8)+"-"+startDate.substring(2,4)+"-"+startDate.substring(0,2);
        String formatEndDate= endDate.substring(4,8)+"-"+endDate.substring(2,4)+"-"+endDate.substring(0,2);

        String url="https://api.test.sabre.com/v1/shop/flights?origin="+finishAirport+"&destination="+nearAirport+"&departuredate="+formatStartDate+"&returndate="+formatEndDate+"" +
                "&onlineitinerariesonly=N&limit=10&offset=1&eticketsonly=N&sortby=totalfare&order=asc&sortby2=departuretime&order2=asc&pointofsalecountry=US";

        //String result=connection.sendRequest(url,connection.getRestToken());
        ArrayList<Vuelo> listVuelos= new ArrayList<Vuelo>();
        for (int i=0;i<3;i++){
            Vuelo vuelo= new Vuelo();
            vuelo.setFlightNumber("XZ5"+ String.valueOf(Math.random()*100));
            vuelo.setPrice(String.valueOf(Math.random()*1000*134.2));
            vuelo.setArrivalHour("23:00") ;
            vuelo.setArrivalDate(startDate);
            vuelo.setDepartureHour("15:00");
            vuelo.setDepartureDate(finishAirport);
            vuelo.setStartAirport(nearAirport);
            vuelo.setEndAirport(nearAirport);
            listVuelos.add(vuelo);
        }
        return listVuelos;

    }
}
