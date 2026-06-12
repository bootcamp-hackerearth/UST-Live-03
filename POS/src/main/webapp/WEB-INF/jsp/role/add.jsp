<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>
</head>

<body>
<h2>Add New Role</h2>
<!-- Success Message -->
<c:if test="${not empty role}">
    <p style="color:green;">
        ${role}
    </p>
</c:if>
<!-- Spring Form -->
<form:form method="post"
           action="/role/add"
           modelAttribute="roleDto">
    <label>Role Name:</label>
    <form:input path="identifier"/>
    <label>Description:</label>
        <form:input path="description"/>



    <button type="submit">Add Role</button>
</form:form>
</body>
</html>
