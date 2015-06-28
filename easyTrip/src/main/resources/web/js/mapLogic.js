var hb = {};
hb.directionsService = null;
hb.directionsDisplay = null;
hb.waypoints = [];

var routeLoadedEvent = new Event('routeLoaded');



var icons = {
    origin: "http://maps.gstatic.com/intl/en_ALL/mapfiles/dd-start.png",
    destination: "http://maps.gstatic.com/intl/en_ALL/mapfiles/dd-end.png"
};

function loadStopovers() {
    if (typeof(hb.route) == "undefined") {
        return false;
    }
    $("#stopover-list").empty();
    var legs = hb.route.routes[0].legs;
    for (i=0;i<legs.length;i++) {
        var leg = legs[i];
        var formattedStartAddress = formatStopover(leg.start_address);
        var formattedEndAddress = formatStopover(leg.end_address);
        $("#stopover-list").append('<label class="list-group-item">Day '+(i+1)+':<br/>'+formattedStartAddress+' - '+formattedEndAddress+'</label>');
    }

}

function formatStopover(address) {
    var addressParts = address.split(", ");
    var formattedAddress = addressParts[addressParts.length-2]+","+addressParts[addressParts.length-1];
    formattedAddress = formattedAddress.substr(formattedAddress.indexOf(" ")+1);
    return formattedAddress;
}

$(function () {

    function initialize() {
        window.addEventListener('routeLoaded', loadStopovers, false);
        var mapOptions = {
            center: { lat: 45.4167754, "lng" : 3.7037902 },
            zoom: 5
        };
        hb.map = new google.maps.Map(document.getElementById('map'), mapOptions);

        var directionsRendererOptions = {
            draggable: true
        };
        hb.directionsService = new google.maps.DirectionsService();
        hb.directionsDisplay = new google.maps.DirectionsRenderer(directionsRendererOptions);
        hb.directionsDisplay.setMap(hb.map);

        google.maps.event.addListener(hb.directionsDisplay, 'directions_changed', function() {
            hb.route = hb.directionsDisplay.getDirections();
            loadStopovers();
        });


        var fromCityName = getUrlParameter("origin");
        var toCityName = getUrlParameter("destination");

        console.log("FROM: " + fromCityName + " TO: " + toCityName);

        hb.paxes = getUrlParameter("paxes");
        hb.startDate = getUrlParameter("start-date");
        hb.endDate = getUrlParameter("end-date");

        hb.tripLength = calculateDaysBetweenDates(hb.startDate, hb.endDate);

        getCloserAirportWithCityName(fromCityName, "FROM");
        getCloserAirportWithCityName(toCityName, "TO");

        getCloserAirports();
    }
    google.maps.event.addDomListener(window, 'load', initialize);


});

function calculateDaysBetweenDates(dateFrom, dateTo) {

    var splittedDateFrom = dateFrom.split("%2F");
    var splittedDateTo = dateTo.split("%2F");

    var oneDay = 24*60*60*1000;
    var firstDate = new Date(splittedDateFrom[2],splittedDateFrom[1],splittedDateFrom[0]);
    var secondDate = new Date(splittedDateTo[2],splittedDateTo[1],splittedDateTo[0]);

    var diffDays = Math.round(Math.abs((firstDate.getTime() - secondDate.getTime())/(oneDay)));
    return diffDays;
}

function getCloserAirportWithCityName(cityname, type) {
    var url = "http://maps.googleapis.com/maps/api/geocode/json?address="+cityname+"&sensor=true";
    $.get(url, function (response) {
        if (type == "FROM") {
            hb.from = response.results[0].geometry.location;
            console.log("FROM COORDS: " + hb.from.lat + "," + hb.from.lng);
            getCloserAirportsToLocation(5, hb.from.lng, hb.from.lat, "FROM");

        } else if (type == "TO") {
            hb.to = response.results[0].geometry.location;
            console.log("TO COORDS: " + hb.to.lat + "," + hb.to.lng);
            //getCloserAirportsToLocation(5, hb.to.lng, hb.to.lat, "TO");

        }
    });
}


