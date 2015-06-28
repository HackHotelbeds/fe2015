
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
            bootbox.dialog({
              title: '<h4>Choose the rates</h4>',
              message: '....',
              buttons: {
                        cancel: {
                          label: "Back to the map",
                          className: "btn-cancel"
                        },
                        success: {
                            label: "Go pay!",
                            className: "btn-success",
                            callback: function () {
                                bootbox.alert('...show paying form...');
                            }
                        }
              }
            });
          },
          error: function() {              
              $.unblockUI();
              bootbox.alert({message: 'Cant get any rates! Try again later!'});
          }
      });
  });

});
