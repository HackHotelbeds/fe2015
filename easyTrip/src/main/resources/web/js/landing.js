
// Closes the sidebar menu
$("#menu-close").click(function(e) {
    e.preventDefault();
    $("#sidebar-wrapper").toggleClass("active");
});

// Opens the sidebar menu
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#sidebar-wrapper").toggleClass("active");
});

$(document).ready(function() {
    $('#slideshow').cycle({
        fx: 'fade',
        pager: '#smallnav', 
        pause:   1, 
        speed: 2000,
        timeout: 4000,
        startingSlide: Math.random() * 12
    });

    $("#start-date").datepicker({
        defaultDate: "+2d",
        minDate: new Date(),
        dateFormat: 'dd/mm/yy'
    });

    $("#end-date").datepicker({
        defaultDate: "+2d",
        minDate: new Date(),
        dateFormat: 'dd/mm/yy'
    });

    $("a#submit-form").on("click", function(ev){
        ev.preventDefault();
        var params = $("#form").serialize();
        window.location = ev.currentTarget.href+"?"+params;
    });
});
