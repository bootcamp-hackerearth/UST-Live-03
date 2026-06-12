<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #fff;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
            font-size: 13px;
        }

        .content {
            display: flex;
            justify-content: center;
            padding: 40px;
        }

        .card {
            width: 420px;
            background: #ffffff;
            border-radius: 18px;
            padding: 24px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 16px;
        }

        .message {
            text-align: center;
            color: green;
            font-size: 13px;
            margin-bottom: 10px;
        }

        .error {
            text-align: center;
            color: red;
            font-size: 12px;
            margin-bottom: 10px;
        }

        label {
            font-size: 12px;
            color: #374151;
            margin-bottom: 4px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 13px;
            margin-bottom: 12px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
        }

        .btn-primary {
            width: 100%;
            background: #4f46e5;
            border: none;
            border-radius: 10px;
            padding: 11px;
        }

        .btn-primary:hover {
            background: #4338ca;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-size: 12px;
            color: #4f46e5;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>Edit Price</h5>

        <button class="dashboard-btn"
                onclick="window.location.href='/'">
            Dashboard
        </button>
    </div>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="content">

    <div class="card">

        <h2>Update Price</h2>

        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>

        <c:if test="${empty priceDto}">
            <div class="error">Price not found</div>
        </c:if>

        <c:if test="${not empty priceDto}">
            <form:form action="${pageContext.request.contextPath}/price/update"
                       method="post"
                       modelAttribute="priceDto">

                <form:hidden path="id"/>

                <label>Product</label>
                <form:select path="product">
                    <form:option value="" label="Select Product"/>
                    <form:options items="${products}" itemValue="name" itemLabel="name"/>
                </form:select>

                <label>Price</label>
                <form:input path="price" type="number" step="0.01"/>

                <label>Price Type</label>
                <form:select path="priceType">
                    <form:option value="" label="Select Price Type"/>
                    <form:option value="COST_PRICE" label="Cost Price"/>
                    <form:option value="SELLING_PRICE" label="Selling Price"/>
                    <form:option value="DISCOUNT" label="Discount"/>
                </form:select>

                <button type="submit" class="btn btn-primary">
                    Update Price
                </button>

            </form:form>
        </c:if>

        <a href="${pageContext.request.contextPath}/price/list" class="back">
            ← Back to Price List
        </a>

    </div>

</div>

</body>
</html>