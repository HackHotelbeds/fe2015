package trip.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by tbarderas on 18/08/2015.
 */
@Controller
public class viewsController {

    @RequestMapping("/")
    public String landing(Map<String, Object> model) {
        model.put("pageTitle", "Trips made easy");
        model.put("customJsList", Arrays.asList("landing"));
        model.put("customCssList", Arrays.asList("landing", "rotating-background"));

        return "landing";
    }
}
