<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Poppins', Arial, sans-serif;
            background-color: #f4f6fb;
        }

        .card {
            width: 450px;
            background: #ffffff;
            padding: 32px;
            border-radius: 12px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.1);
        }

        h3 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 24px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            color: #444;
            display: block;
            margin-bottom: 6px;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            background: #f9fafc;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background: #ffffff;
        }

        input:disabled {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #4e73df;
            color: #ffffff;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 12px;
        }

        button:hover {
            background-color: #224abe;
        }

        .message {
            color: red;
            text-align: center;
            margin-bottom: 12px;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 12px;
            color: #4e73df;
            font-weight: 600;
            text-decoration: none;
        }
    </style>
</head>

<body>
<div class="card">
    <h3>Update Price</h3>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form:form method="post"
               action="${pageContext.request.contextPath}/price/update"
               modelAttribute="price">
        <form:hidden path="identifier"/>
        <div class="form-group">
            <label>Product</label>
            <form:input path="product" disabled="true"/>
        </div>
        <div class="form-group">
            <label>Price Type</label>
            <form:input path="priceType" disabled="true"/>
        </div>
        <div class="form-group">
            <label>Amount</label>
            <form:input path="amount"
                        type="number"
                        step="0.01"
                        min="0"
                        required="true"/>
        </div>
        <button type="submit">Update price</button>
        <a href="${pageContext.request.contextPath}/price/list" class="back-link">
            ← Back
        </a>
    </form:form>
</div>
</body>
</html>