<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Price</title>

    <style>
        body {
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        .form-label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            font-size: 14px;
            background: rgba(255,255,255,0.1);
            color: #fff;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        .btn-primary {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 14px;
            margin-top: 10px;
        }

        .btn-primary:hover {
            opacity: 0.9;
        }

        .btn-back {
            width: 100%;
            padding: 10px;
            border-radius: 20px;
            border: none;
            background: rgba(255,255,255,0.2);
            color: #fff;
            font-size: 14px;
            margin-top: 10px;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.3);
        }

        .message-box {
            margin-top: 10px;
            font-size: 13px;
            color: #ffb3b3;
            text-align: center;
        }

        .footer {
            margin-top: 10px;
            text-align: center;
            font-size: 12px;
            color: #ddd;
        }
        form select {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            font-size: 14px;
            background: rgba(255,255,255,0.1);
            color: #fff;
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
        }

        form select:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        form select option {
            color: #000;
        }
        form input[type="date"] {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            font-size: 14px;
            background: rgba(255,255,255,0.1);
            color: #fff;
        }

        form input[type="date"]:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }
    </style>
</head>

<body>

<div class="card">

    <h4>Add New Price</h4>

    <c:if test="${not empty price}">
        <div style="text-align:center; color:#b6ffb6; margin-bottom:10px;">
            ${price}
        </div>
    </c:if>

    <form:form method="post" action="/price/add" modelAttribute="priceDto">

        <div class="mb-3">
            <label class="form-label">Product Name</label>

                    <form:select path="identifier" required="true">
                        <form:option value="">-- Select Product --</form:option>
                        <form:options items="${products}" itemValue="identifier" itemLabel="identifier"/>
                    </form:select>

            <label class="form-label">Cost Price</label>
            <form:input path="costPrice" placeholder="Enter Cost Price" required="true"/>

            <label class="form-label">Selling Price</label>
            <form:input path="sellingPrice" placeholder="Enter Selling Price" required="true"/>

            <label class="form-label">Discount</label>
            <form:input path="discount" placeholder="Enter Discount Percent" required="true"/>

        </div>

        <button type="submit" class="btn-primary">
            Add Price
        </button>

        <button type="button" class="btn-back"
                onclick="window.location.href='/price/list'">
            Back to Price List
        </button>

        <c:if test="${not empty message}">
            <div class="message-box">${message}</div>
        </c:if>

    </form:form>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>