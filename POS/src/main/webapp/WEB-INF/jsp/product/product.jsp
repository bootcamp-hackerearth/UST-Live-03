<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Update Product</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        .container {
            max-width: 450px;
            margin: auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            font-weight: 600;
            margin-top: 12px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 8px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        button {
            width: 100%;
            margin-top: 20px;
            padding: 10px;
            border: none;
            border-radius: 6px;
            background: #2B2B2B;
            color: white;
            font-size: 15px;
            cursor: pointer;
        }

        button:hover {
            background: #444;
        }

        .message {
            background: #E6F4EA;
            color: #2E7D32;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Update Product</h2>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/product/update"
               method="post"
               modelAttribute="product">

        <label>Product Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Product Name</label>
        <form:input path="name" required="true"/>

        <label>Unit</label>
        <form:select path="unit">
            <form:option value="" label="-- Select Unit --"/>
            <form:options items="${unit}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Brand</label>
        <form:select path="brand">
            <form:option value="" label="-- Select Brand --"/>
            <form:options items="${brand}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Model</label>
        <form:select path="model">
            <form:option value="" label="-- Select Model --"/>
            <form:options items="${model}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Price</label>
        <form:input path="price" type="number" step="0.01" required="true"/>

        <label>Category</label>
        <form:select path="category">
            <form:option value="" label="-- Select Category --"/>
            <form:options items="${categories}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <button type="submit">Update Product</button>

    </form:form>

</div>

</body>
</html>