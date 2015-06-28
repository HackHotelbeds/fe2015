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
public class HotelbedsParser {
    public static List<Hotel> parse(String jsonRs,int night) throws org.json.simple.parser.ParseException, IOException {
        List<Hotel> services = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Map> fullRs = mapper.readValue(jsonRs, Map.class);
        ArrayList<ArrayList<String>> hotels = (ArrayList<ArrayList<String>>)fullRs.get("hotels").get("hotels");
        for (int i = 0; i < hotels.size(); i++) {
            Map<String,Object> activity = (Map<String,Object>)hotels.get(i);
            String name = activity.get("name").toString();
            String code = activity.get("code").toString();
            String category = activity.get("category").toString();
            String currency = activity.get("currency").toString();
            String lat = activity.get("latitude").toString();
            String lon = activity.get("longitude").toString();
            Hotel hotel = new Hotel();
            hotel.setName(name);
            hotel.setCode(code);
            hotel.setCategory(category);
            hotel.setCurrency(currency);
            hotel.setLat(lat);
            hotel.setLon(lon);
            hotel.setCompany("HOTELBEDS");
            hotel.setNight(night);

            ArrayList<Map> rooms = (ArrayList<Map>)activity.get("rooms");
            String roomType = rooms.get(0).get("code").toString();
            hotel.setRoomType(roomType);
            ArrayList<LinkedHashMap<Object,Object>> pricesArr = (ArrayList<LinkedHashMap<Object,Object>>)rooms.get(0).get("prices");
            LinkedHashMap<Object,Object> prices = pricesArr.get(0);
            String price = prices.get("net").toString();
            hotel.setPrice(price);
            services.add(hotel);
        }
        return services;
    }
}
