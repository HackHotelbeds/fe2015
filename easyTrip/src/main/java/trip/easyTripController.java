package trip;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class easyTripController {

    @RequestMapping("/")
    public String isAlive() {
        return "Server UP";
    }

}
