
<!-- jQuery -->
<script src="js/jquery-2.1.1.min.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="js/bootstrap.min.js"></script>
<#list customJsList as customJs>
    <script src="js/${customJs}.js"></script>
</#list>
