<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #ffffff;
        }

        .card-container {
            width: 500px;
            background: #fff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0,0,0,0.25);
            position: relative;
        }

        h2 {
            text-align: center;
            color: #4b6cb7;
            margin-bottom: 25px;
        }

        .back-icon {
            position: absolute;
            left: 16px;
            top: 16px;
            text-decoration: none;
            font-size: 22px;
            color: #4b6cb7;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            font-weight: 500;
            font-size: 14px;
            margin-bottom: 6px;
            display: block;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            cursor: pointer;
        }

        .error-message {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            background: rgba(220,53,69,0.12);
            color: #dc3545;
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/product/list" class="back-icon">←</a>

    <h2>Add New Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="/product/add"
               modelAttribute="productDto">

        <div class="form-group">
            <label>Product Name</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        placeholder="Enter product name"/>
        </div>

        <div class="form-group">
            <label>Category</label>
            <form:select path="category"
                         cssClass="form-control">
                <form:option value="">-- Select Category --</form:option>
                <form:options items="${category}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <div class="form-group">
            <label>Supplier ID</label>
            <form:input path="supplierId"
                        cssClass="form-control"
                        placeholder="Enter supplier ID"/>
        </div>

        <button type="submit" class="btn-submit">
            Add Product
        </button>

    </form:form>

</div>

</body>
</html>