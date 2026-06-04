<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }
        .form-container {
            width: 400px;
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
        .btn-save { background-color: #007bff; }
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
        .success { color: green; }
        .error { color: red; }
    </style>
</head>

<body>

<h2>Edit Product</h2>

<!--  MESSAGE DISPLAY -->
<c:if test="${not empty message}">
    <div class="message ${success ? 'success' : 'error'}">
        ${message}
    </div>
</c:if>

<div class="form-container">

    <form:form action="/product/update"
               method="post"
               modelAttribute="productDto">

        <!--  HIDDEN ID (VERY IMPORTANT) -->
        <form:hidden path="id"/>

        <!-- PRODUCT Identifier -->
        <label>Product Identifier</label>
        <form:input path="identifier" required="required" readonly="true"/>

         <!-- PRODUCT NAME -->
                <label>Product Name</label>
                <form:input path="productName"/>

        <!-- DESCRIPTION -->
        <label>Description</label>
        <form:textarea path="description" rows="3"/>

        <!-- CATEGORY DROPDOWN -->
        <label>Category</label>
        <form:select path="category" >

            <option value="">-- Select Category --</option>

            <c:forEach var="cat" items="${categories}">
                <option value="${cat.identifier}"
                    <c:if test="${productDto.category == cat.identifier}">
                        selected
                    </c:if>>
                    ${cat.identifier}
                </option>
            </c:forEach>

        </form:select>


 <label>Brand</label>
        <form:select path="brand" >
            <option value="">-- Select Brand --</option>
            <c:forEach var="b" items="${brands}">
                <option value="${b.identifier}"
                    <c:if test="${productDto.brand == b.identifier}">
                        selected
                    </c:if>>
                    ${b.identifier}
                </option>
            </c:forEach>
        </form:select>

         <label>Model</label>
                <form:select path="model" >
                    <option value="">-- Select Brand --</option>
                    <c:forEach var="m" items="${models}">
                        <option value="${m.identifier}"
                            <c:if test="${productDto.model == m.identifier}">
                                selected
                            </c:if>>
                            ${m.identifier}
                        </option>
                    </c:forEach>
                </form:select>


        <!-- STATUS -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">InActive</form:option>
        </form:select>

        <!--  BUTTONS -->
        <button type="submit" class="btn btn-save">Update</button>

        <a href="${pageContext.request.contextPath}/product/list"
           class="btn-back">Back to List</a>

    </form:form>

</div>

</body>
</html>