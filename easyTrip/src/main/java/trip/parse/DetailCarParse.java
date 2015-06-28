package trip.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.CarDetails;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Ibram on 28/06/2015.
 */
public class DetailCarParse extends DefaultHandler {

    String tmpValue;
    List<CarDetails> listCarDetails= new ArrayList<CarDetails>();
    CarDetails carDetails=new CarDetails();

    public List<CarDetails> getListCarDetails() {
        return listCarDetails;
    }

    public void setListCarDetails(List<CarDetails> listCar) {
        this.listCarDetails = listCar;
    }



    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {


        if("MakeModel".equals(qName)){
            carDetails = new CarDetails();
            carDetails.setCapacity(attributes.getValue("Capacity"));
            carDetails.setCarName(attributes.getValue("Example"));
            carDetails.setCarType(attributes.getValue("Type"));
            listCarDetails.add(carDetails);
        }




    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {






    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        tmpValue = new String(ch, start, length).trim();


    }
}
