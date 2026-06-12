<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        h2 { margin-bottom: 15px; }
        .form-container { width: 400px; }
        label { display: block; margin-top: 15px; font-weight: bold; }
        input { width: 100%; padding: 8px; margin-top: 5px; }
        .btn { margin-top: 20px; padding: 8px 14px; border: none; cursor: pointer;
               color: white; border-radius: 4px; font-size: 14px; text-decoration: none; }
        .btn-update { background-color: #007bff; }
        .btn-back { background-color: #6c757d; margin-left: 10px; }
    </style>
</head>

<body>

<h2>Edit Price</h2>

<div class="form-container">

    <!--  MUST be Spring form -->
    <form:form action="${pageContext.request.contextPath}/price/update"
               method="post"
               modelAttribute="priceDto">

        <!--  THESE WERE NOT SUBMITTED BEFORE -->
        <form:hidden path="identifier"/>
        <form:hidden path="id"/>

        <label>Cost Price</label>
        <form:input path="costPrice" type="number" step="0.10" required="true"/>

        <label>Selling Price</label>
        <form:input path="sellingPrice" type="number" step="0.10" required="true"/>

        <button type="submit" class="btn btn-update">Update</button>

        <a href="${pageContext.request.contextPath}/price/list"
           class="btn btn-back">Back to List</a>

    </form:form>

</div>

</body>
</html>
``