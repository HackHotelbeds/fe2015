<#import "../layout.ftl" as webLayout>

<@webLayout.webLayout>
    <#include "navigation.ftl">

    <#include "landscapImages.ftl">

    <header id="top" class="header">
        <div class="text-vertical-center">
            <div class="slogan">
                <h1>Route 99</h1>
                <h3>The easiest way to an awesome trip</h3>
            </div>
            <br>
            <!-- HERE GOES THE SEARCH FORM -->
            <#include "searchForm.ftl">
        </div>
    </header>
</@webLayout.webLayout>
