var hb = {};

var icons = {
    origin: "http://maps.gstatic.com/intl/en_ALL/mapfiles/dd-start.png",
    destination: "http://maps.gstatic.com/intl/en_ALL/mapfiles/dd-end.png"
};

$(function () {

    function initialize() {
        var mapOptions = {
            center: { lat: 45.4167754, "lng" : 3.7037902 },
            zoom: 5
        };
        hb.map = new google.maps.Map(document.getElementById('map'), mapOptions);


        var fromCityName = getUrlParameter("from");
        var toCityName = getUrlParameter("to");

        console.log("FROM: " + fromCityName + " TO: " + toCityName);

        hb.startDate = getUrlParameter("startdate");
        hb.endDate = getUrlParameter("enddate");

        getCloserAirportWithCityName(fromCityName, "FROM");
        getCloserAirportWithCityName(toCityName, "TO");

        getCloserAirports();
    }
    google.maps.event.addDomListener(window, 'load', initialize);


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

            //var coord = new google.maps.LatLng(hb.to.lat, hb.to.lng);
            //hb.addMarker(coord, icons.destination, hb.map, false, false);
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

hb.addMarker = function(location, icon, map, draggable, center) {
    var marker = new google.maps.Marker({
        position: location,
        map: map,
        icon: icon,
        draggable: draggable
    });

    if (center) {
        map.setCenter(location);
    }
    return marker;
};

function getCloserAirportsToLocation(maxResults, latitude, longitude, type) {
    var closerAirportUrl = "https://airport.api.aero/airport/nearest/"+longitude+"/"+latitude+"/?maxAirport="+maxResults+"&user_key=593e0bc48367a68437f4eaea4497d03d";
    $.get(closerAirportUrl, function (response) {
        if (type == "CURRENT") {
            hb.currentLocationAirports = response.airports;
            console.log("CURRENT LOCATION AIRPORTS: " + hb.currentLocationAirports[0].code + ", count" + hb.currentLocationAirports.length);
        } else if (type == "TO") {
            hb.originAirports = response.airports;
            console.log("TO AIRPORTS: " + hb.originAirports[0].code + ", count" + hb.originAirports.length);

            var coord = new google.maps.LatLng(hb.to.lat, hb.to.lng);
            coord = new google.maps.LatLng(hb.originAirports[0].lat, hb.originAirports[0].lng);
            hb.addMarker(coord, icons.destination, hb.map, false, false);
        } else if (type == "FROM") {
            hb.destinationAirports = response.airports;
            console.log("FROM AIRPORTS: " + hb.destinationAirports[0].code + ", count" + hb.destinationAirports.length);

            var coord = new google.maps.LatLng(hb.from.lat, hb.from.lng);
            coord = new google.maps.LatLng(hb.destinationAirports[0].lat, hb.destinationAirports[0].lng);
            hb.addMarker(coord, icons.origin, hb.map, false, false);
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