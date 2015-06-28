package trip.parse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import trip.pojo.Consecionario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class ConsecionarioParse  {

    String tmpValue;
    List<Consecionario> listConsecionary= new ArrayList<Consecionario>();
    Consecionario consecionario;

    public List<Consecionario> getListConsecionary() {
        return listConsecionary;
    }

    public void setListConsecionary(List<Consecionario> listCar) {
        this.listConsecionary = listCar;
    }


    public void parseo(Document doc)  {

        NodeList nodes = doc.getElementsByTagName("VehVendorAvails");

        Element element = (Element) nodes.item(0);
        NodeList concesionariosList = element.getElementsByTagName("VehVendorAvail");
        for (int i = 0; i < concesionariosList.getLength(); i++) {
            Element consecionarioElement = (Element) concesionariosList.item(i);
            consecionario=new Consecionario();
            consecionario.setCityName(consecionarioElement.getElementsByTagName("CityName").item(0).getFirstChild().getTextContent());
            consecionario.setCountry(consecionarioElement.getElementsByTagName("CountryCode").item(0).getFirstChild().getTextContent());
            consecionario.setLocaltionCode(consecionarioElement.getElementsByTagName("LocationDetails").item(0).getAttributes().getNamedItem("LocationCode").getNodeValue());
            consecionario.setVendorCode(consecionarioElement.getElementsByTagName("Vendor").item(0).getAttributes().getNamedItem("Code").getNodeValue());
            listConsecionary.add(consecionario);
        }



    }
}
