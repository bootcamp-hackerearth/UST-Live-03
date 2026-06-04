<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Product</title>

    <style>
        body {
            font-family: system-ui, sans-serif;
            background-color: #f1f5f9;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 100%;
            max-width: 460px;
            background: #ffffff;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #0f172a;
        }

        .input-group {
            margin-bottom: 18px;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 6px;
            display: block;
            color: #64748b;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #cbd5e1;
        }

        select[multiple] {
            height: 120px;
        }

        input[readonly] {
            background: #f8fafc;
            color: #64748b;
        }

        .error-text {
            color: #be123c;
            font-size: 12px;
            margin-top: 4px;
        }

        button {
            width: 100%;
            padding: 14px;
            border-radius: 10px;
            background: #1e293b;
            color: white;
            font-size: 16px;
            font-weight: 600;
            border: none;
            cursor: pointer;
        }

        .link-btn {
            display: block;
            margin-top: 22px;
            text-align: center;
            font-size: 14px;
            color: #64748b;
            text-decoration: none;
        }

        .bottom-error {
            margin-top: 20px;
            padding: 12px;
            text-align: center;
            border-radius: 8px;
            background: #fff1f2;
            color: #be123c;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Product</h2>

    <form:form action="${pageContext.request.contextPath}/product/update"
               method="post"
               modelAttribute="product">

        <form:hidden path="id"/>

        <div class="input-group">
            <label>Product Name</label>
            <form:input path="identifier" readonly="true"/>
            <form:errors path="identifier" cssClass="error-text"/>
        </div>

        <div class="input-group">
            <label>Categories *</label>
            <form:select path="category" multiple="true" required="true">
                <form:options items="${categoryList}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="category" cssClass="error-text"/>
        </div>

        <div class="input-group">
            <label>Model *</label>
            <form:select path="models" required="true">
                <form:option value="" label="-- Select Model --"/>
                <form:options items="${models}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="models" cssClass="error-text"/>
        </div>

        <div class="input-group">
            <label>Brand *</label>
            <form:select path="brand" required="true">
                <form:option value="" label="-- Select Brand --"/>
                <form:options items="${brand}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="brand" cssClass="error-text"/>
        </div>

        <div class="input-group">
            <label>Unit *</label>
            <form:select path="unit" required="true">
                <form:option value="" label="-- Select Unit --"/>
                <form:options items="${unit}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="unit" cssClass="error-text"/>
        </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/product/list" class="link-btn">
            ← Back to Product List
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="bottom-error">${message}</div>
    </c:if>

</div>

</body>
</html>