
var ratesFull = localStorage.getItem('route99-rates');
var ratesSelectedString = localStorage.getItem('route99-ratesSelected');
var paymentFormString = localStorage.getItem('route99-paymentForm');

function parseQueryString( ratesSelectedString ) {
    var params = {}, queries, temp, i, l;
 
    // Split into key/value pairs
    queries = ratesSelectedString.split("&");
 
    // Convert the array of strings into an object
    for ( i = 0, l = queries.length; i < l; i++ ) {
        temp = queries[i].split('=');
        params[temp[0]] = temp[1];
    }
 
    return params;
};

function buildResultsPanel(name, title, values, useCheckbox) {
  var resultsPanel = '';

  for (var v = 0; v < values.length; v++) {
    resultsPanel += '<div class="input-group">';

    resultsPanel += '  <div class="col-md-7 result-item no-shadow">' + values[v].text + '</div>';
    resultsPanel += '  <div class="col-md-3 result-price no-shadow">' + values[v].price + '</div>';
    resultsPanel += '  <div class="col-md-2 result-company no-shadow" data-company-name="' + values[v].company + '"></div>';
    resultsPanel += '</div>';
  }

  return resultsPanel;
}

function showItineraryWithRates(rates) {
  var summary = '';

  var arrivalFlights = [];
  if (rates.ida != null) {
    for (var f = 0; f < rates.ida.length; f++) {
      var flight = rates.ida[f];
      var flightText = flight.startAirport+'-'+flight.endAirport;
      flightText += ' ('+flight.departureDate+' '+flight.departureHour+'-'+flight.arrivalDate+' '+flight.arrivalHour+')';
      var flightPrice = '  ' + flight.price + ' ' + flight.currency;

      arrivalFlights[f] = {
        "id": flight.id,
        "text": flightText,
        "price": flightPrice,
        "company": flight.company
      }
    }
    $('#arrival-flight-selected').html(buildResultsPanel('arrivalFlight', 'Arrival flight', arrivalFlights));
  }

  var carRented = [];
  if (rates.listCar != null) {
    for (var f = 0; f < rates.listCar.length; f++) {
      var car = rates.listCar[f];
      var carText = car.carName;
      var carPrice = '  ' + car.price + ' ' + car.currency;

      carRented[f] = {
        "id": car.id,
        "text": carText,
        "price": carPrice,
        "company": car.company
      }
    }
    $('#car-selected').html(buildResultsPanel('carRented', 'Car rented', carRented));
  }

  if (rates.hotelOptionDays != null) {
    for (var hotelOption = 0; hotelOption < rates.hotelOptionDays.length; hotelOption++) {
      var hotels = [];
      for (var f = 0; f < rates.hotelOptionDays[hotelOption].listHotel.length; f++) {
        var hotel = rates.hotelOptionDays[hotelOption].listHotel[f];
        var hotelText = hotel.name;
        if (typeof(hotel.roomtype) == "undefined") {
          hotelText += ' ' + hotel.roomtype;
        }
        if (hotel.board != null) {
          hotelText += ' ' + hotel.board;
        }
        var hotelPrice = '  ' + hotel.price + ' ' + hotel.currency;

        hotels[f] = {
          "id": hotel.id,
          "text": hotelText,
          "price": hotelPrice,
          "company": hotel.company
        }
      }
      $('#hotels-selected').html(buildResultsPanel('hotelOption' + hotelOption, 'Hotels, day ' + rates.hotelOptionDays[hotelOption].day, hotels));
    }
  }

  if (rates.ticketOptionDays) {
    for (var ticketOption = 0; ticketOption < rates.ticketOptionDays.length; ticketOption++) {
      var tickets = [];
      for (var f = 0; f < rates.ticketOptionDays[ticketOption].listTicket.length; f++) {
        var ticket = rates.ticketOptionDays[ticketOption].listTicket[f];
        var ticketText = ticket.name;
        if (typeof(ticket.currency) == "undefined") {
          ticket.currency = "EUR";
        }
        var ticketPrice = '  ' + ticket.price + ' ' + ticket.currency;

        tickets[f] = {
          "id": ticket.id,
          "text": ticketText,
          "price": ticketPrice,
          "company": ticket.company
        }
      }
      $('#tickets-selected').html(buildResultsPanel('ticketOption' + ticketOption,
        'Tickets, day ' + rates.ticketOptionDays[ticketOption].day,
        tickets,
        'useCheckbox'
      ));
    }
  }

  var departureFlights = [];
  if (rates.vuelta != null) {
    for (var f = 0; f < rates.vuelta.length; f++) {
      var flight = rates.vuelta[f];
      var flightText = flight.startAirport + '-' + flight.endAirport;
      flightText += ' (' + flight.departureDate + ' ' + flight.departureHour + '-' + flight.arrivalDate + ' ' + flight.arrivalHour + ')';
      var flightPrice = '  ' + flight.price + ' ' + flight.currency;

      departureFlights[f] = {
        "id": flight.id,
        "text": flightText,
        "price": flightPrice,
        "company": flight.company
      }
    }
    $('#returning-flight-selected').html(buildResultsPanel('departureFlight', 'Departure flight', departureFlights));
  }


}


$(document).ready(function() {
	
	var ratesSelected = parseQueryString(ratesSelectedString);
	var paymentForm = parseQueryString(paymentFormString);
	var rates = $.parseJSON(ratesFull);

	showItineraryWithRates(rates);

});
