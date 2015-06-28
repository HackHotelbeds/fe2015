
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
  var resultsPanel = '';
  resultsPanel += '<div class="panel panel-default results-panel ">';
  resultsPanel += '<div class="panel-heading resultsPanel-heading">';
  resultsPanel += '<h4 class="panel-title">';
  resultsPanel += '<div class="col-md-7"><a data-toggle="collapse" href="#' + name + '"><i class="fa fa-list-alt"></i> ' + title + '</a></div>';
  resultsPanel += '<div class="text-right price-selected"> ? </div>'
  resultsPanel += '</h4>';
  resultsPanel += '</div>';

  resultsPanel += '<div id="' + name + '" class="panel-collapse collapse in">';
  resultsPanel += '<div class="panel-body">';

  if (useCheckbox) {
    resultsPanel += '<div class="input-group">';
    resultsPanel += '  <span class="input-group-addon rate-option">';
    resultsPanel += '    <input type="radio" name="' + name + 'EmptyRate" value="null">';
    resultsPanel += '  </span>';
    resultsPanel += '  <div class="form-control no-shadow">No ticket for this day</div>';
    resultsPanel += '</div>';
  }

  for (var v = 0; v < values.length; v++) {
    resultsPanel += '<div class="input-group">';
    resultsPanel += '  <span class="input-group-addon rate-option">';
    if (useCheckbox) {
      resultsPanel += '    <input type="checkbox" name="' + name + values[v].id + '">';
    } else {
      resultsPanel += '    <input type="radio" name="' + name + 'Rate" value="'+ values[v].id + '">';
    }
    resultsPanel += '  </span>';
    resultsPanel += '  <div class="col-md-7 result-item no-shadow">' + values[v].text + '</div>';
    resultsPanel += '  <div class="col-md-3 result-price no-shadow">' + values[v].price + '</div>';
    resultsPanel += '  <div class="col-md-2 result-company no-shadow" data-company-name="' + values[v].company + '"></div>';
    resultsPanel += '</div>';
  }
  resultsPanel += '</div>';
  resultsPanel += '</div>';
  resultsPanel += '</div>';

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
              // guardar formulario con seleccion
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

  var formBody = '';

  formBody += '<fieldset>';  

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="holder-title">Title</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <select class="form-control" name="holder-title" id="holder-title">';
  formBody += '    <option value=""></option>';
  formBody += '    <option value="1">Mr.</option>';
  formBody += '    <option value="2">Mrs.</option>';
  formBody += '    <option value="3">Ms.</option>';
  formBody += '    </select>';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="holder-first-name">Holder\' first name</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="holder-first-name" id="holder-first-name" placeholder="Holder\'s first name">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="card-holder-name">Holder\' last name</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="holder-last-name" id="holder-last-name" placeholder="Holder\'s last name">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="passport-or-id">Passport / ID</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="passport-or-id" id="passport-or-id" placeholder="Passport code or ID">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="address">Address</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="address" id="address" placeholder="Address">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="email">Email</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="email" class="form-control" name="email" id="email" placeholder="Email">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="card-holder-name">Name on Card</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="card-holder-name" id="card-holder-name" placeholder="Card Holder\'s Name">';
  formBody += '  </div>';
  formBody += '</div>';
  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="card-number">Card Number</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <input type="text" class="form-control" name="card-number" id="card-number" placeholder="Debit/Credit Card Number">';
  formBody += '  </div>';
  formBody += '</div>';
  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="expiry-month">Expiration Date</label>';
  formBody += '  <div class="col-sm-8">';
  formBody += '    <div class="row">';
  formBody += '      <div class="col-xs-3">';
  formBody += '        <select class="form-control col-sm-2" name="expiry-month" id="expiry-month">';
  formBody += '          <option>Month</option>';
  formBody += '          <option value="01">Jan (01)</option>';
  formBody += '          <option value="02">Feb (02)</option>';
  formBody += '          <option value="03">Mar (03)</option>';
  formBody += '          <option value="04">Apr (04)</option>';
  formBody += '          <option value="05">May (05)</option>';
  formBody += '          <option value="06">June (06)</option>';
  formBody += '          <option value="07">July (07)</option>';
  formBody += '          <option value="08">Aug (08)</option>';
  formBody += '          <option value="09">Sep (09)</option>';
  formBody += '          <option value="10">Oct (10)</option>';
  formBody += '          <option value="11">Nov (11)</option>';
  formBody += '          <option value="12">Dec (12)</option>';
  formBody += '        </select>';
  formBody += '      </div>';
  formBody += '      <div class="col-xs-3">';
  formBody += '        <select class="form-control" name="expiry-year">';
  formBody += '          <option value="13">2013</option>';
  formBody += '          <option value="14">2014</option>';
  formBody += '          <option value="15">2015</option>';
  formBody += '          <option value="16">2016</option>';
  formBody += '          <option value="17">2017</option>';
  formBody += '          <option value="18">2018</option>';
  formBody += '          <option value="19">2019</option>';
  formBody += '          <option value="20">2020</option>';
  formBody += '          <option value="21">2021</option>';
  formBody += '          <option value="22">2022</option>';
  formBody += '          <option value="23">2023</option>';
  formBody += '        </select>';
  formBody += '      </div>';
  formBody += '    </div>';
  formBody += '  </div>';
  formBody += '</div>';
  formBody += '<div class="form-group">';
  formBody += '  <label class="col-sm-4 control-label" for="cvv">Card CVV</label>';
  formBody += '  <div class="col-sm-4">';
  formBody += '    <input type="text" class="form-control" name="cvv" id="cvv" placeholder="Security Code">';
  formBody += '  </div>';
  formBody += '</div>';

  formBody += '</fieldset>';

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
              bootbox.alert('...confirmando...');
            }
        }
    }
  });  
}

$(document).ready(function() {
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
      {"listHotel": [
        {"name":"Il Napolitano","price":"91.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"},
        {"name":"Windham Napoli","price":"62.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"},
        {"name":"Iberostar Napoli","price":"83.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"HOTELBEDS"}
      ], "day": "4/7/15"},
    ],
    "ida": [{
      "id": "125676789",
      "startAirport": "LGW",
      "endAirport": "MIL",
      "departureDate": "1/7/15", "departureHour": "11:20",
      "arrivalDate": "1/7/15", "arrivalHour": "13:00",
      "flightNumber": "A123", "price": "123.27", "currency": "EUR",
            "company": "SABRE"      
    },{
      "id": "452349153",
      "startAirport": "LGW",
      "endAirport": "NAP",
      "departureDate": "4/7/15", "departureHour": "16:50",
      "arrivalDate": "4/7/15", "arrivalHour": "18:30",
      "flightNumber": "A321", "price": "98.27", "currency": "EUR",
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
            "price": "1496.57",
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
            "price": "1620.83",
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
            "price": "1752.72",
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

    //showItineraryWithRates(rates);

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



  });

});
