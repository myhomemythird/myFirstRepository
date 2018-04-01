<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Spring Web</title>
        <link rel="stylesheet" 
              type="text/css"
              href="<c:url value="/resource/style.css"/>" >
    </head>
    <body>
        <h1>Hello Spring Web</h1>
        
        <a href="<c:url value="/spittles" />">Spittles</a>
        <a href="<c:url value="/spittles/register" />">Register</a>
    </body>
</html>