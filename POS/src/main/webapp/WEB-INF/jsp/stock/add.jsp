<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Stock</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

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
            padding: 35px 40px;
            border-radius: 16px;
            background: #fff;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        /* ✅ Back Button */
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
            background: rgba(75,108,183,0.08);
            border-radius: 50%;
            transition: 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-3px) scale(1.05);
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
            font-weight: 500;
            display: block;
            margin-bottom: 6px;
        }

        .form-control {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
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

        .btn-submit:hover {
            transform: scale(1.04);
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="${pageContext.request.contextPath}/stock/list" class="back-icon">←</a>

    <h2>Add Stock</h2>

    <c:if test="${not empty message}">
        <div style="color:red;text-align:center;margin-bottom:12px;">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/stock/add"
               modelAttribute="stockDto">

        <div class="form-group">
            <label>Stock</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        placeholder="Enter stock name"/>
        </div>

        <div class="form-group">
            <label>Product</label>
            <form:select path="productName" cssClass="form-control">
                <form:option value="" label="-- Select Product --"/>
                <c:forEach items="${product}" var="product">
                    <form:option value="${product.identifier}">
                        ${product.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <div class="form-group">
            <label>Warehouse</label>
            <form:select path="warehouseName" cssClass="form-control">
                <form:option value="" label="-- Select Warehouse --"/>
                <c:forEach items="${warehouse}" var="warehouse">
                    <form:option value="${warehouse.identifier}">
                        ${warehouse.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <div class="form-group">
            <label>Quantity</label>
            <form:input path="noOfProducts"
                        type="number"
                        cssClass="form-control"
                        placeholder="Enter quantity"/>
        </div>

        <div class="form-group">
            <label>Status</label>
            <form:select path="stockStatus" cssClass="form-control">
                <form:option value="" label="-- Select Status --"/>
                <form:option value="IN_STOCK">In Stock</form:option>
                <form:option value="OUT_OF_STOCK">Out of Stock</form:option>
            </form:select>
        </div>

        <button type="submit" class="btn-submit">
            Add Stock
        </button>

    </form:form>

</div>

</body>
</html>