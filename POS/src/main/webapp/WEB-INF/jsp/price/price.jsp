<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #2B2B2B;
            margin-bottom: 25px;
        }

        .form-container {
            max-width: 420px;
            margin: auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        label {
            font-weight: 600;
            color: #333;
            font-size: 14px;
            display: block;
            margin-top: 15px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
            font-size: 14px;
        }

        input[readonly] {
            background: #F3F4F6;
            cursor: not-allowed;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #2B2B2B;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }

        .save-btn {
            flex: 1;
            padding: 10px;
            background: #2B2B2B;
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
        }

        .save-btn:hover {
            background: #444;
        }

        .back-btn {
            flex: 1;
            padding: 10px;
            background: #E5E7EB;
            color: #111827;
            border-radius: 6px;
            font-weight: 600;
            text-align: center;
            text-decoration: none;
        }

        .back-btn:hover {
            background: #D1D5DB;
        }
    </style>
</head>

<body>

<h2>Update Price</h2>

<div class="form-container">

    <form:form action="/price/update" method="post" modelAttribute="price">

    <label>Identifier</label>
                    <form:input path="identifier" readonly="true"/>

        <label>Product Name</label>
        <form:input path="product" readonly="true"/>

        <label>Price</label>
        <form:input path="priceAmount" type="number" step="0.01" required="true"/>

        <label>Price Type</label>
        <form:input path="priceType" required="true" readonly="true"/>

        <div class="btn-group">
            <button type="submit" class="save-btn">Update</button>
            <a href="/price/list" class="back-btn">Back</a>
        </div>

    </form:form>

</div>

</body>
</html>