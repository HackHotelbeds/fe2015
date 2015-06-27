package trip.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.Consecionario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class ConsecionarioParse extends DefaultHandler {

    String tmpValue;
    List<Consecionario> listConsecionary= new ArrayList<Consecionario>();
    Consecionario consecionario;

    public List<Consecionario> getListConsecionary() {
        return listConsecionary;
    }

    public void setListConsecionary(List<Consecionario> listCar) {
        this.listConsecionary = listCar;
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {


        if("VehVendorAvail".equals(qName)){
            consecionario = new Consecionario();
        }
        if("LocationDetails".equals(qName)){
            if(attributes.getValue("UnitOfMeasure").equals("MI")) {
                consecionario.setDistanceMI(attributes.getValue("Distance"));
            }
            if(attributes.getValue("UnitOfMeasure").equals("KM")) {
                consecionario.setDistanceMI(attributes.getValue("Distance"));
            }

            consecionario.setLocaltionCode(attributes.getValue("LocationCode"));
            consecionario.setLocationName(attributes.getValue("LocationName"));
        }
        if("Address".equals(qName)){
            consecionario.setPositionX(attributes.getValue("Latitude"));
            consecionario.setPositionY(attributes.getValue("Longitude"));
        }
        if("Vendor".equals(qName)){
            consecionario.setVendorCode(attributes.getValue("Code"));
            consecionario.setVendorShortName(attributes.getValue("CompanyShortName"));
        }



    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {



        if("CityName".equals(qName)) {
            consecionario.setCityName(tmpValue);
        }

        if("VehVendorAvail".equals(qName)){
            listConsecionary.add(consecionario);
        }

        if("CountryCode".equals(qName)){
            consecionario.setCountry(tmpValue);
        }

        if("StreetNmbr".equals(qName)){
            consecionario.setStreetInfo(consecionario.getStreetInfo() + " " + tmpValue);
        }


    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        tmpValue = new String(ch, start, length).trim();


    }
}
