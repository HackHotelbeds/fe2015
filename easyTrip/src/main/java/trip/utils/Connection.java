package trip.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibram on 27/06/2015.
 */
public class Connection {

    private String restToken="";

    public String getRestToken() {
        return restToken;
    }

    public void setRestToken(String restToken) {
        this.restToken = restToken;
    }



    public String callSoap(final String request, final String url) throws SOAPException {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
       //test https://sws3-sts.cert.sabre.com
        //customer https://sws3-crt.cert.sabre.com
        //SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
        return null;
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









    private String createSecurityRequest(){
        String request=""
    }
}
