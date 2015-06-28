package trip.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import trip.pojo.Hotel;
import trip.pojo.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roger on 28/06/2015.
 */
public class TicketParser {

    public static List<Ticket> parse(String jsonRs,int night) throws org.json.simple.parser.ParseException, IOException {
        List<Ticket> services = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Map> fullRs = mapper.readValue(jsonRs, Map.class);
        ArrayList<ArrayList<String>> tickets = (ArrayList<ArrayList<String>>)fullRs.get("data").get("tours");

        for (int i = 0; i < tickets.size(); i++) {
            Map<String,Object> activity = (Map<String,Object>)tickets.get(i);
            Ticket ticket = new Ticket();
            ticket.setCompany("GETYOURGUIDE");
            ticket.setNight(night);
            ticket.setName(activity.get("title").toString());
            LinkedHashMap<Object,Object> pricesArr = (LinkedHashMap<Object,Object>)activity.get("price");
            LinkedHashMap<Object,Object> priceMap = (LinkedHashMap<Object,Object>)pricesArr.get("values");
            ticket.setPrice(priceMap.get("amount").toString());
            services.add(ticket);
        }


        return services;
    }
}
