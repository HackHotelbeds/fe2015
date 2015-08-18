<#include "header.ftl">

<body>
<!-- Navigation -->
<a id="menu-toggle" href="#" class="btn btn-dark btn-lg toggle"><i class="fa fa-bars"></i></a>
<nav id="sidebar-wrapper">
    <ul class="sidebar-nav">
        <a id="menu-close" href="#" class="btn btn-light btn-lg pull-right toggle"><i class="fa fa-times"></i></a>
        <li class="sidebar-brand">
            <a href="#top"  onclick = $("#menu-close").click();></a>
        </li>
        <li>
            <a href="#about" onclick = $("#menu-close").click(); >About us</a>
        </li>
        <li>
            <a href="#contact" onclick = $("#menu-close").click(); >Contact</a>
        </li>
    </ul>
</nav>

<!-- Main landscape for search -->
<div id="slideshow">
    <img src="img/bg/landing-background-1.jpg" class="bgM"/>
    <img src="img/bg/landing-background-2.jpg" class="bgM"/>
    <img src="img/bg/landing-background-3.jpg" class="bgM"/>
    <img src="img/bg/landing-background-4.jpg" class="bgM"/>
    <img src="img/bg/landing-background-5.jpg" class="bgM"/>
    <img src="img/bg/landing-background-6.jpg" class="bgM"/>
    <img src="img/bg/landing-background-7.jpg" class="bgM"/>
    <img src="img/bg/landing-background-8.jpg" class="bgM"/>
    <img src="img/bg/landing-background-9.jpg" class="bgM"/>
    <img src="img/bg/landing-background-10.jpg" class="bgM"/>
    <img src="img/bg/landing-background-11.jpg" class="bgM"/>
    <img src="img/bg/landing-background-12.jpg" class="bgM"/>
</div>

<header id="top" class="header">
    <div class="text-vertical-center">
        <div class="slogan">
            <h1>Route 99</h1>
            <h3>The easiest way to an awesome trip</h3>
        </div>
        <br>
        <!-- HERE GOES THE SEARCH FORM -->
        <form id="form">
            <fieldset>
                <input type="text" name="origin" placeholder="Origin of the trip"/>
                <input type="text" name="start-date" class="datepicker" placeholder="And date"/>
            </fieldset>

            <fieldset>
                <input type="text" name="destination" placeholder="End of your trip"/>
                <input type="text" name="end-date" class="datepicker" placeholder="And date"/>
            </fieldset>

            <fieldset>
                <ul id="travellerList">
                    <div id="pax1" class="traveller"></div>
                    <div id="pax1" class="traveller"></div>
                    <div id="pax2" class="traveller"></div>
                    <div id="pax3" class="traveller"></div>
                    <div id="pax4" class="traveller"></div>
                    <div id="pax5" class="traveller"></div>
                    <div id="pax6" class="traveller"></div>
                    <div id="pax7" class="traveller"></div>
                </ul>
                <input type="hidden" id="paxes" value="2"/>
            </fieldset>
        </form>

        <a href="mapView.html" id="submit-form" class="btn btn-dark btn-lg">Find me a trip!</a>
    </div>
</header>

<#include "footer.ftl">

</body>

</html>
