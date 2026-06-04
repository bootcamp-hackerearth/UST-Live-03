<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

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

        input[readonly] {
            background-color: #f8f9fa;
            color: #666;
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

        .btn-container {
            display: flex;
            justify-content: space-between;
            margin-top: 25px;
        }

        .btn {
            padding: 12px 18px;
            border: none;
            border-radius: 6px;
            text-decoration: none;
            color: white;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
        }

        .btn-cancel {
            background-color: #6c757d;
        }

        .btn-update {
            background-color: #007bff;
        }

        .btn-update:hover {
            background-color: #0056b3;
        }

        .btn-cancel:hover {
            background-color: #5a6268;
        }

    </style>
</head>

<body>

<a href="/price/list" class="back-btn">
    ← Back
</a>

<div class="card">

    <h2>Edit Price</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">
            ${message}
        </div>
    </c:if>

    <c:if test="${empty priceDto}">
        <div class="error-msg">
            Price not found
        </div>
    </c:if>

    <c:if test="${not empty priceDto}">

        <form:form action="/price/update"
                   method="post"
                   modelAttribute="priceDto">

            <form:hidden path="identifier"/>

            <label>Product</label>

            <form:input path="product"
                        readonly="true"/>

            <form:hidden path="product"/>

            <label>Price Amount</label>

            <form:input path="priceAmount"
                        type="number"
                        step="0.01"
                        placeholder="Enter Price"
                        required="true"/>

            <label>Price Type</label>

            <form:input path="priceType"
                        placeholder="Enter Price Type"
                        required="true"/>

            <div class="btn-container">

                <a href="/price/list"
                   class="btn btn-cancel">
                    Cancel
                </a>

                <button type="submit"
                        class="btn btn-update">
                    Update
                </button>

            </div>

        </form:form>

    </c:if>

</div>

</body>
</html>