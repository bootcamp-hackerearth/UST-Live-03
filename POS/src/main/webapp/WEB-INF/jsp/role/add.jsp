<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Add Role</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.card {
    width: 420px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.card-header {
    text-align: center;
    margin-bottom: 20px;
}

.card-header h5 {
    color: #fff;
}

.form-label {
    font-size: 13px;
    color: #ccc;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;
    border-radius: 8px;
    border: none;
    outline: none;
    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn-primary {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 12px;
    border-radius: 6px;
    color: #00ffcc;
    font-size: 13px;
}

.card-footer {
    text-align: center;
    margin-top: 10px;
    font-size: 12px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Add New Role</h5>
    </div>

    <c:if test="${not empty role}">
        <div class="alert">
            ${role}
        </div>
    </c:if>

    <form:form method="post"
               action="/role/add"
               modelAttribute="roleDto">

        <label class="form-label">Role Name</label>
        <form:input
            path="identifier"
            cssClass="form-control"
            required="true"
            minlength="3"
            maxlength="50"
            pattern="[A-Za-z0-9 ]+"
            title="Enter valid role name"
        />

        <label class="form-label">Description</label>
        <form:input
            path="description"
            cssClass="form-control"
            required="true"
            minlength="3"
            maxlength="100"
            pattern="[A-Za-z0-9 ,.]+"
            title="Enter valid description"
        />

        <button type="submit" class="btn-primary">
            Add Role
        </button>

    </form:form>

    <div class="card-footer">
        POS Management System
    </div>

</div>

</body>
</html>