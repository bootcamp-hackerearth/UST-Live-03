<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Product</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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

.card-container {
    width: 450px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
    position: relative;
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
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

select option {
    background: #1a1b26;
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

.error-message {
    margin-bottom: 12px;
    padding: 10px;
    text-align: center;
    color: #ff8080;
    font-size: 13px;
}
</style>
</head>

<body>

<a href="/product/list" class="back-icon">←</a>

<div class="card-container">

    <h2>Add New Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="/product/add"
               modelAttribute="productDto">

        <label>Identifier</label>
        <form:input
            path="identifier"
            cssClass="form-control"
            placeholder="Enter Sku name"
            required="true"
            minlength="2"
            maxlength="50"
            pattern="[A-Za-z0-9 ]+"
            title="Enter valid identifier"
        />

        <label>Category</label>
        <form:select
            path="category"
            cssClass="form-control"
            required="true"
        >
            <form:option value="">-- Select Category --</form:option>
            <form:options items="${category}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Brand</label>
        <form:select
            path="brand"
            cssClass="form-control"
            required="true"
        >
            <form:option value="">-- Select Brand --</form:option>
            <form:options items="${brand}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Unit</label>
        <form:select
            path="unit"
            cssClass="form-control"
            required="true"
        >
            <form:option value="">-- Select Unit --</form:option>
            <form:options items="${unit}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Model</label>
        <form:select
            path="model"
            cssClass="form-control"
            required="true"
        >
            <form:option value="">-- Select Model --</form:option>
            <form:options items="${model}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Product Name</label>
        <form:input
            path="productName"
            cssClass="form-control"
            placeholder="Enter product Name"
            required="true"
            minlength="2"
            maxlength="100"
            pattern="[A-Za-z0-9 ,.]+"
            title="Enter valid product name"
        />

        <button type="submit" class="btn-submit">
            Add Product
        </button>

    </form:form>

</div>

</body>
</html>