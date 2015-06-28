package trip.managers;

import trip.Services.*;
import trip.pojo.*;
import trip.utils.Connection;
import trip.utils.UtilsParse;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Roger on 27/06/2015.
 */
public class AvailabilityManager {

    List<Ticket> ticketServices = new ArrayList<>();
    List<Hotel> hotelServices = new ArrayList<>();
    List<Car> listCar = null;

    private Connection connection=new Connection();

    public String manager(Entrada entrada) throws InterruptedException, ParseException {

        StepoverElement[] waypoints = entrada.getStepovers();
        int numbOfThreads = waypoints.length;


        if((connection.getRestToken()==null || connection.getRestToken().equals("")) && (connection.getSoapToken()==null || connection.getSoapToken().equals(""))){
            connection.connectSabreAPI();
            try {
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex){
                return "connection problem";
            }

        }

        StepoverElement[] stepower= entrada.getStepovers();

        ExecutorService executor = Executors.newFixedThreadPool(numbOfThreads);
        CompletionService<List<Car>> carCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Vuelo>> vueloIdaCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Vuelo>> vueloVueltaCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Hotel>> hotelCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Hotel>> hotelbedsCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Ticket>> ticketCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Ticket>> tabCompService = new ExecutorCompletionService<>(executor);

        CarTask carTask = new CarTask(entrada.getOriginAirport().getIata(),entrada.getDestinationAirport().getIata(),
                entrada.getStartDate(),entrada.getEndDate(),Integer.valueOf(entrada.getPaxes()),connection);

        VueloIdaTask vueloIdaTask = new VueloIdaTask(entrada.getClosestAirport().getIata(), entrada.getOriginAirport().getIata(),
        entrada.getStartDate(), entrada.getEndDate(), Integer.valueOf(entrada.getPaxes()), connection);
        VueloVueltaTask vueloVueltaTask = new VueloVueltaTask(entrada.getDestinationAirport().getIata(), entrada.getClosestAirport().getIata(),
        entrada.getStartDate(), entrada.getEndDate(), Integer.valueOf(entrada.getPaxes()), connection);
        carCompService.submit(carTask);
        vueloIdaCompService.submit(vueloIdaTask);
        vueloVueltaCompService.submit(vueloVueltaTask);


        HotelbedsTask hotelbedsStartTask = new HotelbedsTask(entrada.getStartDate(), entrada.getEndDate(), entrada.getPaxes(),
                entrada.getDestinationAirport().getLat().toString(), entrada.getDestinationAirport().getLng().toString(), 1, entrada.getOriginAirport().getIata());
        hotelbedsCompService.submit(hotelbedsStartTask);

        int hotelId = 1;
        //FIXME
        // HotelTask hotelTask = new HotelTask(entrada.getStartDate(), dateTo, entrada.getPaxes(), entrada.getOriginAirport().getLat(),
        //entrada.getOriginAirport().getLng(), connection, hotelId);
        HotelTask hotelTask = new HotelTask(entrada.getStartDate(), "", entrada.getPaxes(), entrada.getOriginAirport().getLat().toString(),
                entrada.getOriginAirport().getLng().toString(), connection, hotelId);
        hotelCompService.submit(hotelTask);
        hotelId++;

        String dateFrom=entrada.getStartDate();
        String dateTo= entrada.getEndDate();
        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            //FIXME HotelTask hotelTask2 = new HotelTask(dateFrom, dateTo, entrada.getPaxes(), lat, lon, connection, hotelId);
            StepoverElement stepowerElement= stepower[executingThreads];
            Calendar c1 = GregorianCalendar.getInstance();
            Calendar c2 = GregorianCalendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateFromNew= sdf.parse(dateFrom);
            c1.setTimeInMillis(dateFromNew.getTime());
            c1.add(Calendar.DATE,1);
            dateFromNew.setTime(c1.getTimeInMillis());
            Date dateToNew= sdf.parse(dateFrom);
            c1.setTimeInMillis(dateFromNew.getTime());
            c1.add(Calendar.DATE,1);
            dateToNew.setTime(c1.getTimeInMillis());
            dateFrom=sdf.format(dateFromNew);

            HotelTask hotelTask2 = new HotelTask(dateFrom, sdf.format(dateToNew), entrada.getPaxes(), stepowerElement.getLat().toString(), stepowerElement.getLng().toString(), connection, hotelId);
            hotelCompService.submit(hotelTask2);
            hotelId++;

        }



