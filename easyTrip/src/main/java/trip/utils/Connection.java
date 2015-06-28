package trip.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.HTTP;
import org.codehaus.jettison.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import java.io.*;
import java.net.*;

/**
 * Created by Ibram on 27/06/2015.
 */
public class Connection {

    private String restToken="";
    private String soapToken="";

    public String getSoapToken() {
        return soapToken;
    }

    public void setSoapToken(String soapToken) {
        this.soapToken = soapToken;
    }

    public String getRestToken() {
        return restToken;
    }

    public void setRestToken(String restToken) {
        this.restToken = restToken;
    }


    public void callLoginSoap(final String request, final String urlSoap) throws SOAPException, IOException, ParserConfigurationException, SAXException {
        String result=callSoap(request,urlSoap);
        soapToken= result.substring(result.indexOf("Shared/IDL:IceSess"),result.indexOf("</wsse:BinarySecurityToken>"));

    }

    public String callSoap(final String request, final String urlSoap) throws SOAPException, IOException, ParserConfigurationException, SAXException {
        // Create SOAP Connection
        //Code to make a webservice HTTP request
        String responseString = "";
        String outputString = "";

        URL url = new URL(urlSoap);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput = request;
        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();

// Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml");
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
//Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();
//Ready with sending the request.
        //Read the response.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

//Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }
        return outputString;
    }

    public String callSoapConnection(final String request, final String urlSoap) throws IOException, URISyntaxException, org.apache.http.ProtocolException {
        // Create SOAP Connection
        //Code to make a webservice HTTP request
        String responseString = "";
        String outputString = "";

        URL url = new URL(urlSoap);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput = request;
        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();

// Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
//Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();
//Ready with sending the request.
        //Read the response.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

//Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }
        return outputString;
    }




    public void  connectSabreAPI(){
        // TODO Auto-generated method stub
        //
        //Request authentication
        //
        final String clientId = "VjE6Zm4zaDBocGcwbWdkYWhmZDpERVZDRU5URVI6RVhU";//Put Your Client Id Here
        final String clientSecret= "RGFkUjVWMHc=";//Put Your Secret Id Here

        //base64 encode clientId and clientSecret
        //String encodedClientId = Base64.encodeBase64String((clientId).getBytes());
        //String encodedClientSecret = Base64.encodeBase64String((clientSecret).getBytes());

        //Concatenate encoded client and secret strings, separated with colon
        String encodedClientIdSecret = clientId+":"+clientSecret;

        //Convert the encoded concatenated string to a single base64 encoded string.
        encodedClientIdSecret = Base64.encodeBase64String(encodedClientIdSecret.getBytes());


        restToken = getAuthToken("https://api.test.sabre.com",encodedClientIdSecret);
      // String response = sendRequest("https://api.test.sabre.com/v1/shop/themes", restToken);

       // System.out.println("SDS Response: "+response);

    }


    public String getAuthToken(String apiEndPoint, String encodedCliAndSecret)  {

        //receives : apiEndPoint (https://api.test.sabre.com)
        //encodedCliAndSecret : base64Encode(  base64Encode(V1:[user]:[group]:[domain]) + ":" + base64Encode([secret]) )
        String strRet = null;

        try {

            URL urlConn = new URL(apiEndPoint + "/v1/auth/token");
            URLConnection conn=urlConn.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", "Basic " + encodedCliAndSecret);
            conn.setRequestProperty("Accept", "application/json");

            //send request
            DataOutputStream dataOut = new DataOutputStream(conn.getOutputStream());
            dataOut.writeBytes("grant_type=client_credentials");
            dataOut.flush();
            dataOut.close();

            //get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strChunk="";
            StringBuilder sb = new StringBuilder();
            while(null != ((strChunk=rd.readLine())))
                sb.append(strChunk);

            //parse the token
            JSONObject respParser = new JSONObject(sb.toString());
            strRet = respParser.getString("access_token");


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return strRet;


    }

    @SuppressWarnings("deprecation")
    public String sendRequest(String payLoad,String authToken){
        URLConnection conn=null;
        String strRet=null;
        try {
            URL urlConn = new URL(payLoad);

            conn = null;
            conn = urlConn.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Accept", "application/json");


            DataInputStream dataIn = new DataInputStream(conn.getInputStream());
            String strChunk="";
            StringBuilder sb = new StringBuilder("");
            while(null != ((strChunk = dataIn.readLine())))
                sb.append(strChunk);


            strRet = sb.toString();


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("IOException: "+conn.getHeaderField(0));
        }
        return strRet;
    }


    public String createSecurityRequest(){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request+"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eb=\"http://www.ebxml.org/namespaces/messageHeader\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">";
        request=request+"<SOAP-ENV:Header>";
        request=request+"<eb:MessageHeader SOAP-ENV:mustUnderstand=\"1\" eb:version=\"1.0\">";
        request=request+"<eb:ConversationId>1234567890987654321</eb:ConversationId>";
        request=request+"<eb:From><eb:PartyId type=\"urn:x12.org:IO5:01\">999999</eb:PartyId></eb:From>";
        request=request+"<eb:To><eb:PartyId type=\"urn:x12.org:IO5:01\">123123</eb:PartyId></eb:To>";
        request=request+"<eb:CPAId>3YAB</eb:CPAId><eb:Service eb:type=\"OTA\">SessionCreateRQ</eb:Service><eb:Action>SessionCreateRQ</eb:Action>";
        request=request+"<eb:MessageData><eb:MessageId>1000</eb:MessageId><eb:Timestamp>2015-06-27T11:15:12Z</eb:Timestamp><eb:TimeToLive>2015-06-29T11:15:12Z</eb:TimeToLive></eb:MessageData>";
        request=request+"</eb:MessageHeader><wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\" xmlns:wsu=\"http://schemas.xmlsoap.org/ws/2002/12/utility\">";
        request=request+"<wsse:UsernameToken><wsse:Username>10621</wsse:Username><wsse:Password>WS061015</wsse:Password><Organization>3YAB</Organization><Domain>AA</Domain></wsse:UsernameToken>";
        request=request+"</wsse:Security></SOAP-ENV:Header><SOAP-ENV:Body><SessionCreateRQ><POS><Source PseudoCityCode=\"3YAB\"/></POS></SessionCreateRQ></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        return request;
    }
}
