var hb = {};
hb.directionsService = null;
hb.directionsDisplay = null;

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

        var directionsRendererOptions = {
            draggable: false
        };
        hb.directionsService = new google.maps.DirectionsService();
        hb.directionsDisplay = new google.maps.DirectionsRenderer(directionsRendererOptions);
        hb.directionsDisplay.setMap(hb.map);

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

        } else if (type == "TO") {
            hb.destinationAirports = response.airports;
            console.log("TO AIRPORTS: " + hb.destinationAirports[0].code + ", count" + hb.destinationAirports.length);

            var coord = new google.maps.LatLng(hb.from.lat, hb.from.lng);
            coord = new google.maps.LatLng(hb.destinationAirports[0].lat, hb.destinationAirports[0].lng);
            //hb.addMarker(coord, icons.origin, hb.map, false, false);

            calculateRoute(hb.originAirports[0], hb.destinationAirports[0]);
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
        waypoints: []
    };
    /*route = directionsService.route(directionsRequest, function(result, status) {
     if (status == google.maps.DirectionsStatus.OK) {
     directionsDisplay.setDirections(result);
     origin.setMap(null);
     destination.setMap(null);
     savedRoute = true;
     }
     });*/

    sendRouteRequest(directionsRequest);
}

function sendRouteRequest(request) {
    hb.directionsService.route(request, function(result, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            hb.directionsDisplay.setDirections(result);
            hb.route = result;
            //origin.setMap(null);
            //destination.setMap(null);
            //savedRoute = true;
        }
    });
    workOutMostPopulatedCities();
}

function workOutMostPopulatedCities() {


    //var center = interestArea.getCenter();
    //var radiusInMeters = interestArea.getRadius();
    //var radiusR = google.maps.geometry.spherical.computeOffset(center, radiusInMeters, 90);
    //var radiusL = google.maps.geometry.spherical.computeOffset(center, radiusInMeters, -90);
    //var radiusT = google.maps.geometry.spherical.computeOffset(center, radiusInMeters, 0);
    //var radiusB = google.maps.geometry.spherical.computeOffset(center, radiusInMeters, 180);
    //var diffLat = Math.abs(radiusL.lng()-radiusR.lng())/2;
    //var diffLng = Math.abs(radiusT.lat()-radiusB.lat())/2;

    //var top = center.lat() - diffLng;
    //var bottom = center.lat() + diffLng;
    //var left = center.lng() - diffLat;
    //var right = center.lng() + diffLat;
    var top, bottom, right, left;

    if (hb.originAirports[0].lng > hb.destinationAirports[0].lng) {
        top = hb.destinationAirports[0].lat;
        bottom = hb.originAirports[0].lat;
        left = hb.destinationAirports[0].lng;
        right = hb.originAirports[0].lng;
    } else {
        top = hb.originAirports[0].lat;
        bottom = hb.destinationAirports[0].lat;
        left = hb.originAirports[0].lng;
        right = hb.destinationAirports[0].lng;
    }
    if (top < bottom) {
        var copy = bottom;
        bottom = top;
        top = copy;
    }

    if (right < left) {
        var copy = right;
        right = left;
        left = copy;
    }

    var margin = 0.4;
    getNearbyCities(top + margin, bottom - margin, left - margin, right + margin);
}

function rotate(x, y, alpha) {
    var point = null;
    point.X = x*Math.cos(alpha) - y*Math.sin(alpha);
    point.Y = x*Math.sin(alpha) + y*Math.cos(alpha);
    return point;
}

function getNearbyCities(top, bottom, left, right, area) {
    var requestUrl = 'http://api.geonames.org/citiesJSON?north=' + top + '&south=' + bottom + '&east=' + right + '&west=' + left + '&lang=en&username=pablogdt';
    //if (typeof(area.markers) == "undefined") {
    //    area.markers = [];
    //}
    //$.each(area.markers, function(i, area) {
    //    area.setMap(null);
    //});

    /*var placeMarkerInsideArea = function(data, area) {
        var index;
        for (index = 0; index < data.geonames.length; index++) {
            //v.toponymName;
            var cityInfo = data.geonames[index];
            var cityCoordinates = new google.maps.LatLng(cityInfo.lat, cityInfo.lng);
            var distance = google.maps.geometry.spherical.computeDistanceBetween(area.getCenter(), cityCoordinates);
            if (distance <= area.getRadius()) {
                var marker = placeMarker(cityCoordinates, icons.origin, map);
                makeInfoWindowEvent(map, infowindow, marker, cityInfo);
                area.markers.push(marker);
            }
        }
    };*/


    //var coord = new google.maps.LatLng(top, left);
    //hb.addMarker(coord, icons.destination, hb.map, false, false);

    //var coord1 = new google.maps.LatLng(bottom, right);
    //hb.addMarker(coord1, icons.destination, hb.map, false, false);

    //var coord = new google.maps.LatLng(top, right);
    //hb.addMarker(coord, icons.destination, hb.map, false, false);

    //var coord1 = new google.maps.LatLng(bottom, left);
    //hb.addMarker(coord1, icons.destination, hb.map, false, false);

    $.get( requestUrl, function( data ) {
        for (i=0;i<hb.tripLength && i<data.geonames.length;i++) {
            var city = data.geonames[i];
            var location = new google.maps.LatLng(city.lat, city.lng);
            hb.addMarker(location, icons.origin, hb.map, true, false);
            var s="";
        }
        data.geonames.forEach(function(entry) {
            console.log(entry);
        });
        //placeMarkerInsideArea(data, area);
        //var v = area.markers.length;
    });

}