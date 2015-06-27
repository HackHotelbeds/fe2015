package trip.parse;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.Car;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Ibram on 27/06/2015.
 */
public class RentalCarParse extends DefaultHandler {
    String tmpValue;
    List<Car> listCar= new ArrayList<Car>();
    Car car;
    boolean add=false;

    public List<Car> getListCar() {
        return listCar;
    }

    public void setListCar(List<Car> listCar) {
        this.listCar = listCar;
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {


        if("VehVendorAvail".equals(qName)){
            car = new Car();
        }
        if("RentalRate".equals(qName)){
            if("S".equals(attributes.getValue("AvailabilityStatus"))){
                add=true;
            }
            car.setRateCode(attributes.getValue("RateCode"));
        }
        if("VehicleCharge".equals(qName)){
            car.setGaranty(attributes.getValue("GuaranteeInd"));
        }
        /*if("Day".equals(qName)){
            car.setAddionalDate(attributes.getValue("Rate"));
        }*/
        if("TotalCharge".equals(qName)){
            car.setPrice(attributes.getValue("Amount"));
        }
        if("Vendor".equals(qName)){
            car.setParticipationLevel(attributes.getValue("ParticipationLevel"));
        }




    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {



        if("VehType".equals(qName)) {
            car.setCarCode(tmpValue);
        }
        if("VehVendorAvail".equals(qName)){
            listCar.add(car);
        }



    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        tmpValue = new String(ch, start, length).trim();


    }

}
