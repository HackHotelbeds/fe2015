
function applyMargins() {
  var leftToggler = $(".mini-submenu-left");
  if (leftToggler.is(":visible")) {
    $("#map .ol-zoom")
      .css("margin-left", 0)
      .removeClass("zoom-top-opened-sidebar")
      .addClass("zoom-top-collapsed");
  } else {
    $("#map .ol-zoom")
      .css("margin-left", $(".sidebar-left").width())
      .removeClass("zoom-top-opened-sidebar")
      .removeClass("zoom-top-collapsed");
  }
}

function isConstrained() {
  return $(".sidebar").width() == $(window).width();
}

function applyInitialUIState() {
  if (isConstrained()) {
    $(".sidebar-left .sidebar-body").fadeOut('slide');
    $('.mini-submenu-left').fadeIn();
  }
}

function getPoints() {
  return [{}];
}

function buildResultsPanel(name, title, values, useCheckbox) {
  data = {
    name: name,
    title: title,
    values: values,
    useCheckbox: useCheckbox
  };
  return Mustache.render(MST['resultBlock'], data);
}

function showItineraryWithRates(rates) {
  var summary = '';

  localStorage.setItem('route99-rates', JSON.stringify(rates));

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
    summary += buildResultsPanel('arrivalFlight', 'Arrival flight', arrivalFlights);
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
    summary += buildResultsPanel('carRented', 'Car rented', carRented);
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
      summary += buildResultsPanel('hotelOption' + hotelOption, 'Hotels, day ' + rates.hotelOptionDays[hotelOption].day, hotels);
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
      summary += buildResultsPanel('ticketOption' + ticketOption,
        'Tickets, day ' + rates.ticketOptionDays[ticketOption].day,
        tickets,
        'useCheckbox'
      );
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
    summary += buildResultsPanel('departureFlight', 'Departure flight', departureFlights);
  }

  bootbox.dialog({
    title: '<h4>Choose the rates</h4>',
    message: '<form id="rateSelection">' + summary + '</form>',
    buttons: {
      cancel: {
        label: "Back to the map",
        className: "btn-cancel"
      },
      success: {
          label: "Go pay!",
          className: "btn-success",
          callback: function () {
              localStorage.setItem('route99-ratesSelected', $('#rateSelection').serialize());
              showPaymentForm(rates);
          }
      }
    }
  });

  $(document).on("shown.bs.modal", function (e) {

    $('.modal-body .results-panel').find('a[data-toggle="collapse"]').each(function(index) {
      if(index > 0) {
        $(this).click();
      }
    });

    $('.modal-body .results-panel .rate-option input[type=radio]').click(function(e) {
      var thisHeading = $(this).parents('.panel-collapse').prev();
      $(thisHeading).find('a[data-toggle="collapse"]').click();

      var nextHeading = $(thisHeading).parent('.results-panel').next().find('.resultsPanel-heading a[data-toggle="collapse"]');
      if ($(nextHeading).hasClass('collapsed')) {
        $(nextHeading).click();
      }

      var optionPrice = $(this).parents('.input-group').find('.result-price').html();
      $(thisHeading).find('.price-selected').html(optionPrice);
    });

    $('.result-company').each(function(e) {
      $(this).html('<img src="img/suppliers/logo-' + $(this).data('company-name') + '.png" />');
    })
  });
}

function showPaymentForm() {

  data = {
    field: [
      {'id': 'holders-first-name', 'label': 'Holder\'s first name'},
      {'id': 'holders-last-name', 'label': 'Holder\'s last name'},
      {'id': 'passport-or-id', 'label': 'Passport / ID', 'placeholder': 'Passport number or ID'},
      {'id': 'address', 'label': 'Address'},
      {'id': 'email', 'label': 'Email', 'placeholder': 'you@somewhere.com', 'type': 'email'},
      {'id': 'card-holder-name', 'label': 'Name on card', 'placeholder': 'Card holder\'s full name'},
      {'id': 'card-number', 'label': 'Credit card number', 'placeholder': 'Debit/Credit Card Number'}
    ],
    cvv: {'id': 'cvv', 'label': 'Card CVV', 'placeholder': 'Security code'}
  };

  formBody = Mustache.render(MST['paymentForm'], data);

  bootbox.dialog({
    title: '<h4>Complete your personal information for paying</h4>',
    message: '<form id="payment-form" class="form-horizontal" role="form">' + formBody + '</form>',
    buttons: {
        cancel: {
          label: "Back to the map",
          className: "btn-cancel"
        },
        success: {
            label: "Pay now!",
            className: "btn-success",
            callback: function () {
              sendConfirmationData();
            }
        }
    }
  });  
}

function sendConfirmationData() {

  localStorage.setItem('route99-paymentForm', $('#payment-form').serialize());
  $.blockUI({ message: 'Sending all of the confirmation data...', overlayCSS: { backgroundColor: '#876146' } }); 

  setTimeout($.unblockUI, 2000); 
  setTimeout("window.location.href ='/tripVoucher';", 2000);
  /*
  $.ajax({
      type: 'post',      
      url: '/tripVoucher',
      data: JSON.stringify(packJsonForSearchRequest()),
      contentType: 'text/plain',
      beforeSend: function() {
        $.blockUI({ message: 'Sending all of the confirmation data...', overlayCSS: { backgroundColor: '#876146' } }); 
      },
      error: function() {              
          $.unblockUI();
          bootbox.alert({message: 'Cant confirm the booking!'});
      }
  });
  */
}

