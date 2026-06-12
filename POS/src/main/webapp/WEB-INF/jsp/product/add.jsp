<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background-color: #f4f6f8;
        }

        h2 {
            margin-bottom: 15px;
        }

        .form-container {
            width: 420px;
            background: #fff;
            padding: 20px;
            border-radius: 6px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        input, textarea, select {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
        }

        textarea {
            resize: vertical;
        }

        .btn {
            margin-top: 20px;
            padding: 8px 14px;
            border: none;
            cursor: pointer;
            color: white;
            border-radius: 4px;
        }

        .btn-save {
            background-color: #28a745;
        }

        .btn-back {
            background-color: #6c757d;
            text-decoration: none;
            padding: 8px 14px;
            margin-left: 10px;
            border-radius: 4px;
            color: white;
        }

        .message {
            margin-bottom: 15px;
            font-weight: bold;
        }

        .success {
            color: green;
        }

        .error {
            color: red;
        }
    </style>
</head>

<body>

<h2>Add Product</h2>

<!--  Success / Error Message -->
<c:if test="${not empty message}">
    <div class="message ${success ? 'success' : 'error'}">
        ${message}
    </div>
</c:if>

<div class="form-container">

    <form:form action="${pageContext.request.contextPath}/product/add"
               method="post"
               modelAttribute="productDto">

        <!-- PRODUCT NAME -->
        <label>Product Name</label>
        <form:input path="identifier" required="required"/>

        <!-- DESCRIPTION -->
        <label>Description</label>
        <form:textarea path="description" rows="3"/>

        <!-- CATEGORY -->
        <label>Category</label>
        <form:select path="category" required="required">
            <option value="">-- Select Category --</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.identifier}">
                    ${cat.identifier}
                </option>
            </c:forEach>
        </form:select>

        <!-- BRAND -->
        <label>Brand</label>
        <form:select path="brand" required="required">
            <option value="">-- Select Brand --</option>
            <c:forEach var="brand" items="${brand}">
                <option value="${brand.identifier}">
                    ${brand.identifier}
                </option>
            </c:forEach>
        </form:select>

        <!-- STATUS -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">ACTIVE</form:option>
            <form:option value="false">INACTIVE</form:option>
        </form:select>

        <!-- BUTTONS -->
        <button type="submit" class="btn btn-save">Save</button>

        <a href="${pageContext.request.contextPath}/product/list"
           class="btn-back">Back to List</a>

    </form:form>

</div>

</body>
</html>