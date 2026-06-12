<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Warehouse</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.card-container {
    width: 420px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #fff;
}

.back-icon {
    position: absolute;
    top: 20px;
    left: 20px;
    color: #00ffff;
    text-decoration: none;
    font-size: 20px;
}

label {
    color: #ccc;
    font-size: 14px;
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

.btn-submit {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;

    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-submit:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 10px;
    border-radius: 6px;
    color: #00ffcc;
}

.error-message {
    text-align: center;
    padding: 10px;
    margin-bottom: 10px;
    border-radius: 6px;
    color: #ff8080;
}
</style>
</head>

<body>

<div class="card-container">

    <a href="/warehouse/list" class="back-icon">←</a>

    <h2>Add New Warehouse</h2>

    <c:if test="${not empty warehouse}">
        <div class="alert">${warehouse}</div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form method="post"
               action="/warehouse/add"
               modelAttribute="warehouseDto">

        <label>Warehouse Name</label>
        <form:input path="identifier"
                    cssClass="form-control"
                    placeholder="Enter warehouse name"/>

        <label>Country</label>
        <form:input path="country"
                    cssClass="form-control"
                    placeholder="Enter country"/>

        <label>Pincode</label>
        <form:input path="Pincode"
                    cssClass="form-control"
                    placeholder="Enter pincode"/>

        <label>Address</label>
        <form:input path="address"
                    cssClass="form-control"
                    placeholder="Enter address"/>

        <button type="submit" class="btn-submit">
            Add Warehouse
        </button>

    </form:form>

</div>

</body>
</html>