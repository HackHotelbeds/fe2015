
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

$(document).ready(function() {
	
	var ratesSelected = parseQueryString(ratesSelectedString);
	var paymentForm = parseQueryString(paymentFormString);
	var rates = $.parseJSON(ratesFull);
	
	$('#arrival-flight-selected').html(ratesSelected);
	$('#car-selected').html('whatever...');
	$('#hotels-selected').html('whatever...');
	$('#tickets-selected').html('whatever...');
	$('#returning-flight-selected').html('whatever...');

});
