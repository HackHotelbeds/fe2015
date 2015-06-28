package trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import trip.Services.CarServices;
import trip.pojo.Itinerary;
import trip.utils.Connection;
import trip.utils.UtilsParse;

@RestController
public class easyTripController {

    protected String applicationOnlyBearerToken;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private Connection connection=new Connection();

    CarServices carServices= new CarServices();

    @RequestMapping("/")
    public String isAlive() {
        return "Server UP";
    }

    @RequestMapping(value="/getItinerary",params = {"actualAirport", "startAirport", "endAirport", "startDate", "finishDate", "numPassenger"})
    @ResponseBody
    public String getItinerary(@RequestParam(value = "actualAirport") String actualAirport,
                               @RequestParam(value = "startAirport") String startAirport,
                               @RequestParam(value = "endAirport") String endAirport,
                               @RequestParam(value = "startDate") String startDate,
                               @RequestParam(value = "finishDate") String finishDate,
                               @RequestParam(value = "numPassenger") String passenger) {
        if((connection.getRestToken()==null || connection.getRestToken().equals("")) && (connection.getSoapToken()==null || connection.getSoapToken().equals(""))){
            connection.connectSabreAPI();
            try {
                connection.callloginSoap(connection.createSecurityRequest(),"https://sws3-crt.cert.sabre.com");
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex){
                return "connection problem";
            }

        }
        Itinerary itinerary= new Itinerary();
        itinerary.setListCar(carServices.getRentalCars("","","","",1,connection.getRestToken()));
        itinerary.setListCar(carServices.getRentalCars(startAirport,"",startDate,finishDate,1,connection));

        return new UtilsParse().convertObjectToJson(itinerary);
    }


}
