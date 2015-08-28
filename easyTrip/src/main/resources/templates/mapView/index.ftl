<#import "../layout.ftl" as webLayout>

<@webLayout.webLayout>
  <div class="container">

      <#include "navigation.ftl">

      <div id="map"></div>

      <#include "stopovers.ftl">

  </div>

  <script src="http://maps.googleapis.com/maps/api/js?sensor=true&key=AIzaSyAe0IA9kruinTHHqWgHkXoANJjyspRQO08&libraries=drawing,places" type="text/javascript"></script>

</@webLayout.webLayout>

