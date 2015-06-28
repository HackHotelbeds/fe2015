package trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import trip.Services.CarServices;
import trip.pojo.Entrada;
import trip.pojo.Itinerary;
import trip.utils.Connection;
import trip.utils.UtilsParse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@RestController
public class easyTripController {

    protected String applicationOnlyBearerToken;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private Connection connection=new Connection();

    CarServices carServices= new CarServices();

    @RequestMapping("/")
    public String isAlive(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        return "Server UP";
    }

    @RequestMapping(value="/getItinerary")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseBody
    public String getItinerary(@RequestBody final String inputJsonObj, HttpServletRequest request, HttpServletResponse response) {

        Gson gs = new Gson();
        Entrada obj = gs.fromJson(inputJsonObj, Entrada.class);


        if((connection.getRestToken()==null || connection.getRestToken().equals("")) && (connection.getSoapToken()==null || connection.getSoapToken().equals(""))){
            connection.connectSabreAPI();
            try {
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex){
                return "connection problem";
            }

        }
        Itinerary itinerary= new Itinerary();
        itinerary.setListCar(carServices.getRentalCars(obj.getOriginAirport().getIata(),obj.getDestinationAirport().getIata(),obj.getStartDate(),obj.getEndDate(),Integer.valueOf(obj.getPaxes()),connection));

        response.addHeader("Access-Control-Allow-Origin", "*");
        return new UtilsParse().convertObjectToJson(itinerary);
    }


}
