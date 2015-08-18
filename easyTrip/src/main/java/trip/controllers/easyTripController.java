package trip.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;
import trip.Services.CarServices;
import trip.managers.AvailabilityManager;
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
import java.net.URLDecoder;

@RestController
public class easyTripController {

    @RequestMapping("/api")
    public String isAlive(HttpServletRequest request, HttpServletResponse response) {
        addCorsHeaders(response);
        return "Server UP";
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, x-requested-by");
    }

    @RequestMapping(value="/api/getItinerary")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseBody
    public String getItinerary(@RequestBody final String inputJsonObj, HttpServletRequest request, HttpServletResponse response) {
        String decodedJson = URLDecoder.decode(inputJsonObj);
        Gson gs = new Gson();
        Entrada obj = gs.fromJson(decodedJson, Entrada.class);
        addCorsHeaders(response);
        AvailabilityManager availabilityManager = new AvailabilityManager();
        try {
            return availabilityManager.manager(obj);
        } catch (Exception e) {
            return "connection problem";
        }
    }


}
