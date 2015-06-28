package trip.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.Hotel;
import trip.pojo.HotelOptions;

import java.util.ArrayList;

/**
 * Created by Roger on 28/06/2015.
 */
public class HotelParser extends DefaultHandler {

    private String tempVal;
    private Hotel service;
    private HotelOptions hotelOptions = new HotelOptions();

    public void startElement(String uri, String localName,String qName,
                             Attributes attributes) throws SAXException {
        tempVal = "";

        if (qName.equalsIgnoreCase("AvailabilityOption")) {
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

    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (qName.equals("AvailabilityOption")) {
            if (hotelOptions.getListHotel() == null) {
                hotelOptions.setListHotel(new ArrayList<>());
            }
            hotelOptions.getListHotel().add(service);
        }

        tempVal = "";
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }

}
