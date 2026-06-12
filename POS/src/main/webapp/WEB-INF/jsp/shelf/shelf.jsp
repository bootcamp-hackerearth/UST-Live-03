<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update Shelf</title>

<style>
body {
    font-family:'Poppins';
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}

.card {
    width:350px;
    padding:30px;
    border-radius:12px;
    background:rgba(255,255,255,0.05);
}

h2 { color:#fff; text-align:center; }

input, select {
    width:100%;
    padding:10px;
    margin:10px 0;
    border:none;
    border-radius:6px;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

button {
    width:100%;
    padding:10px;
    background:#00ffff;
    border:none;
}
</style>
</head>

<body>

<div class="card">

<h2>Update Shelf</h2>

<form:form method="post" action="/shelf/update" modelAttribute="shelfDto">

<form:hidden path="id"/>

<label>Identifier</label>
<form:input path="identifier" readonly="true"/>

<label>Status</label>
<form:select path="status">
    <form:option value="true">Active</form:option>
    <form:option value="false">Inactive</form:option>
</form:select>

<button type="submit">Update</button>

</form:form>

</div>

</body>
</html>