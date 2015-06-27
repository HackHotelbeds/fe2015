var hb = {};

$(function () {

    var fromCityName = getUrlParameter("from");
    var toCityName = getUrlParameter("to");

    console.log("FROM: " + fromCityName + " TO: " + toCityName);

    hb.startDate = getUrlParameter("startdate");
    hb.endDate = getUrlParameter("enddate");

    getCloserAirportWithCityName(fromCityName, "FROM");
    getCloserAirportWithCityName(toCityName, "TO");

    getCloserAirports();

});

function getCloserAirportWithCityName(cityname, type) {
    var url = "http://maps.googleapis.com/maps/api/geocode/json?address="+cityname+"&sensor=true";
    $.get(url, function (response) {
        if (type == "FROM") {
            hb.from = response.results[0].geometry.location;
            console.log("FROM COORDS: " + hb.from.lat + "," + hb.from.lng);
            getCloserAirportsToLocation(3, hb.from.lng, hb.from.lat, "FROM");
        } else if (type == "TO") {
            hb.to = response.results[0].geometry.location;
            console.log("TO COORDS: " + hb.to.lat + "," + hb.to.lng);
            getCloserAirportsToLocation(3, hb.to.lng, hb.to.lat, "TO");
        }
    });
}


function getCloserAirports() {
    $.get("http://ipinfo.io", function (response) {
        var location = response.loc.split(",");
        var longitude = location[0];
        var latitude = location[1];
        getCloserAirportsToLocation(3, latitude, longitude, "CURRENT");
    }, "jsonp");
}

function getCloserAirportsToLocation(maxResults, latitude, longitude, type) {
    var closerAirportUrl = "https://airport.api.aero/airport/nearest/"+longitude+"/"+latitude+"/?maxAirport="+maxResults+"&user_key=593e0bc48367a68437f4eaea4497d03d";
    $.get(closerAirportUrl, function (response) {
        if (type == "CURRENT") {
            hb.currentLocationAirports = response.airports;
            console.log("CURRENT LOCATION AIRPORTS: " + hb.currentLocationAirports[0].code + ", count" + hb.currentLocationAirports.length);
        } else if (type == "TO") {
            hb.originAirports = response.airports;
            console.log("TO AIRPORTS: " + hb.originAirports[0].code + ", count" + hb.originAirports.length);
        } else if (type == "FROM") {
            hb.destinationAirports = response.airports;
            console.log("FROM AIRPORTS: " + hb.destinationAirports[0].code + ", count" + hb.destinationAirports.length);
        }
    }, "jsonp");
}

function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}