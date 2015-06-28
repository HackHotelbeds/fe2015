package trip.Services;

import org.json.simple.parser.ParseException;
import trip.parse.HotelbedsParser;
import trip.parse.TabParser;
import trip.pojo.Hotel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 27/06/2015.
 */
public class HotelbedsService {


    public List<Hotel> getHotelbedsHotels(String dateFrom, String dateTo, String paxes, String lat, String lon, int night, String dest) {
        String soapXml = createRossettaRequest(dateFrom, dateTo, paxes, lat, lon, dest);
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("http://testapi.hotelbeds.com/hotel-api/1.0/hotels");
            java.net.URLConnection conn = url.openConnection();
            conn.setRequestProperty("Api-Key", "676j97rtbv2sbnydhkxrfppt");
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setDoOutput(true);
            java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
            wr.write(soapXml);
            wr.flush();
            // Read the response
            java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "UTF8"));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            System.out.println(response);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        List<Hotel> services = new ArrayList<>();
        try {
            services = HotelbedsParser.parse(response.toString(), night);
        } catch (ParseException e) {
        } catch (IOException e) {
        }
        return services;
    }


    public String createRossettaRequest(String dateFrom, String dateTo, String paxes, String lat, String lon, String dest){

        String request= new String();
        request= request+ "<hotelListRQ xmlns=\"http://www.hotelbeds.com/schemas/messages\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
        request= request+ "\n<stay checkIn=\"" + dateFrom + "\" checkOut=\"" + dateTo + "\"/>";
        request= request+ "\n<occupancies>";
        request= request+ "\n<occupancy rooms=\"1\" adults=\"" + paxes + "\" children=\"0\"/>";
        request= request+ "\n</occupancies>";
        request= request+ "\n<destination code=\"" + dest + "\"/>";
        //request= request+ "\n<geolocation longitude=\"" + lon + "\" latitude=\"" + lat + "\" radius=\"10\" unit=\"km\"/>";
        request= request+ "\n<limit maxHotels=\"5\"/>";
        request= request+ "\n</hotelListRQ>";
        return request;
    }
}
