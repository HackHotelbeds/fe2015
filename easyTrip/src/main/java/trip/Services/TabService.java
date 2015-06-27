package trip.Services;

import org.json.simple.parser.ParseException;
import trip.parse.TabParser;
import trip.pojo.Ticket;
import trip.utils.UtilsParse;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 27/06/2015.
 */
public class TabService {

    public List<Ticket> getTabTickets(String dateFrom, String dateTo, String lat, String lon) {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            ;
        }

        String soapXml = createTabRequest(dateFrom, dateTo, lat, lon);
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL("https://actapiint.activitiesbank.com/actdis-api/public/v2/activity/search");
            java.net.URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer coh169ama2fm6dvoudd4rjcn213m93l1");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
            wr.write(soapXml);
            wr.flush();
            // Read the response
            java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "UTF8"));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Ticket> services = new ArrayList<>();
        try {
            services = TabParser.parse(response.toString());
        } catch (ParseException e) {
        } catch (IOException e) {
        }
        //TODO RETURN
        return null;
    }


    public String createTabRequest(String dateFrom, String dateTo, String lat, String lon){

        String request= new String();
        request= request+ "{";
        request= request+ "\"filters\": [";
        request= request+ "[";
        request= request+ "{";
        request= request+ "\"type\": \"gps\",";
        request= request+ "\"latitude\": " + lon + ",";
        request= request+ "\"longitude\": " + lat;
        request= request+ "}";
        request= request+ "]";
        request= request+ "],";
        request= request+ "\"from\": \"" + dateFrom + "\",";
        request= request+ "\"to\": \"" + dateTo + "\",";
        request= request+ "\"language\": \"en\"";
        request= request+ "}";
        return request;
    }
}
