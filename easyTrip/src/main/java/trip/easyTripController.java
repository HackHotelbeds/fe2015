package trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import trip.Services.CarServices;
import trip.pojo.Itinerary;
import trip.utils.UtilsParse;

@RestController
public class easyTripController {

    protected String applicationOnlyBearerToken;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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
        Itinerary itinerary= new Itinerary();
        itinerary.setListCar(carServices.getRentalCars("","","","",1));

        return new UtilsParse().convertObjectToJson(itinerary);
    }


}
