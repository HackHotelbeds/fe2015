package trip.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import trip.pojo.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Roger on 28/06/2015.
 */
public class TabParser {

    public static List<Ticket> parse(String jsonRs) throws org.json.simple.parser.ParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Map> fullRs = mapper.readValue(jsonRs, Map.class);
        ArrayList<ArrayList<String>> activites = (ArrayList<ArrayList<String>>)fullRs.get("activities");
        List<Ticket> services = new ArrayList<>();
        for (int i = 0; i < activites.size(); i++) {
            Map<String,String> activity = (Map<String,String>)activites.get(i);
            Object preuTmp = activity.get("adultPrice");
            String name = activity.get("name");
            Ticket ticket = new Ticket();
            ticket.setName(name);
            ticket.setPrice(preuTmp.toString());
            System.out.println(i + " " + name  + " " + preuTmp.toString());
            services.add(ticket);
        }
        return services;
    }
}
