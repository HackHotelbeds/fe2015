package trip.utils;

/**
 * Created by Ibram on 27/06/2015.
 */

import com.fasterxml.jackson.core.JsonGenerationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import trip.pojo.Itinerary;

import java.io.IOException;
import java.io.StringWriter;
import java.text.*;
import java.util.Date;

public class UtilsParse {
    private static SimpleDateFormat SDF= new SimpleDateFormat("dd/MM/yyyy");

    public static String convertObjectToJson(Itinerary itinerary){

        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter= new StringWriter();
        try
        {

            mapper.writeValue(stringWriter, itinerary);
        } catch (JsonGenerationException e)
        {
            e.printStackTrace();
        } catch (JsonMappingException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static Date stringToDate(final String fecha) throws ParseException {
        return SDF.parse(fecha);
    }

    public static String dateToString(final Date fecha) throws ParseException {
        return SDF.format(fecha);
    }
}
