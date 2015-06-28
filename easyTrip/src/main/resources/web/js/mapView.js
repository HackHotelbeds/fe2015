
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
  resultsPanel += '<a data-toggle="collapse" href="#' + name + '"><i class="fa fa-list-alt"></i> ' + title + '</a>';
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
    resultsPanel += '  <div class="col-md-8 result-item no-shadow">' + values[v].text + '</div>';
    resultsPanel += '  <div class="col-md-4 result-price no-shadow">' + values[v].price + '</div>';
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
  for (var f = 0; f < rates.ida.length; f++) {
    var flight = rates.ida[f];
    var flightText = flight.startAirport+'-'+flight.endAirport;
    flightText += ' ('+flight.departureDate+' '+flight.departureHour+'-'+flight.arrivalDate+' '+flight.arrivalHour+')';
    var flightPrice = '  ' + flight.price + ' ' + flight.currency;

    arrivalFlights[f] = {
      "id": flight.id,
      "text": flightText,
      "price": flightPrice
    }
  }
  summary += buildResultsPanel('arrivalFlight', 'Arrival flight', arrivalFlights);

  var carRented = [];
  for (var f = 0; f < rates.listCar.length; f++) {
    var car = rates.listCar[f];
    var carText = car.carName;
    var carPrice = '  ' + car.price + ' ' + car.currency;

    carRented[f] = {
      "id": car.id,
      "text": carText,
      "price": carPrice
    }
  }
  summary += buildResultsPanel('carRented', 'Car rented', carRented);

  for (var hotelOption = 0; hotelOption < rates.hotelOptionDays.length; hotelOption++) {
    var hotels = [];
    for (var f = 0; f < rates.hotelOptionDays[hotelOption].listHotel.length; f++) {
      var hotel = rates.hotelOptionDays[hotelOption].listHotel[f];
      var hotelText = hotel.name + ' ' + hotel.roomtype + ' ' + hotel.board;
      var hotelPrice = '  ' + hotel.price + ' ' + hotel.currency;

      hotels[f] = {
        "id": hotel.id,
        "text": hotelText,
        "price": hotelPrice
      }
    }
    summary += buildResultsPanel('hotelOption' + hotelOption, 'Hotels, day ' + rates.hotelOptionDays[hotelOption].day, hotels);
  }

  for (var ticketOption = 0; ticketOption < rates.ticketOptionDays.length; ticketOption++) {
    var tickets = [];
    for (var f = 0; f < rates.ticketOptionDays[ticketOption].listTicket.length; f++) {
      var ticket = rates.ticketOptionDays[ticketOption].listTicket[f];
      var ticketText = ticket.name;
      var ticketPrice = '  ' + ticket.price + ' ' + ticket.currency;

      tickets[f] = {
        "id": ticket.id,
        "text": ticketText,
        "price": ticketPrice
      }
    }
    summary += buildResultsPanel('ticketOption' + ticketOption, 
      'Tickets, day ' + rates.ticketOptionDays[ticketOption].day, 
      tickets,
      'useCheckbox'
    );
  }

  var departureFlights = [];
  for (var f = 0; f < rates.vuelta.length; f++) {
    var flight = rates.vuelta[f];
    var flightText = flight.startAirport+'-'+flight.endAirport;
    flightText += ' ('+flight.departureDate+' '+flight.departureHour+'-'+flight.arrivalDate+' '+flight.arrivalHour+')';
    var flightPrice = '  ' + flight.price + ' ' + flight.currency;

    departureFlights[f] = {
      "id": flight.id,
      "text": flightText,
      "price": flightPrice
    }
  }
  summary += buildResultsPanel('departureFlight', 'Departure flight', departureFlights);

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
    });

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
          {"name":"Hot1","price":"111.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"Trivago"},
          {"name":"Hot2","price":"112.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"Expedia"},
          {"name":"Hot3","price":"113.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"Bedsonline"}
        ], 
        "day": "1/7/15"
      },
      {"listHotel": [
        {"name":"Hot1","price":"111.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"Booking"},
        {"name":"Hot2","price":"112.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"Bedsonline"},
        {"name":"Hot3","price":"113.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"Sabre"}
      ], "day": "2/7/15"},
      {"listHotel": [
        {"name":"Hot1","price":"111.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"Bedsonline"},
        {"name":"Hot2","price":"112.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"Trivago"},
        {"name":"Hot3","price":"113.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"Sabre"}
      ], "day": "3/7/15"},
      {"listHotel": [
        {"name":"Hot1","price":"111.00","category":"3","code":"123","currency":"EUR","lat":"1.004","lon":"2.003","roomtype":"DBL","board":"RO","night":"1","company":"Bedsonline"},
        {"name":"Hot2","price":"112.00","category":"3","code":"124","currency":"EUR","lat":"1.014","lon":"2.013","roomtype":"DBL","board":"RO","night":"1","company":"Expedia"},
        {"name":"Hot3","price":"113.00","category":"3","code":"125","currency":"EUR","lat":"1.024","lon":"2.023","roomtype":"DBL","board":"RO","night":"1","company":"Sabre"}
      ], "day": "4/7/15"},
    ],
    "ida": [{
      "id": "125676789",
      "startAirport": "MAD",
      "endAirport": "MIL",
      "departureDate": "1/7/15", "departureHour": "11:00",
      "arrivalDate": "1/7/15", "arrivalHour": "12:00",
      "flightNumber": "A123", "price": "123.27", "currency": "EUR"
    },{
      "id": "452349153",
      "startAirport": "MAD",
      "endAirport": "MIL",
      "departureDate": "1/7/15", "departureHour": "13:00",
      "arrivalDate": "1/7/15", "arrivalHour": "14:00",
      "flightNumber": "A321", "price": "113.27", "currency": "EUR"
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
            "startString": null
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
            "startString": null
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
            "startString": null
        }
    ],
    "ticketOptionDays": [
      {"listTicket": [
        {"name":"Exc1", "price":"57.00", "currency":"EUR", "company":"Disney"},
        {"name":"Exc2", "price":"58.00", "currency":"EUR", "company":"Isango"},
        {"name":"Exc3", "price":"59.00", "currency":"EUR", "company":"Tab"}
      ], "day": "1/7/15"},
      {"listTicket": [
        {"name":"Exc4", "price":"157.00", "currency":"EUR", "company":"Disney"},
        {"name":"Exc5", "price":"158.00", "currency":"EUR", "company":"Isango"},
        {"name":"Exc6", "price":"159.00", "currency":"EUR", "company":"Tab"}
      ], "day": "3/7/15"},
    ],
    "vuelta": [{
      "id": "123456789",
      "startAirport": "NAP",
      "endAirport": "MAD",
      "departureDate": "5/7/15", "departureHour": "11:00",
      "arrivalDate": "5/7/15", "arrivalHour": "12:00",
      "flightNumber": "A123", "price": "123.27", "currency": "EUR"
    },{
      "id": "456789153",
      "startAirport": "NAP",
      "endAirport": "MAD",
      "departureDate": "5/7/15", "departureHour": "17:00",
      "arrivalDate": "5/7/15", "arrivalHour": "18:00",
      "flightNumber": "A456", "price": "113.27" , "currency": "EUR"     
    }]
};

    showItineraryWithRates(rates);

/*
      $.ajax(
      {
          type: 'post',
          url: 'http://172.16.24.212/isAlive',
          data:  getPoints(),
          beforeSend: function() {
            $.blockUI({ message: 'Looking for the best rates...', overlayCSS: { backgroundColor: '#876146' } }); 
          },
          success: function(data) {
            $.unblockUI();
            showItineraryWithRates(data);
          },
          error: function() {              
              $.unblockUI();
              bootbox.alert({message: 'Cant get any rates! Try again later!'});
          }
      });
*/
  });

});
