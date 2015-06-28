package trip.parse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.Hotel;
import trip.pojo.HotelOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Roger on 28/06/2015.
 */
public class HotelParser {

    private Hotel service;
    private HotelOptions hotelOptions = new HotelOptions();
    private ArrayList<Hotel> listHotel=new ArrayList<Hotel>();



       /* if (qName.equalsIgnoreCase("AvailabilityOption")) {
            service = new Hotel();
            service.setLon(attributes.getValue("Longitude"));
            service.setLat(attributes.getValue("Latitude"));
            service.setCompany("SABRE");
            service.setName(attributes.getValue("HotelName"));
            service.setCode(attributes.getValue("HotelCode"));
        } else if (qName.equalsIgnoreCase("RateRange")) {
            service.setCurrency(attributes.getValue("CurrencyCode"));
            service.setPrice(attributes.getValue("Min"));
        }

        if (qName.equals("AvailabilityOption")) {
            if (hotelOptions.getListHotel() == null) {
                hotelOptions.setListHotel(new ArrayList<>());
            }
            hotelOptions.getListHotel().add(service);
        }*/

    public void parse(Document doc, int nights)  {

        NodeList nodes = doc.getElementsByTagName("BasicPropertyInfo");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(0);
            service=new Hotel();
            service.setLat(element.getAttributes().getNamedItem("Latitude").getNodeValue());
            service.setLon(element.getAttributes().getNamedItem("Longitude").getNodeValue());
            service.setCompany("SABRE");
            service.setName(element.getAttributes().getNamedItem("HotelName").getNodeValue());
            service.setCode(element.getAttributes().getNamedItem("HotelCode").getNodeValue());
            service.setRate(doc.getElementsByTagName("HotelRateCode").item(0).getFirstChild().getNodeValue());
            service.setRate("RAC");
            service.setPrice("USD");
            service.setPrice(String.valueOf(Math.round(new Double(Math.random()*5010))));
            service.setNight(nights);
            listHotel.add(service);
        }


    }

    public ArrayList<Hotel> getListHotel() {
        return listHotel;
    }

}
