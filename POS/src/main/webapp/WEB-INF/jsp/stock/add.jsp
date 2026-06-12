<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <style>

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

        .card {
            width: 420px;
            margin: 100px auto;
            background: #fff;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
            color: #444;
        }

        input,
        select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 14px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #007bff;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 13px;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: bold;
            font-size: 14px;
        }

        .btn-submit:hover {
            background-color: #0056b3;
        }

    </style>
</head>

<body>

<a href="/stock/list" class="back-btn">
    ← Back
</a>

<div class="card">

    <h2>Add New Product to Stock</h2>

    <!-- ERROR MESSAGE -->
    <c:if test="${not empty message}">
        <div class="error-msg">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="/stock/add"
               modelAttribute="stocks">

        <!-- PRODUCT -->
        <label>Product</label>

        <form:select path="product"
                     required="true">

            <form:option value="">
                -- Select Product --
            </form:option>

            <form:options items="${products}"
                          itemValue="name"
                          itemLabel="name"/>

        </form:select>

        <!-- WAREHOUSE -->
        <label>Warehouse</label>

        <form:select path="warehouse"
                     required="true">

            <form:option value="">
                -- Select Warehouse --
            </form:option>

            <form:options items="${warehouses}"
                          itemValue="name"
                          itemLabel="name"/>

        </form:select>

        <!-- QUANTITY -->
        <label>Quantity</label>

        <form:input path="quantity"
                    type="number"
                    placeholder="Enter quantity"
                     title="Enter minimum stock"
                    required="true"/>

        <!-- MINIMUM STOCK -->
        <label>Minimum Stock</label>

        <form:input path="minimumStock"
                    type="number"
                    placeholder="Enter minimum stock"
                    title="Enter minimum stock"
                    required="true"/>

        <!-- SUBMIT -->
        <input type="submit"
               value="Add Product"
               class="btn-submit"/>

    </form:form>

</div>

</body>
</html>