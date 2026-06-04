<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Stock</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

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
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75, 108, 183, 0.08);
            transition: all 0.25s ease;
        }

        .home-link:hover {
            background: #4b6cb7;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
            transform: translateY(-2px);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 18px;
            position: relative;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
            font-family: 'Poppins', sans-serif;
            transition: all 0.2s ease;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.15);
        }

        input[readonly] {
            background: #f5f5f5;
        }

        select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            background-color: #ffffff;
            cursor: pointer;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg fill='%234b6cb7' height='20' viewBox='0 0 20 20' width='20' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M5 7l5 5 5-5z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 12px center;
            background-size: 16px;
        }

        .btn-submit {
            margin-top: 10px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.25s ease;
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(75, 108, 183, 0.35);
        }

        .btn-cancel {
            margin-top: 10px;
            width: 100%;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            transition: all 0.2s ease;
        }

        .btn-cancel:hover {
            background: #e0e0e0;
        }

    </style>
</head>

<body>

<div class="card-container">

    <a href="/stock/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Edit Stock</h2>

    <c:if test="${empty stockDto}">
        <div style="color:red; text-align:center; margin-bottom:15px;">
            Stock not found
        </div>
    </c:if>

    <c:if test="${not empty stockDto}">
        <form:form action="/stock/update"
                   method="post"
                   modelAttribute="stockDto">

            <form:hidden path="id"/>

            <div class="form-group">
                <label>Stock Name</label>
                <form:input path="identifier" readonly="true"/>
            </div>

            <div class="form-group">
                <label>Stock Status</label>
                <form:select path="stockStatus">
                    <form:option value="" label="Select stock status"/>
                    <form:option value="IN_STOCK" label="In Stock"/>
                    <form:option value="OUT_OF_STOCK" label="Out of Stock"/>
                </form:select>
            </div>

        <div class="form-group">
            <label>Product Name</label>
            <form:select path="productName" required="required">
                <form:option value="" label="-- Select Product --"/>
                <c:forEach items="${products}" var="product">
                    <form:option value="${product.identifier}">
                        ${product.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <div class="form-group">
            <label>Warehouse Name</label>
            <form:select path="warehouseName" required="required">
                <form:option value="" label="-- Select Warehouse --"/>
                <c:forEach items="${warehouses}" var="warehouse">
                    <form:option value="${warehouse.identifier}">
                        ${warehouse.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

            <div class="form-group">
                <label>Number of Stocks</label>
                <form:input path="quantity" required="required" placeholder="Enter Number of Stocks"/>
            </div>

            <input type="submit" value="Update Stock" class="btn-submit"/>

            <a href="/stock/list" class="btn-cancel">Cancel</a>

        </form:form>
    </c:if>

</div>

</body>
</html>