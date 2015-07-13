
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

function updatePaxes() {
    var numTravellers = $('#paxes').val();

    $('.traveller').each(function(index) {
        if (index < numTravellers) {
            $('.traveller:nth-child(' + (index + 1) + ')').addClass('active');            
        } else {
            $('.traveller:nth-child(' + (index + 1) + ')').removeClass('active');
        }
    });
}

$(document).ready(function() {
    $('#slideshow').cycle({
        fx: 'fade',
        pager: '#smallnav', 
        pause:   1, 
        speed: 2000,
        timeout: 4000,
        startingSlide: Math.random() * 12
    });

    $(".datepicker").datepicker({
        defaultDate: "+2d",
        minDate: new Date(),
        numberOfMonths: 2,
        dateFormat: 'yy-mm-dd'
    });

    $("a#submit-form").on("click", function(ev){
        ev.preventDefault();
        var params = $("#form").serialize();
        window.location = ev.currentTarget.href+"?"+params;
    });

    $("#origin").geocomplete()
        .bind("geocode:result", function(event, result){
            console.log("Result: " + result.formatted_address);
            $("#origin-lat").val(result.geometry.location.A);
            $("#origin-lng").val(result.geometry.location.F);
        })
        .bind("geocode:error", function(event, status){
            console.log("ERROR: " + status);
        })
        .bind("geocode:multiple", function(event, results){
            console.log("Multiple: " + results.length + " results found");
        });
    $("#destination").geocomplete()
        .bind("geocode:result", function(event, result){
            console.log("Result: " + result.formatted_address);
            $("#dest-lat").val(result.geometry.location.A);
            $("#dest-lng").val(result.geometry.location.F);
        })
        .bind("geocode:error", function(event, status){
            console.log("ERROR: " + status);
        })
        .bind("geocode:multiple", function(event, results){
            console.log("Multiple: " + results.length + " results found");
        });

    updatePaxes();
    $('.traveller').click(function(e) {
        var travellerPicked = $(this).index() + 1;
        $('#paxes').val(travellerPicked);
        updatePaxes();
    })
});
