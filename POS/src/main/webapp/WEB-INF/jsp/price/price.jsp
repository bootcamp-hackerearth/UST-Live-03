<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

    <style>
        body { font-family: Arial; margin: 30px; }
        .form-container { width: 400px; }
        label { display: block; margin-top: 15px; font-weight: bold; }
        input, select { width: 100%; padding: 8px; margin-top: 5px; }
        .btn { margin-top: 20px; padding: 8px 14px; border: none; color: white; border-radius: 4px; }
        .btn-update { background: blue; }
        .btn-back { background: gray; margin-left: 10px; }
    </style>
</head>

<body>

<h2>Edit Price</h2>

<div class="form-container">

    <form:form action="${pageContext.request.contextPath}/price/update"
               method="post"
               modelAttribute="priceDto">

        <!-- HIDDEN FIELDS -->
        <form:hidden path="id"/>
        <form:hidden path="identifier"/>

        <!-- PRODUCT (READ ONLY DISPLAY) -->
        <label>Product</label>
        <input type="text" value="${priceDto.identifier}" readonly />

        <!-- COST PRICE -->
        <label>Cost Price</label>
        <form:input path="costPrice" type="number" step="0.10" required="true"/>

        <!-- SELLING PRICE -->
        <label>Selling Price</label>
        <form:input path="sellingPrice" type="number" step="0.10" required="true"/>

         <!-- MRP -->
                <label>MRP</label>
                <form:input path="mrp" type="number" step="0.10" required="true"/>

        <button type="submit" class="btn btn-update">Update</button>

        <a href="${pageContext.request.contextPath}/price/list"
           class="btn btn-back">Back</a>

    </form:form>

</div>

</body>
</html>