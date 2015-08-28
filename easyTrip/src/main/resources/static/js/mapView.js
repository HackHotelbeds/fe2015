
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

function loadStopovers() {
    if (typeof(hb.route) == "undefined") {
        return false;
    }

    var legs = hb.route.routes[0].legs;
    var today = new Date(hb.startDate);
    var newStopOverList = '';
    for (i=0; i < legs.length; i++) {
        var leg = legs[i];
        var stays = [];
        for (s = 0; s < 5; s++) {
            stays[s] = {
                index: s,
                icon: hb.stays['leg_' + i] > s ? 'active' : ''
            }
        }
        data = {
            legIndex: i,
            date: formattedDate_ddM(today),
            startAddress: formatStopover(leg.start_address),
            endAddress: formatStopover(leg.end_address),
            stay: stays
        };

        newStopOverList += Mustache.render(MST['stopOver'], data);
        today.setDate(today.getDate() + hb.stays['leg_' + i]);
    }
    $("#stopover-list").empty().append(newStopOverList);
    registerHotelStaysClicks();
}

function updateStaysInLeg(legId) {
    var staysInLeg = hb.stays[$('#' + legId).attr('id')];

    $('#' + legId).find('.hotel-stay').each(function(index) {
        if (index < staysInLeg) {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    });
}

function formattedDate_ddM(date) {
    var day = date.getDate();
    return {
            day: (day < 10 ? '0' : '') + day,
            month: $.datepicker.formatDate('M', date)
    };
}

function formatStopover(address) {
    var addressParts = address.split(", ");
    var formattedAddress = addressParts[addressParts.length-2]+","+addressParts[addressParts.length-1];
    formattedAddress = formattedAddress.substr(formattedAddress.indexOf(" ")+1);
    return formattedAddress;
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
      var flightTimes = flight.departureDate+' '+flight.departureHour + ' - ' + flight.arrivalDate+' '+flight.arrivalHour;
      var flightPrice = '  ' + flight.price + ' ' + flight.currency;

      arrivalFlights[f] = {
        "id": flight.id,
        "text": flightText,
        "timing": flightTimes,
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
      var flightTimes = flight.departureDate + ' ' + flight.departureHour + ' - ' + flight.arrivalDate + ' ' + flight.arrivalHour;
      var flightPrice = '  ' + flight.price + ' ' + flight.currency;

      departureFlights[f] = {
        "id": flight.id,
        "text": flightText,
        "timing": flightTimes,
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
  templates = ['resultBlock', 'paymentForm', 'stopOver'];

  $.each(templates, function(ndx, tmpName){
    $.get('/mst/' + tmpName + '.mst', function(template) {
      MST[tmpName] = template;
      Mustache.parse(template);
    });
  });
}

function getMockRatesSummary() {
    return $.parseJSON(
        $.ajax({
            type: 'GET',
            url: '/mst/mockRates.json',
            dataType: 'json',
            async: false
        }).responseText
    );
}

function registerHotelStaysClicks() {
    $('.hotel-stay').on('click', function() {
        var hotelStayPicked = $(this).index() + 1;
        var legId = $(this).parents('.list-group-item').attr('id');

        hb.stays[legId] = hotelStayPicked;
//        updateStaysInLeg(legId);
        loadStopovers();
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
      $.ajax({
          //type: 'post',
          //url: 'http://localhost:9999/getItinerary',
          type: 'get',
          url: '/mst/mockRates.json',
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
