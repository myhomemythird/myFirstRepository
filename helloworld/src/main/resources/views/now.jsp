<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<html>
 <head>
  <title>Hello Spring Boot MVC!</title>
  <link rel="stylesheet" type="text/css" href="<c:url value="/resources/style.css" />">
 </head>
 <body>
  <h1>Welcome to Spring Boot</h1>
  <a href="<c:url value="/spittles" />">Spittles ${now}</a>
  <a href="<c:url value="/spittles/register" />">Register</a>
 </body>
</html>