$(function () {

    var from = getUrlParameter("from");
    var to = getUrlParameter("to");
    var startDate = getUrlParameter("startdate");
    var endDate = getUrlParameter("enddate");

    alert(getCloserAirport());

});


function getCloserAirport() {
    $.get("http://ipinfo.io", function (response) {
        var location = response.loc.split(",");
        var longitude = location[0];
        var latitude = location[1];

        var closerAirportUrl = "https://airport.api.aero/airport/nearest/"+longitude+"/"+latitude+"/?maxAirport=3&user_key=593e0bc48367a68437f4eaea4497d03d";
        $.get(closerAirportUrl, function (response) {
            var s ="";
            $.each(response.airports, function(index, value) {
                if (typeof value.iata !== "undefined") {
                    $("#closerAirport").val(value.iata);
                    return false;
                }
            });
        }, "jsonp");
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