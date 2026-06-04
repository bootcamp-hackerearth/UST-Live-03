<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background-color: #f4f6fb;
            font-family: 'Poppins', Arial, sans-serif;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .card {
            width: 560px;
            background-color: #ffffff;
            border-radius: 12px;
            padding: 32px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        h3 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 28px;
            font-weight: 700;
        }
        .form-group {
            display: grid;
            grid-template-columns: 150px 1fr;
            align-items: center;
            gap: 14px;
            margin-bottom: 18px;
        }
        label {
            font-size: 14px;
            font-weight: 600;
            color: #444;
        }
        input, select {
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            font-size: 14px;
            width: 100%;
        }

        select[multiple] {
            height: 120px;
        }

        button {
            display: block;
            width: 60%;
            margin: 20px auto 0;
            padding: 10px;
            background-color: #4e73df;
            border: none;
            border-radius: 8px;
            color: #ffffff;
            font-size: 14px;
            font-weight: 600;
        }

        .back-btn {
            display: block;
            width: 60%;
            margin: 12px auto 0;
            padding: 8px;
            text-align: center;
            border-radius: 8px;
            border: 2px solid #4e73df;
            color: #4e73df;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
        }
    </style>
</head>

<body>
<div class="card">
    <h3>Add Product</h3>
    <c:if test="${not empty message}">
        <div class="alert alert-danger">
            ${message}
        </div>
    </c:if>
    <form:form method="post"
               action="${pageContext.request.contextPath}/product/add"
               modelAttribute="productDto">
        <div class="form-group">
            <label>Identifier</label>
            <form:input path="identifier" required="required"/>
        </div>
        <div class="form-group">
            <label>Product Name</label>
            <form:input path="productName" required="required"/>
        </div>
        <div class="form-group">
            <label>Category</label>
            <form:select path="category" multiple="true" required="required">
                <c:forEach items="${categories}" var="cat">
                    <form:option value="${cat.name}">
                        ${cat.name}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>
        <div class="form-group">
            <label>Brand</label>
            <form:select path="brand" multiple="true" required="required">
                <c:forEach items="${brands}" var="brand">
                    <form:option value="${brand.brandName}">
                        ${brand.brandName}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>
        <div class="form-group">
            <label>Model</label>
            <form:select path="model" multiple="true" required="required">
                <c:forEach items="${model}" var="model">
                    <form:option value="${model.modelName}">
                        ${model.modelName}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>
        <div class="form-group">
            <label>Description</label>
            <form:input path="description" required="required"/>
        </div>
        <div class="form-group">
            <label>Status</label>
            <form:select path="status">
                <form:option value="true">
                    Active
                </form:option>
                <form:option value="false">
                    Inactive
                </form:option>
            </form:select>
        </div>
        <button type="submit">
            Add Product
        </button>
    </form:form>
    <a href="${pageContext.request.contextPath}/product/list"
       class="back-btn">
        ← Back to Product List
    </a>
</div>
</body>
</html>