package trip.parse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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



    public void parseo(Document doc)  {

        NodeList nodes = doc.getElementsByTagName("Makes");

        Element element = (Element) nodes.item(0);
        NodeList cardetailsList = element.getElementsByTagName("MakeModel");
        for (int i = 0; i < cardetailsList.getLength(); i++) {
            Element carDetailsElement = (Element) cardetailsList.item(i);
            carDetails=new CarDetails();
            carDetails.setCarName(carDetailsElement.getAttributes().getNamedItem("Example").getNodeValue());
            carDetails.setCarType(carDetailsElement.getAttributes().getNamedItem("Type").getNodeValue());
            carDetails.setCapacity(carDetailsElement.getAttributes().getNamedItem("Capacity").getNodeValue());

            listCarDetails.add(carDetails);
        }



    }
}
