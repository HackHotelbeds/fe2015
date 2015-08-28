package trip.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by tbarderas on 18/08/2015.
 */
@Controller
public class viewsController {

    @RequestMapping("/")
    public String landing(final Map<String, Object> model) {
        model.put("pageTitle", "Trips made easy");

        model.put("maxPax", 7);
        model.put("numBackground", 12);

        model.put("customCssList", Arrays.asList("landing", "rotating-background"));
        model.put("customJsList", Arrays.asList("landing", "jquery-ui", "jquery.cycle.all", "jquery.geocomplete"));

        return "landing/index";
    }

    @RequestMapping("/routeMap")
    public String mapView(
            @RequestParam("origin") String origin,
            @RequestParam("start-date") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam("destination") String destination,
            @RequestParam("end-date") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
            @RequestParam("paxes") int paxes,
            final Map<String, Object> model
    ) {
        model.put("pageTitle", "Hands on map");

        model.put("customCssList", Arrays.asList("mapView"));
        model.put("customJsList", Arrays.asList("ol", "jquery-ui", "mapView", "mapLogic", "bootbox.min", "jquery.blockUI"));

        return "mapView/index";
    }

    @RequestMapping("/tripVoucher")
    public String tripVoucher(
            final Map<String, Object> model
    ) {
        model.put("pageTitle", "Congratulations! This is your trip");

        model.put("customCssList", Arrays.asList("voucher"));
        model.put("customJsList", Arrays.asList("tripVoucher"));

        return "tripVoucher/index";
    }
}
