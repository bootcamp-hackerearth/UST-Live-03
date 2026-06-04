<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Price</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 550px;
            background: #fff;
            padding: 40px;
            border-radius: 18px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.15);
        }

        .back-btn {
            position: absolute;
            top: 20px;
            left: 25px;
            text-decoration: none;
            font-size: 32px;
            color: #4b6cb7;
            font-weight: 600;
            transition: 0.3s;
        }

        .back-btn:hover {
            color: #182848;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #4b6cb7;
            font-size: 34px;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            font-size: 16px;
        }

        .form-control {
            width: 100%;
            height: 52px;
            padding: 12px 15px;
            border-radius: 12px;
            border: 1px solid #d0d0d0;
            font-size: 15px;
            outline: none;
        }

        .form-control:focus {
            border-color: #4b6cb7;
        }

        .alert {
            padding: 12px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
            background: rgba(220, 53, 69, 0.1);
            border: 1px solid #dc3545;
            color: #dc3545;
        }

        .btn-submit {
            width: 100%;
            height: 52px;
            border: none;
            border-radius: 14px;
            cursor: pointer;
            font-size: 18px;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.95;
        }
    </style>
</head>

<body>

<div class="card-container">

    <!-- Back Button -->
    <a href="/price/list" class="back-btn">&#8592;</a>

    <h2>Add Price</h2>

    <c:if test="${not empty priceDto.message}">
        <div class="alert">
            ${priceDto.message}
        </div>
    </c:if>

    <form:form method="post"
               action="/price/add"
               modelAttribute="priceDto">

        <div class="form-group">
            <label>Product Identifier</label>
            <form:select path="identifier" cssClass="form-control">
                <form:option value="" label="-- Select Product --"/>
                <c:forEach items="${product}" var="product">
                    <form:option value="${product.identifier}">
                        ${product.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <div class="form-group">
            <label>Cost Price</label>
            <form:input path="costPrice"
                        type="number"
                        cssClass="form-control"
                        placeholder="Enter cost price"/>
        </div>

        <div class="form-group">
            <label>Selling Price</label>
            <form:input path="sellingPrice"
                        type="number"
                        cssClass="form-control"
                        placeholder="Enter selling price"/>
        </div>

        <button type="submit" class="btn-submit">
            Save Price
        </button>

    </form:form>

</div>

</body>
</html>