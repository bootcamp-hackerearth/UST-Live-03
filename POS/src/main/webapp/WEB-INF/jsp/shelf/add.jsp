<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Shelf</title>

<style>
body {
    font-family: 'Poppins', sans-serif;
    background: linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}

.card {
    width:380px;
    padding:30px;
    border-radius:12px;
    background:rgba(255,255,255,0.05);
}

h2 { color:#fff; text-align:center; }

label { color:#ccc; font-size:13px; }

input, select {
    width:100%;
    padding:10px;
    margin:10px 0;
    border-radius:6px;
    border:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

button {
    width:100%;
    padding:10px;
    background:#00ffff;
    border:none;
    border-radius:6px;
}
</style>
</head>

<body>

<div class="card">

<h2>Add Shelf</h2>

<c:if test="${not empty message}">
    <div style="color:red;text-align:center">${message}</div>
</c:if>

<form:form method="post" action="/shelf/add" modelAttribute="shelf">

    <label>Identifier</label>
    <form:input path="identifier"/>

    <label>Status</label>
    <form:select path="status">
        <form:option value="true">Active</form:option>
        <form:option value="false">Inactive</form:option>
    </form:select>

    <button type="submit">Save</button>

</form:form>

</div>

</body>
</html>