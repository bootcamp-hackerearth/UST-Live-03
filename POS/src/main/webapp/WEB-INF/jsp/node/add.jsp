<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>Add Node</title>

<style>
body {
    background: #F6F7F9;
    font-family: Arial;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.card {
    width: 420px;
    background: #FFFFFF;
    padding: 25px;
    border-radius: 12px;
    border: 1px solid #E5E7EB;
}

input, select {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #E5E7EB;
    margin-bottom: 10px;
}

input:focus, select:focus {
    border-color: #2B2B2B;
    outline: none;
}

button {
    width: 100%;
    padding: 10px;
    background: #2B2B2B;
    color: white;
    border: none;
    border-radius: 8px;
}

button:hover {
    background: #111111;
}
</style>
</head>

<body>

<div class="card">

<h2>Add Node</h2>

<c:if test="${not empty message}">
<div style="color:#166534;text-align:center;">${message}</div>
</c:if>

<form:form method="post" action="/node/add" modelAttribute="nodeDto">

<form:input path="identifier" placeholder="Identifier"/>
<form:input path="path" placeholder="Path"/>

<form:select path="roles" multiple="true">
    <c:forEach var="role" items="${roles}">
        <option value="${role.identifier}">${role.identifier}</option>
    </c:forEach>
</form:select>

<button type="submit">Add Node</button>


</form:form>

</div>

</body>
</html>
