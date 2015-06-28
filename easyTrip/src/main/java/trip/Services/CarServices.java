package trip.Services;


import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import trip.parse.ConsecionarioParse;
import trip.parse.DetailCarParse;
import trip.parse.RentalCarParse;
import trip.pojo.Car;
import trip.pojo.CarDetails;
import trip.pojo.Consecionario;
import trip.pojo.Itinerary;
import trip.utils.Connection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class CarServices {

@Test
    public void test() {
        Connection connection=new Connection();
        if((connection.getRestToken()==null || connection.getRestToken().equals("")) && (connection.getSoapToken()==null || connection.getSoapToken().equals(""))){
            connection.connectSabreAPI();
            try {
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex){

            }
        }
        Itinerary itinerary= new Itinerary();
        itinerary.setListCar(getRentalCars("BCN", "MAD", "2015-07-01", "2015-07-05", 1, connection));
    System.out.println(itinerary.getListCar().size());
    }

    public List<Car> getRentalCars(final String startAirport, final String finishAirpot,
                                   final String startDate, final String endDate, final int numberOfPassenger, final Connection pconnection){

        Connection connection=new Connection();
        if((connection.getRestToken()==null || connection.getRestToken().equals("")) && (connection.getSoapToken()==null || connection.getSoapToken().equals(""))){
            connection.connectSabreAPI();
            try {
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex){

            }
        }

        ClassLoader classLoader = getClass().getClassLoader();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            String xmlInput=connection.callSoapConnection(createRequestConsecionario(startAirport, connection.getSoapToken()), "https://sws3-crt.cert.sabre.com");


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new ByteArrayInputStream(xmlInput.getBytes("utf-8"))));

            ConsecionarioParse parse = new ConsecionarioParse();
            parse.parseo(doc);

            List<Consecionario> listConsecionary=parse.getListConsecionary();
            Consecionario consecionario=listConsecionary.get(0);
            //TODO Lista de consecinarios de la llegada y ver cuales se encuentran en los puntos de salida y llegada
            //para poder dejar el coche. Con ese listado de marcas en los dos aeropuertos se miran que coches ofrecen
            //para devolverlos
            String xmlInputRentalCar=connection.callSoap(createRequestCar(consecionario.getLocaltionCode(), consecionario.getVendorCode(), startDate, endDate, connection.getSoapToken()),"https://sws3-crt.cert.sabre.com");


            Document doc2 = db.parse(new InputSource(new ByteArrayInputStream(xmlInputRentalCar.getBytes("utf-8"))));


            RentalCarParse parseRentalCar= new RentalCarParse();
            parseRentalCar.parseo(doc2);

            List<Car> listCar=parseRentalCar.getListCar();

            String xmlInputRentalDetailCar=connection.callSoap(createRequestCarDetails(consecionario.getLocaltionCode(), consecionario.getVendorCode(), startDate, endDate, connection.getSoapToken()),"https://sws3-crt.cert.sabre.com");

            Document doc3 = db.parse(new InputSource(new ByteArrayInputStream(xmlInputRentalDetailCar.getBytes("utf-8"))));

            DetailCarParse detailCarParse= new DetailCarParse();
            detailCarParse.parseo(doc3);

            List<CarDetails> carDetailsList = detailCarParse.getListCarDetails();

            for (CarDetails carDetails: carDetailsList){
                for(Car car:listCar){
                    if (car.getCarCode()!=null && carDetails.getCarType()!=null && car.getCarCode().equals(carDetails.getCarType())){
                        car.setCapacity(carDetails.getCapacity());
                        car.setCarName(carDetails.getCarName());
                        car.setCarType(carDetails.getCarType());
                    }
                }
            }
            return listCar;

        } catch (Exception ex){
                ex.printStackTrace();
        }
        return null;
    }


    public String createRequestConsecionario(final String startAirport, final String token){
        String request= new String();
        request=request+"<SOAP-ENV:Envelope xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <SOAP-ENV:Header>\n" +
                "        <m:MessageHeader xmlns:m=\"http://www.ebxml.org/namespaces/messageHeader\">\n" +
                "            <m:From>           <m:PartyId type=\"urn:x12.org:IO5:01\">99999</m:PartyId>          </m:From>\n" +
                "            <m:To>               <m:PartyId type=\"urn:x12.org:IO5:01\">123123</m:PartyId>           </m:To>\n" +
                "            <m:CPAId>ABC</m:CPAId>            <m:ConversationId>abc123</m:ConversationId>            <m:Service m:type=\"OTA\">VehLocationListRQ</m:Service>\n" +
                "            <m:Action>VehLocationListLLSRQ</m:Action>\n" +
                "            <m:MessageData>\n" +
                "                <m:MessageId>mid:20001209-133003-2333@clientofsabre.com</m:MessageId>\n" +
                "                <m:Timestamp>2015-06-27T11:15:12Z</m:Timestamp>\n" +
                "                <m:TimeToLive>2015-06-29T11:15:12Z</m:TimeToLive>\n" +
                "            </m:MessageData>\n" +
                "            <m:DuplicateElimination/>            <m:Description>Car</m:Description>\n" +
                "        </m:MessageHeader>\n" +
                "        <wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\">\n" +
                "            <wsse:BinarySecurityToken EncodingType=\"wsse:Base64Binary\" valueType=\"String\">"+token+"</wsse:BinarySecurityToken>\n" +
                "        </wsse:Security>\n" +
                "    </SOAP-ENV:Header>\n" +
                "   <SOAP-ENV:Body>\n";
        request= request+ "<VehLocationListRQ Version=\"2.0.0\" xmlns=\"http://webservices.sabre.com/sabreXML/2011/10\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  ";
        request= request+ "  <VehAvailRQCore>    <VehRentalCore> ";
        request= request+ "<PickUpLocation LocationCode=\""+startAirport+"\"/>  ";
        request= request+ " </VehRentalCore>   </VehAvailRQCore> </VehLocationListRQ>   </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";

        System.out.println(request);
        return request;
    }

    public String createRequestCar(final String location,final String consecionario, final String dateStart, final String dateEnd, final String token){
        String request= new String();
        request=request+"<SOAP-ENV:Envelope xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <SOAP-ENV:Header>\n" +
                "        <m:MessageHeader xmlns:m=\"http://www.ebxml.org/namespaces/messageHeader\">\n" +
                "            <m:From>           <m:PartyId type=\"urn:x12.org:IO5:01\">99999</m:PartyId>          </m:From>\n" +
                "            <m:To>               <m:PartyId type=\"urn:x12.org:IO5:01\">123123</m:PartyId>           </m:To>\n" +
                "            <m:CPAId>ABC</m:CPAId>            <m:ConversationId>abc123</m:ConversationId>            <m:Service m:type=\"OTA\">OTA_VehAvailRateLLSRQ</m:Service>\n" +
                "            <m:Action>OTA_VehAvailRateLLSRQ</m:Action>\n" +
                "            <m:MessageData>\n" +
                "                <m:MessageId>mid:20001209-133003-2333@clientofsabre.com</m:MessageId>\n" +
                "                <m:Timestamp>2015-06-27T11:15:12Z</m:Timestamp>\n" +
                "                <m:TimeToLive>2015-06-29T11:15:12Z</m:TimeToLive>\n" +
                "            </m:MessageData>\n" +
                "            <m:DuplicateElimination/>            <m:Description>Car</m:Description>\n" +
                "        </m:MessageHeader>\n" +
                "        <wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\">\n" +
                "            <wsse:BinarySecurityToken EncodingType=\"wsse:Base64Binary\" valueType=\"String\">"+token+"</wsse:BinarySecurityToken>\n" +
                "        </wsse:Security>\n" +
                "    </SOAP-ENV:Header>\n" +
                "   <SOAP-ENV:Body>\n";
        request= request+ "<OTA_VehAvailRateRQ xmlns=\"http://webservices.sabre.com/sabreXML/2011/10\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"2.4.0\">\n" +
                "    <VehAvailRQCore QueryType=\"Quote\">";
        request= request+"<VehRentalCore PickUpDateTime=\""+dateStart.substring(5)+"T09:00\" ReturnDateTime=\""+dateEnd.substring(5)+"T11:00\">\n" +
                "            <PickUpLocation LocationCode=\""+location+"\" />\n" +
                "        </VehRentalCore>\n" +
                "        <VendorPrefs>\n" +
                "            <VendorPref Code=\""+consecionario+"\" />\n" +
                "        </VendorPrefs>\n" +
                "    </VehAvailRQCore>\n" +
                "</OTA_VehAvailRateRQ>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        return request;
    }

    public String createRequestCarDetails(final String location,final String consecionario, final String dateStart, final String dateEnd, final String token) {
        String request = new String();
        request = request + "<SOAP-ENV:Envelope xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <SOAP-ENV:Header>\n" +
                "        <m:MessageHeader xmlns:m=\"http://www.ebxml.org/namespaces/messageHeader\">\n" +
                "            <m:From>           <m:PartyId type=\"urn:x12.org:IO5:01\">99999</m:PartyId>          </m:From>\n" +
                "            <m:To>               <m:PartyId type=\"urn:x12.org:IO5:01\">123123</m:PartyId>           </m:To>\n" +
                "            <m:CPAId>ABC</m:CPAId>            <m:ConversationId>abc123</m:ConversationId>            <m:Service m:type=\"OTA\">OTA_VehLocDetailLLSRQ</m:Service>\n" +
                "            <m:Action>OTA_VehLocDetailLLSRQ</m:Action>\n" +
                "            <m:MessageData>\n" +
                "                <m:MessageId>mid:20001209-133003-2333@clientofsabre.com</m:MessageId>\n" +
                "                <m:Timestamp>2015-06-27T11:15:12Z</m:Timestamp>\n" +
                "                <m:TimeToLive>2015-06-29T11:15:12Z</m:TimeToLive>\n" +
                "            </m:MessageData>\n" +
                "            <m:DuplicateElimination/>            <m:Description>Car</m:Description>\n" +
                "        </m:MessageHeader>\n" +
                "        <wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\">\n" +
                "            <wsse:BinarySecurityToken EncodingType=\"wsse:Base64Binary\" valueType=\"String\">" + token + "</wsse:BinarySecurityToken>\n" +
                "        </wsse:Security>\n" +
                "    </SOAP-ENV:Header>\n" +
                "   <SOAP-ENV:Body>\n";
        request=request+" <OTA_VehLocDetailRQ Version=\"2.1.0\" xmlns=\"http://webservices.sabre.com/sabreXML/2011/10\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> \n" +
                "  <VehAvailRQCore> ";
        request=request+" <VehRentalCore PickUpDateTime=\""+dateStart.substring(5)+"\" ReturnDateTime=\""+dateEnd.substring(5)+"\"> \n" +
                "     <PickUpLocation LocationCode=\""+location+"\"/>\n" +
                "   </VehRentalCore>\n" +
                "   <VendorPrefs> \n" +
                "     <VendorPref Code=\""+consecionario+"\"/> \n" +
                "   </VendorPrefs>\n" +
                "  </VehAvailRQCore> \n" +
                "</OTA_VehLocDetailRQ>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        return request;
    }
}
