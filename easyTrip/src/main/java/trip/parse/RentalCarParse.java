package trip.parse;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import trip.pojo.Car;
import trip.pojo.Consecionario;

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

    public void parseo(Document doc)  {

        NodeList nodes = doc.getElementsByTagName("VehVendorAvails");

        Element element = (Element) nodes.item(0);
        NodeList carlIST = element.getElementsByTagName("VehVendorAvail");
        for (int i = 0; i < carlIST.getLength(); i++) {
            Element carElement = (Element) carlIST.item(i);
            car=new Car();
            car.setPrice(carElement.getElementsByTagName("TotalCharge").item(0).getAttributes().getNamedItem("Amount").getNodeValue());
            car.setCurrency(carElement.getElementsByTagName("VehicleCharge").item(0).getAttributes().getNamedItem("CurrencyCode").getNodeValue());
            car.setCarCode(carElement.getElementsByTagName("VehType").item(0).getFirstChild().getTextContent());
            listCar.add(car);
        }



    }

}
