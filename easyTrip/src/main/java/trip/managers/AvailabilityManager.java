package trip.managers;

import trip.Services.CarServices;
import trip.Services.HotelbedsService;
import trip.Services.TabService;
import trip.pojo.*;
import trip.utils.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Roger on 27/06/2015.
 */
public class AvailabilityManager {

    List<Ticket> ticketServices = new ArrayList<>();
    List<Hotel> hotelServices = new ArrayList<>();

    public Itinerary manager() throws InterruptedException {
        //public Itinerary manager(String actualAirport,String startAirport,String endAirport,
        // String startDate,String finishDate,String passenger,String waypoints) {
        String actualAirport = "PMI";
        String startAirport= "BCN";
        String endAirport = "MAD";
        String startDate = "25082015";
        String finishDate= "30082015";
        String passenger = "1";
        //String waypoints = "1.123,9.987;2.369,7.741";
        String waypoints = "1.123,9.987";

        String[] waypointList = waypoints.split(";");
        int numbOfThreads = 7 + waypointList.length;





        ExecutorService executor = Executors.newFixedThreadPool(numbOfThreads);
        CompletionService<List<Car>> carCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Vuelo>> vueloIdaCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Vuelo>> vueloVueltaCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Hotel>> hotelCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Hotel>> hotelbedsCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Ticket>> ticketCompService = new ExecutorCompletionService<>(executor);
        CompletionService<List<Ticket>> tabCompService = new ExecutorCompletionService<>(executor);

        CarTask carTask = new CarTask("","","","",1,"");
        //TODO FIXME
        CarTask carTask = new CarTask("","","","",1,null);
        VueloIdaTask vueloIdaTask = new VueloIdaTask();
        VueloVueltaTask vueloVueltaTask = new VueloVueltaTask();
        carCompService.submit(carTask);
        vueloIdaCompService.submit(vueloIdaTask);
        vueloVueltaCompService.submit(vueloVueltaTask);
        HotelbedsTask hotelbedsStartTask = new HotelbedsTask(startAirport);
        hotelbedsCompService.submit(hotelbedsStartTask);
        HotelbedsTask hotelbedsEndTask = new HotelbedsTask(endAirport);
        hotelbedsCompService.submit(hotelbedsEndTask);

        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            HotelTask hotelTask = new HotelTask();
            hotelCompService.submit(hotelTask);
        }

        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            TicketTask ticketTask = new TicketTask();
            ticketCompService.submit(ticketTask);
        }
        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            TabTask tabTask = new TabTask();
            tabCompService.submit(tabTask);
        }

        carCompService.take();
        vueloIdaCompService.take();
        vueloVueltaCompService.take();
        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            hotelCompService.take();
        }
        for(int executingThreads = 0; executingThreads < 2; executingThreads++) {
            hotelbedsCompService.take();
        }
        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            ticketCompService.take();
        }
        for(int executingThreads = 0; executingThreads < waypointList.length; executingThreads++) {
            tabCompService.take();
        }
        executor.shutdown();


        //TODO
        return null;
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
            return carServices.getRentalCars(startAirport, finishAirpot, startDate, endDate, numberOfPassenger, connection);
        }
    }




    private final class VueloIdaTask implements Callable<List<Vuelo>> {
        //CarServices carServices;

        VueloIdaTask(){
            //carServices = new CarServices();
        }

        @Override public List<Vuelo> call() throws Exception {
            return null;
        }
    }


    private final class VueloVueltaTask implements Callable<List<Vuelo>> {
        //CarServices carServices;

        VueloVueltaTask(){
            //carServices = new CarServices();
        }

        @Override public List<Vuelo> call() throws Exception {
            return null;
        }
    }

    private final class HotelTask implements Callable<List<Hotel>> {
        //CarServices carServices;

        HotelTask(){
            //carServices = new CarServices();
        }

        @Override public List<Hotel> call() throws Exception {
            return null;
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

        HotelbedsTask(String pDest){
            hotelbedsService = new HotelbedsService();
            dest = pDest;
        }

        @Override public List<Hotel> call() throws Exception {
            //FIXME DUMMY DATA
            List<Hotel> services =  hotelbedsService.getHotelbedsHotels("2015-09-19","2015-09-20","1","2.646633999999949","39.57119", 1, dest);
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

}
