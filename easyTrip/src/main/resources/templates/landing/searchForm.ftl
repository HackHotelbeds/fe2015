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
            <#list 1..maxPax as pax>
                <div id="pax${pax}" class="traveller"></div>
            </#list>
        </ul>
        <input type="hidden" id="paxes" name="paxes" value="2"/>
    </fieldset>

    <a href="routeMap" id="submit-form" class="btn btn-dark btn-lg">Find me a trip!</a>
</form>
