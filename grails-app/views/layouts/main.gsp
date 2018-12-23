<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apoiasuas.util.SegurancaHelper" %>
<!doctype html>
<html lang="pt-br" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" integrity="sha384-gfdkjb5BdAXd+lj+gudLWI+BXq4IuLW5IT+brZEZsLFm++aCMlF1V92rMkPaX4PP" crossorigin="anonymous">
    <title>
        <g:layoutTitle default="Provisão Alimentar"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <asset:script src="login.js"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="menu"/>
    <asset:stylesheet src="override"/>

    <g:layoutHead/>
</head>
<body>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <asset:javascript src="application.js"/>

</body>
</html>
