package trip.Services;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import trip.parse.TicketParser;
import trip.pojo.Hotel;
import trip.pojo.Ticket;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roger on 28/06/2015.
 */
public class TicketService {

    @Test
    public void test() {
        List<Ticket> services = getTickets("32.3875577", "-96.808878777");
    }

    public List<Ticket> getTickets(String lat, String lon) {

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

        String result = null;
        try
        {
            StringBuffer data = new StringBuffer();
            String urlStr = "https://api-hackathon.getyourguide.com/1/tours";
            urlStr +=  createGetMyGuideRequest(lat, lon);

            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection ();
            conn.setRequestProperty("X-ACCESS-TOKEN", "s6Uqf3MMhRA7w1z315GLvF3Sc5RJox3ncFoWkJnS6QxYg5BY");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null)
            {
                sb.append(line);
            }
            rd.close();
            result = sb.toString();
        } catch (Exception e){
        }

        List<Ticket> services = null;

        try {
            services = TicketParser.parse(result, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return services;
    }

    public String createGetMyGuideRequest(String lat, String lon){
        return "?currency=EUR&cnt_language=en&coordinates[]="+lat+"&coordinates[]="+lon;
    }
}