function getCloserAirports() {
    $.get("http://ipinfo.io", function (response) {
        var location = response.loc.split(",");
        var longitude = location[0];
        var latitude = location[1];
        longitude = 51.156785;
        latitude = -0.169987;
        getCloserAirportsToLocation(5, latitude, longitude, "CURRENT");
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
        } else if (type == "FROM") {
            hb.originAirports = response.airports;
            console.log("FROM AIRPORTS: " + hb.originAirports[0].code + ", count" + hb.originAirports.length);

            var coord = new google.maps.LatLng(hb.to.lat, hb.to.lng);
            coord = new google.maps.LatLng(hb.originAirports[0].lat, hb.originAirports[0].lng);
            //hb.addMarker(coord, icons.destination, hb.map, false, false);

            getCloserAirportsToLocation(5, hb.to.lng, hb.to.lat, "TO");

        } else if (type == "TO") {
            hb.destinationAirports = response.airports;
            console.log("TO AIRPORTS: " + hb.destinationAirports[0].code + ", count" + hb.destinationAirports.length);

            var coord = new google.maps.LatLng(hb.from.lat, hb.from.lng);
            coord = new google.maps.LatLng(hb.destinationAirports[0].lat, hb.destinationAirports[0].lng);
            //hb.addMarker(coord, icons.origin, hb.map, false, false);

            //setTimeout(function(){}, 500);
            workOutMostPopulatedCities();
            //calculateRoute(hb.originAirports[0], hb.destinationAirports[0]);
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

function calculateRoute(origin, destination) {
    directionsRequest = {
        origin: new google.maps.LatLng(origin.lat, origin.lng),
        destination: new google.maps.LatLng(destination.lat, destination.lng),
        provideRouteAlternatives: false,
        travelMode: google.maps.TravelMode.DRIVING,
        unitSystem: google.maps.UnitSystem.IMPERIAL,
        waypoints: hb.waypoints,
        optimizeWaypoints: true
    };
    sendRouteRequest(directionsRequest);
}

function sendRouteRequest(request) {
    hb.directionsService.route(request, function(result, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            hb.directionsDisplay.setDirections(result);
            hb.route = result;
            window.dispatchEvent(routeLoadedEvent);
            $.unblockUI();
        }
    });
    $.blockUI({ message: 'Building your planning...', overlayCSS: { backgroundColor: '#876146' } });
}

function workOutMostPopulatedCities() {
    var top, bottom, right, left;
    top = hb.destinationAirports[0].lat;
    bottom = hb.originAirports[0].lat;
    left = hb.destinationAirports[0].lng;
    right = hb.originAirports[0].lng;

    var xIncrement = (right - left)/hb.tripLength;
    var yIncrement = (bottom - top)/hb.tripLength;
    var previousX = null, previousY = null;
    var first = false;
    var expectedCalls = hb.tripLength - 1;
    for (i=0;i<hb.tripLength;i++) {
        if (!first) {
            first = true;
        } else {
            var calcX = left + xIncrement * i;
            var calcY = top + yIncrement * i;
            //hb.addMarker(new google.maps.LatLng(calcY, calcX), icons.destination, hb.map, true, false);

            if (previousX != null && previousY != null) {
                getNearbyCities(calcY, previousY, calcX, previousX, expectedCalls);
            }

            previousX = calcX;
            previousY = calcY;
        }
    }

    //calculateRoute(hb.originAirports[0], hb.destinationAirports[0]);
}

function getNearbyCities(top, bottom, left, right, expectedCalls) {
    /*if (true) { return false; }*/
    var requestUrl = 'http://api.geonames.org/citiesJSON?north=' + top + '&south=' + bottom + '&east=' + right + '&west=' + left + '&lang=en&username=pablogdt';

    //var coord = new google.maps.LatLng(top, left);
    //hb.addMarker(coord, icons.destination, hb.map, false, false);

    //var coord1 = new google.maps.LatLng(bottom, right);
    //hb.addMarker(coord1, icons.destination, hb.map, false, false);

    //var coord = new google.maps.LatLng(top, right);
    //hb.addMarker(coord, icons.destination, hb.map, false, false);

    //var coord1 = new google.maps.LatLng(bottom, left);
    //hb.addMarker(coord1, icons.destination, hb.map, false, false);

    $.get( requestUrl, function( data ) {
        if (typeof(data.geonames) == "undefined") {
            return false;
        }
        var city = data.geonames[0];
        var point = new google.maps.LatLng(city.lat, city.lng);
        //hb.addMarker(point, icons.origin, hb.map, true, false);
        var waypoint = { location: point };
        hb.waypoints.push(waypoint);
        console.log("Adding waypoint: "+waypoint.location+ ". Exp:"+expectedCalls+",Got:"+hb.waypoints.length);
        if (expectedCalls-1 == hb.waypoints.length) {
            console.log("Calculating route...");
            calculateRoute(hb.originAirports[0], hb.destinationAirports[0]);
        }
    });

}

function packJsonForSearchRequest() {
    var jsonData = {};
    jsonData.closestAirport = {};
    jsonData.closestAirport.iata = hb.currentLocationAirports[0].code;
    jsonData.closestAirport.lat = hb.currentLocationAirports[0].lat;
    jsonData.closestAirport.lng = hb.currentLocationAirports[0].lng;

    jsonData.originAirport = {};
    jsonData.originAirport.iata = hb.originAirports[0].code;
    jsonData.originAirport.lat = hb.originAirports[0].lat;
    jsonData.originAirport.lng = hb.originAirports[0].lng;

    jsonData.destinationAirport = {};
    jsonData.destinationAirport.iata = hb.destinationAirports[0].code;
    jsonData.destinationAirport.lat = hb.destinationAirports[0].lat;
    jsonData.destinationAirport.lng = hb.destinationAirports[0].lng;

    jsonData.paxes = hb.paxes;
    jsonData.startDate = hb.startDate;
    jsonData.endDate = hb.endDate;

    jsonData.stepovers = [];


    for (i=0;i<hb.route.routes[0].legs.length;i++) {
        var leg = hb.route.routes[0].legs[i];
        jsonData.stepovers.push({ address : leg.end_address, lat: leg.end_location.A, lng: leg.end_location.F });
    }

    return jsonData;
}