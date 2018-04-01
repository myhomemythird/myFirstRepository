<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Spring Web</title>
        <link rel="stylesheet" 
              type="text/css"
              href="<c:url value="/resource/style.css"/>" >
    </head>
    <body>
        <h1><s:message code="spittr.welcome" /></h1>
        
        <a href="<c:url value="/spittles" />">Spittles</a>
        <a href="<c:url value="/spitter/register" />">Register</a>
    </body>
</html>