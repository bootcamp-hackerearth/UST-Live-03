<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Unit</title>

<style>
* {
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body {
    min-height:100vh;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    justify-content:center;
    align-items:center;
}

.card-container {
    width:420px;
    padding:35px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

label {
    color:#ccc;
    font-size:13px;
}

input, select {
    width:100%;
    padding:10px;
    margin-top:6px;
    margin-bottom:15px;
    border-radius:8px;
    border:none;
    outline:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

select option {
    background:#1a1b26;
    color:#fff;
}

input:focus, select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

.btn-submit {
    width:100%;
    padding:12px;
    border-radius:8px;
    border:none;
    cursor:pointer;
    font-weight:bold;
    background:#00ffff;
    color:#000;
}

.btn-submit:hover {
    box-shadow:0 0 15px #00ffff;
}

.btn-cancel {
    display:block;
    margin-top:10px;
    text-align:center;
    padding:10px;
    border-radius:8px;
    background:#666;
    color:#fff;
    text-decoration:none;
}

.btn-cancel:hover {
    background:#555;
}

.alert-danger {
    background:rgba(255,0,0,0.15);
    color:#ff8080;
    padding:10px;
    border-radius:6px;
    margin-bottom:10px;
    text-align:center;
}
</style>
</head>

<body>

<div class="card-container">

<h2>Edit Unit</h2>

<c:if test="${empty unit}">
    <div class="alert-danger">Unit not found</div>
</c:if>

<c:if test="${not empty unit}">
    <form:form action="/unit/update" method="post" modelAttribute="unit">

        <form:hidden path="id"/>

        <label>Unit Name</label>
        <form:input
            path="identifier"
            required="true"
            minlength="2"
            maxlength="50"
            pattern="[A-Za-z ]+"
            title="Enter valid unit name"
        />

        <label>Status</label>
        <form:select path="status" required="true">
            <form:option value="true" label="Active"/>
            <form:option value="false" label="Inactive"/>
        </form:select>

        <input type="submit" value="Update Unit" class="btn-submit"/>
        <a href="/unit/list" class="btn-cancel">Cancel</a>

    </form:form>
</c:if>

</div>

</body>
</html>