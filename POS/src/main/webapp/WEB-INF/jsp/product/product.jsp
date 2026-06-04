<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>

    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', Arial, sans-serif;
            background-color: #f4f6fb;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background-color: #ffffff;
            padding: 32px;
            border-radius: 12px;
            width: 420px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.10);
        }

        h2 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 24px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            color: #444;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            font-size: 14px;
            background-color: #f9fafc;
        }

        select[multiple] {
            height: 120px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background-color: #ffffff;
        }

        .btn {
            width: 100%;
            padding: 10px;
            background-color: #4e73df;
            color: #ffffff;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 6px 14px rgba(78,115,223,0.35);
            margin-top: 12px;
        }

        .btn:hover {
            background-color: #224abe;
        }

        .message {
            color: red;
            text-align: center;
            margin-bottom: 12px;
            font-size: 14px;
        }

        .back-link {
            display: block;
            margin-top: 14px;
            text-align: center;
            text-decoration: none;
            color: #4e73df;
            font-weight: 600;
            font-size: 14px;
        }
    </style>
</head>

<body>
<div class="container">
    <h2>Update Product</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form:form method="post"
               action="${pageContext.request.contextPath}/product/update"
               modelAttribute="product">
        <div class="form-group">
            <label>Identifier</label>
            <form:input path="identifier" readonly="true"/>
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
            <form:select path="status" cssClass="form-control">
                <form:option value="true">
                    Active
                </form:option>
                <form:option value="false">
                    Inactive
                </form:option>
            </form:select>
        </div>
        <button type="submit" class="btn">
            Update Product
        </button>
    </form:form>
    <a href="${pageContext.request.contextPath}/product/list"
       class="back-link">
        Back to List
    </a>
</div>
</body>
</html>