        //FIXME HotelTask hotelTask3 = new HotelTask(dateFrom, dateTo, entrada.getPaxes(), lat, lon, connection, hotelId);
        HotelTask hotelTask3 = new HotelTask(dateFrom,"", entrada.getPaxes(), entrada.getDestinationAirport().getLat().toString(), entrada.getDestinationAirport().getLng().toString(), connection, hotelId);
        hotelCompService.submit(hotelTask3);

        HotelbedsTask hotelbedsEndTask = new HotelbedsTask(dateFrom,dateTo, entrada.getPaxes(),
                entrada.getDestinationAirport().getLat().toString(), entrada.getDestinationAirport().getLng().toString(), hotelId, entrada.getOriginAirport().getIata());
        hotelbedsCompService.submit(hotelbedsEndTask);

        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            TicketTask ticketTask = new TicketTask();
            ticketCompService.submit(ticketTask);
        }
        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            TabTask tabTask = new TabTask();
            tabCompService.submit(tabTask);
        }

        carCompService.take();
        vueloIdaCompService.take();
        vueloVueltaCompService.take();
        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            hotelCompService.take();
        }
        for(int executingThreads = 0; executingThreads < 2; executingThreads++) {
            hotelbedsCompService.take();
        }
        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            ticketCompService.take();
        }
        for(int executingThreads = 0; executingThreads < numbOfThreads; executingThreads++) {
            tabCompService.take();
        }
        executor.shutdown();


        Itinerary itinerary = new Itinerary();


        if (itinerary.getTicketOptionDays() == null) {
            itinerary.setTicketOptionDays(new ArrayList<>());
        }
        for (Ticket ticket: ticketServices) {
            int night = ticket.getNight();
            boolean found = false;
            for (int i = 0; i < itinerary.getTicketOptionDays().size() && !found; i++) {
                TicketOptions ticketOptions = itinerary.getTicketOptionDays().get(i);
                if (ticketOptions.getDay() == night) {
                    if (ticketOptions.getListTicket() == null){
                        ticketOptions.setListTicket(new ArrayList<>());
                    }
                    itinerary.getTicketOptionDays().remove(ticketOptions);
                    ticketOptions.getListTicket().add(ticket);
                    itinerary.getTicketOptionDays().add(ticketOptions);
                    found = true;
                }
            }
            if (!found) {
                TicketOptions ticketOptions = new TicketOptions();
                ticketOptions.setDay(night);
                ticketOptions.setListTicket(new ArrayList<>());
                ticketOptions.getListTicket().add(ticket);
                itinerary.getTicketOptionDays().add(ticketOptions);
                found = true;
            }
        }

        if (itinerary.getHotelOptionDays() == null) {
            itinerary.setHotelOptionDays(new ArrayList<>());
        }
        for (Hotel hotel: hotelServices) {
            int night = hotel.getNight();
            boolean found = false;
            for (int i = 0; i < itinerary.getHotelOptionDays().size() && !found; i++) {
                HotelOptions hotelOptions = itinerary.getHotelOptionDays().get(i);
                if (hotelOptions.getDay() == night) {
                    if (hotelOptions.getListHotel() == null){
                        hotelOptions.setListHotel(new ArrayList<>());
                    }
                    itinerary.getHotelOptionDays().remove(hotelOptions);
                    hotelOptions.getListHotel().add(hotel);
                    itinerary.getHotelOptionDays().add(hotelOptions);
                    found = true;
                }
            }
            if (!found) {
                HotelOptions hotelOptions = new HotelOptions();
                hotelOptions.setDay(night);
                hotelOptions.setListHotel(new ArrayList<>());
                hotelOptions.getListHotel().add(hotel);
                itinerary.getHotelOptionDays().add(hotelOptions);
                found = true;
            }
        }

        itinerary.setListCar(listCar);

        return new UtilsParse().convertObjectToJson(itinerary);
    }

    private final class CarTask implements Callable<List<Car>> {
        CarServices carServices;
        String startAirport;
        String finishAirpot;
        String startDate;
        String endDate;
        Connection connection;
        int numberOfPassenger;

        CarTask(final String pStartAirport, final String pFinishAirpot,
                final String pStartDate, final String pEndDate, final int pNumberOfPassenger,
                final Connection pConnection){
            startAirport = pStartAirport;
            finishAirpot = pFinishAirpot;
            startDate = pStartDate;
            endDate = pEndDate;
            numberOfPassenger = pNumberOfPassenger;
            connection = pConnection;
            carServices = new CarServices();
        }

        @Override public List<Car> call() throws Exception {
            listCar = carServices.getRentalCars(startAirport, finishAirpot, startDate, endDate, numberOfPassenger, connection);
            return listCar;
        }
    }




    private final class VueloIdaTask implements Callable<List<Vuelo>> {
        FlightService flightService;
        String nearAirport;
        String startAirport;
        String startDate;
        String endDate;
        int numberOfPassenger;
        Connection connection;

        VueloIdaTask(final String pnearAirport, final String pstartAirport,
                     final String pstartDate, final String pendDate, final int pnumberOfPassenger, final Connection pconnection){
            flightService = new FlightService();
            nearAirport = pnearAirport;
            startAirport = pstartAirport;
            startDate = pstartDate;
            endDate = pendDate;
            numberOfPassenger = pnumberOfPassenger;
            connection = pconnection;
        }

        @Override public List<Vuelo> call() throws Exception {
            FlightService flightService = new FlightService();
            return flightService.getVueloIda(nearAirport, startAirport, startDate, endDate, numberOfPassenger, connection);
        }
    }


    private final class VueloVueltaTask implements Callable<List<Vuelo>> {
        FlightService flightService;
        String finishAirport;
        String nearAirport;
        String startDate;
        String endDate;
        int numberOfPassenger;
        Connection connection;

        VueloVueltaTask(final String pfinishAirport, final String pnearAirport,
                        final String pstartDate, final String pendDate, final int pnumberOfPassenger, final Connection pconnection){
            flightService = new FlightService();
            finishAirport = pfinishAirport;
            nearAirport = pnearAirport;
            startDate=pstartDate;
            endDate=pendDate;
            numberOfPassenger=pnumberOfPassenger;
            connection=pconnection;
        }

        @Override public List<Vuelo> call() throws Exception {
            FlightService flightService = new FlightService();
            return flightService.getVueloVuelta(finishAirport, nearAirport, startDate, endDate, numberOfPassenger, connection);
        }
    }

    private final class HotelTask implements Callable<List<Hotel>> {
        HotelServices hotelServices;
        String dateFrom;
        String dateTo;
        String paxes;
        String lat;
        String lon;
        Connection connection;
        int night;

        HotelTask(String pdateFrom, String pdateTo, String ppaxes, String plat, String plon, Connection pconnection, int pnight){
            hotelServices = new HotelServices();
            dateFrom = pdateFrom;
            dateTo = pdateTo;
            paxes = ppaxes;
            lat = plat;
            lon = plon;
            connection = pconnection;
            night = pnight;
        }

        @Override
        public List<Hotel> call() throws Exception {
            return hotelServices.getHotels(dateFrom, dateTo, paxes, lat, lon, connection, night);
        }
    }

    private final class TicketTask implements Callable<List<Ticket>> {
        //CarServices carServices;

        TicketTask(){
            //carServices = new CarServices();
        }

        @Override public List<Ticket> call() throws Exception {
            return null;
        }
    }


    private final class HotelbedsTask implements Callable<List<Hotel>> {
        HotelbedsService hotelbedsService;
        String dest;
        String dateFrom;
        String dateTo;
        String paxes;
        String lat;
        String lon;
        int night;

        HotelbedsTask(String pdateFrom, String pdateTo, String ppaxes, String plat, String plon, int pnight, String pdest){
            hotelbedsService = new HotelbedsService();
            dest = pdest;
            dateFrom=pdateFrom;
            dateTo=pdateTo;
            paxes=ppaxes;
            lat=plat;
            lon=plon;
            night=pnight;
        }

        @Override public List<Hotel> call() throws Exception {
            List<Hotel> services =  hotelbedsService.getHotelbedsHotels(dateFrom, dateTo, paxes, lat, lon, night, dest);
            for (int i = 0; i < services.size(); i++) {
                hotelServices.add(services.get(i));
            }
            return null;
        }
    }


    private final class TabTask implements Callable<List<Ticket>> {
        TabService tabService;

        TabTask(){
            tabService = new TabService();
        }

        @Override
        public List<Ticket> call() throws Exception {
            //FIXME DUMMY DATA
            List<Ticket> services = tabService.getTabTickets("2015-09-19","2015-09-20","2.646633999999949","39.57119", 1);
            for (int i = 0; i < services.size(); i++) {
                ticketServices.add(services.get(i));
            }
            return null;
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, x-requested-by");
    }



}
