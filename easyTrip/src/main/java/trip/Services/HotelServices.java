package trip.Services;

import org.apache.http.ProtocolException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import trip.parse.HotelParser;
import trip.pojo.Hotel;
import trip.utils.Connection;
import org.junit.Test;

import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Roger on 28/06/2015.
 */
public class HotelServices {

    @Test
    public void test() {
        Connection connection = new Connection();
        if ((connection.getRestToken() == null || connection.getRestToken().equals("")) && (connection.getSoapToken() == null || connection.getSoapToken().equals(""))) {
            connection.connectSabreAPI();
            try {
                connection.callLoginSoap(connection.createSecurityRequest(), "https://sws3-crt.cert.sabre.com");
            } catch (Exception ex) {

            }

            try {
                //TODO cordenadas con fload 2 decimales
                List<Hotel> services = getHotels("2015-09-19","2015-09-20","2","32.38","-96.80",connection);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    private List<Hotel> getHotels(String dateFrom, String dateTo, String paxes, String lat, String lon, Connection connection) throws ProtocolException, IOException, URISyntaxException, ParserConfigurationException, SAXException, InterruptedException {
        String request = getRequest(dateFrom, dateTo, paxes, lat, lon, connection.getSoapToken());
        String xmlInput = connection.callSoapConnection(request, "https://sws3-crt.cert.sabre.com");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new ByteArrayInputStream(xmlInput.getBytes("utf-8"))));

        HotelParser hotelParser = new HotelParser();
        hotelParser.parse(doc);


        return null;
    }

    private String getRequest(String dateFrom, String dateTo, String paxes, String lat, String lon, String token) {

        String request= new String();

        request=request+"<SOAP-ENV:Envelope xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <SOAP-ENV:Header>\n" +
                "        <m:MessageHeader xmlns:m=\"http://www.ebxml.org/namespaces/messageHeader\">\n" +
                "            <m:From>           " +
                "            <m:PartyId type=\"urn:x12.org:IO5:01\">99999</m:PartyId>          </m:From>\n" +
                "            <m:To>               <m:PartyId type=\"urn:x12.org:IO5:01\">123123</m:PartyId>           </m:To>\n" +
                "            <m:CPAId>ABC</m:CPAId>" +
                "           <m:ConversationId>abc123</m:ConversationId>" +
                "           <m:Service m:type=\"OTA\">OTA_HotelAvailRQ</m:Service>\n" +
                "            <m:Action>OTA_HotelAvailLLSRQ</m:Action>\n" +
                "            <m:MessageData>\n" +
                "                <m:MessageId>mid:20001209-133003-2333@clientofsabre.com</m:MessageId>\n" +
                "                <m:Timestamp>2015-06-27T11:15:12Z</m:Timestamp>\n" +
                "                <m:TimeToLive>2015-06-29T11:15:12Z</m:TimeToLive>\n" +
                "            </m:MessageData>\n" +
                "            <m:DuplicateElimination/>            <m:Description>Hotel</m:Description>\n" +
                "        </m:MessageHeader>\n" +
                "        <wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\">\n" +
                "            <wsse:BinarySecurityToken EncodingType=\"wsse:Base64Binary\" valueType=\"String\">"+token+"</wsse:BinarySecurityToken>\n" +
                "        </wsse:Security>\n" +
                "    </SOAP-ENV:Header>\n" +
                "   <SOAP-ENV:Body>\n";

        request= request+ "<OTA_HotelAvailRQ xmlns=\"http://webservices.sabre.com/sabreXML/2011/10\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"2.2.0\">";
        request= request+ "\n<AvailRequestSegment>";
        request= request+ "\n<GuestCounts Count=\"" + paxes + "\" />";
        request= request+ "\n<HotelSearchCriteria>";
        request= request+ "\n<Criterion>";
        request= request+ "\n<HotelRef Latitude=\"" + lat + "\" Longitude=\"" + lon + "\"/>";
        request= request+ "\n</Criterion>";
        request= request+ "\n</HotelSearchCriteria>";
        request= request+ "\n<TimeSpan End=\"" + dateTo.substring(5) + "\" Start=\"" + dateFrom.substring(5) + "\" />";
        request= request+ "\n</AvailRequestSegment>";
        request= request+ "\n</OTA_HotelAvailRQ>";
        request= request+ "\n</SOAP-ENV:Body>";
        request= request+ "\n</SOAP-ENV:Envelope>";
        return request;

    }
    


}
