<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>Edit Node</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", Arial, sans-serif;
    background: #F6F7F9;
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}

form {
    width: 450px;
    background: #FFFFFF;
    padding: 25px;
    border-radius: 12px;
    border: 1px solid #E5E7EB;
    box-shadow: 0 8px 20px rgba(0,0,0,0.06);
}

h4 {
    text-align: center;
    color: #111827;
    margin-bottom: 18px;
}

label {
    font-weight: 600;
    color: #111827;
    display: block;
    margin-top: 10px;
}

input, select {
    width: 100%;
    padding: 10px;
    margin-top: 5px;
    border-radius: 8px;
    border: 1px solid #E5E7EB;
    font-size: 14px;
}

input:focus, select:focus {
    border-color: #2B2B2B;
    outline: none;
}

select[multiple] {
    height: 120px;
}

button {
    padding: 10px 16px;
    border-radius: 8px;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

.btn-primary {
    background: #2B2B2B;
    color: white;
}

.btn-primary:hover {
    background: #111111;
}

.btn-cancel {
    background: #E5E7EB;
    color: #111827;
}

.btn-cancel:hover {
    background: #D1D5DB;
}

.error {
    color: #B91C1C;
    font-size: 12px;
}
</style>

</head>

<body>

<c:if test="${empty node}">
    <div style="color:#B91C1C;">Node not found</div>
</c:if>

<c:if test="${not empty node}">

<form:form method="post" action="/node/update" modelAttribute="node">

<h4>Edit Node</h4>

<label>Path</label>
<form:input path="path"
            required="true"
            pattern="^/.*"
            title="Path must start with /"/>

<label>Identifier</label>
<form:input path="identifier"
            required="true"
            pattern="^[A-Za-z0-9_-]+$"
            title="Only letters, numbers, _ and - allowed"/>

<label>Roles</label>
<form:select path="roles" multiple="true" required="true">

    <c:forEach var="role" items="${roles}">
        <option value="${role.identifier}">
            ${role.identifier}
        </option>
    </c:forEach>

</form:select>

<div style="display:flex;justify-content:space-between;margin-top:15px;">

    <a href="/node/list">
        <button type="button" class="btn-cancel">Cancel</button>
    </a>

    <button type="submit" class="btn-primary">Update</button>

</div>

</form:form>

</c:if>

</body>
</html>
