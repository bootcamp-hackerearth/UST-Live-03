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
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 500px;
            background: #fff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            transition: 0.25s ease;
        }

        .btn-submit:hover {
            transform: scale(1.05);
        }

        /* ✅ Back button styling */
        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
        }
    </style>
</head>

<body>

<div class="card-container">

    <!-- ✅ Back Button -->
    <a href="/price/list" class="back-icon">←</a>

    <h2>Add Price</h2>

    <!-- ✅ Error message from backend -->
    <c:if test="${not empty priceDto.message}">
        <div class="alert">
            ${priceDto.message}
        </div>
    </c:if>

    <form:form method="post"
               action="/price/add"
               modelAttribute="priceDto">

        <!-- ✅ Product Identifier Dropdown -->
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

        <!-- ✅ Cost Price -->
        <div class="form-group">
            <label>Cost Price</label>
            <form:input path="costPrice"
                        type="number"
                        cssClass="form-control"
                        placeholder="Enter cost price"
                        required="true"/>
        </div>

        <!-- ✅ Selling Price -->
        <div class="form-group">
            <label>Selling Price</label>
            <form:input path="sellingPrice"
                        type="number"
                        cssClass="form-control"
                        placeholder="Enter selling price"
                        required="true"/>
        </div>

        <button type="submit" class="btn-submit">
            Save Price
        </button>

    </form:form>

</div>

</body>
</html>