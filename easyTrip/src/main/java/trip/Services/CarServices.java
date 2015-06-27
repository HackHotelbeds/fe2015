package trip.Services;

import trip.pojo.Car;
import trip.pojo.Consecionario;
import trip.parse.ConsecionarioParse;
import trip.parse.RentalCarParse;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class CarServices {

    public List<Car> getRentalCars(final String startAirport, final String finishAirpot,
                                   final String startDate, final String endDate, final int numberOfPassenger, final String restToken){

        ClassLoader classLoader = getClass().getClassLoader();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            InputStream xmlInput =classLoader.getResourceAsStream("CarsByAirportLocation.xml");

            SAXParser saxParser = factory.newSAXParser();
            ConsecionarioParse parse = new ConsecionarioParse();
            saxParser.parse(xmlInput, parse);
            //Lista de consecionarios del aeropuerto de salida
            List<Consecionario> listConsecionary=parse.getListConsecionary();
            //TODO Lista de consecinarios de la llegada y ver cuales se encuentran en los puntos de salida y llegada
            //para poder dejar el coche. Con ese listado de marcas en los dos aeropuertos se miran que coches ofrecen
            //para devolverlos

            InputStream xmlInputRentalCar =classLoader.getResourceAsStream("carAvailability.xml");
            RentalCarParse parseRentalCar= new RentalCarParse();
            saxParser.parse(xmlInputRentalCar,parseRentalCar);
            List<Car> listCar=parseRentalCar.getListCar();
            return listCar;

        } catch (Exception ex){

        }
        return null;
    }


    public String createRequestConsecionario(final String startAirport){
        String request= new String();
        request= request+ "<VehLocationListRQ Version=\"2.0.0\" xmlns=\"http://webservices.sabre.com/sabreXML/2011/10\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> ";
        request= request+ "\n<VehAvailRQCore>";
        request= request+ "\n<VehRentalCore>";
        request= request+ "\n<PickUpLocation LocationCode=\""+startAirport+"\"/>  ";
        request= request+ "\n</VehRentalCore>";
        request= request+ "\n</VehAvailRQCore>";
        request= request+ "\n</VehLocationListRQ>";
        return request;
    }
}
