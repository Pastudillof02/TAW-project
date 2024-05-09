<%@ page import="es.uma.proyectogrupo18.ui.Usuario" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: pablo
  Date: 29/04/2024
  Time: 13:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setAttribute("usuario", new Usuario());
%>
<html>
<head>
    <title>Login</title>
</head>
<body>
<div style="text-align: center">
    <h1>Bienvenidos al login de Fit Score</h1>
    <div>
        <form:form action="/login/autentica" modelAttribute="usuario" method="post">
            ${error}
            Usuario: <form:input path="user" cssStyle="margin-bottom: 1%"/>
            </br>
            Contraseña: <form:password path="pwd" cssStyle="margin-bottom: 1%"/>
            </br>
            <form:button> Enviar </form:button>

        </form:form>
    </div>
</div>

</body>
</html>