var MST = {};

function loadMstTemplates() {
  templates = ['resultBlock', 'paymentForm'];

  $.each(templates, function(ndx, tmpName){
    $.get('/mst/' + tmpName + '.mst', function(template) {
      MST[tmpName] = template;
      Mustache.parse(template);
    });
  });
}

$(document).ready(function() {
  loadMstTemplates();

  $('.sidebar-left .slide-submenu').on('click',function() {
    var thisEl = $(this);
    thisEl.closest('.sidebar-body').fadeOut('slide',function(){
      $('.mini-submenu-left').fadeIn();
      applyMargins();
    });
  });

  $('.mini-submenu-left').on('click',function() {
    var thisEl = $(this);
    $('.sidebar-left .sidebar-body').toggle('slide');
    thisEl.hide();
    applyMargins();
  });

  $(window).on("resize", applyMargins);

  $('#all-aboard-submit').click(function(e) {

    var rates = {
    "hotelOptionDays": [
      {
        "listHotel": [
          {"name":"Grand Hotel Milano","price":"75.40","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"SABRE"},
          {"name":"Gran Melia Milano","price":"102.75","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"},
          {"name":"Hilton Milano","price":"213.30","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"}
        ], 
        "day": "1/7/15"
      },
      {"listHotel": [
        {"name":"Hilton","price":"107.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"SABRE"},
        {"name":"Grande Italia Hotel","price":"192.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"SABRE"},
        {"name":"Gran Hotel","price":"73.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"}
      ], "day": "2/7/15"},
      {"listHotel": [
        {"name":"Hilton","price":"59.40","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"},
        {"name":"Hotel La Toscana","price":"82.30","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"},
        {"name":"Cesare Hotel","price":"63.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"}
      ], "day": "3/7/15"},
    ],
    "ida": [{
      "id": "125676789",
      "startAirport": "LGW",
      "endAirport": "MIL",
      "departureDate": "1/7/15", "departureHour": "11:20",
      "arrivalDate": "1/7/15", "arrivalHour": "13:00",
      "flightNumber": "A123", "price": "123.27", "currency": "EUR",
            "company": "SABRE"      
    }],
    "listCar": [
        {
            "addionalString": null,
            "capacity": "4",
            "carCode": "ECAR",
            "carName": "HYUNDAI ACCENT OR SIMILAR",
            "carType": "ECAR",
            "endString": null,
            "garanty": "G",
            "numDays": null,
            "participationLevel": "B",
            "price": "196.57",
            "rateCode": "WEB",
            "startString": null,
            "company": "SABRE",
            "currency": "EUR"
        },
        {
            "addionalString": null,
            "capacity": "4",
            "carCode": "CCAR",
            "carName": "NISSAN VERSA OR SIMILAR",
            "carType": "CCAR",
            "endString": null,
            "garanty": "G",
            "numDays": null,
            "participationLevel": "B",
            "price": "160.83",
            "rateCode": "WEB",
            "startString": null,
            "company": "SABRE",
            "currency": "EUR"
        },
        {
            "addionalString": null,
            "capacity": "5",
            "carCode": "ICAR",
            "carName": "TOYOTA COROLLA OR SIMILAR",
            "carType": "ICAR",
            "endString": null,
            "garanty": "G",
            "numDays": null,
            "participationLevel": "B",
            "price": "175.72",
            "rateCode": "WEB",
            "startString": null,
            "company": "SABRE",
            "currency": "EUR"
        }
    ],
    "ticketOptionDays": [
      {"listTicket": [
        {"name":"City Sightseeing Milano", "price":"7.00", "currency":"EUR", "company":"HOTELBEDS"},
        {"name":"Milan Zoo", "price":"18.00", "currency":"EUR", "company":"GETYOURGUIDE"},
        {"name":"San Siro Tour", "price":"15.00", "currency":"EUR", "company":"HOTELBEDS"}
      ], "day": "1/7/15"},
      {"listTicket": [
        {"name":"City sightseeing", "price":"7.00", "currency":"EUR", "company":"HOTELBEDS"},
        {"name":"National Museum", "price":"8.00", "currency":"EUR", "company":"HOTELBEDS"},
        {"name":"Modern Art Gallery", "price":"9.00", "currency":"EUR", "company":"GETYOURGUIDE"}
      ], "day": "3/7/15"},
    ],
    "vuelta": [{
      "id": "123456789",
      "startAirport": "NAP",
      "endAirport": "LGW",
      "departureDate": "4/7/15", "departureHour": "12:20",
      "arrivalDate": "4/7/15", "arrivalHour": "15:00",
      "flightNumber": "A321", "price": "98.27", "currency": "EUR",
      "company": "SABRE"          
    }]
};

      showItineraryWithRates(rates);
/*
      $.ajax({
          type: 'post',
          url: 'http://localhost:9999/getItinerary',
          data:  JSON.stringify(packJsonForSearchRequest()),
          contentType: 'text/plain',
          beforeSend: function() {
            $.blockUI({ message: 'Looking for the best rates...', overlayCSS: { backgroundColor: '#876146' } }); 
          },
          success: function(data) {
            $.unblockUI();
            showItineraryWithRates($.parseJSON(data));
          },
          error: function() {              
              $.unblockUI();
              bootbox.alert({message: 'Cant get any rates! Try again later!'});
          }
      });
*/


  });

});
