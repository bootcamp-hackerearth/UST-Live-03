<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Add Product</title>

    <style>
        :root {
            --primary: #0f172a;
            --bg-light: #f8fafc;
            --text-muted: #64748b;
            --border: #e2e8f0;
            --card-bg: #ffffff;
            --error: #be123c;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: system-ui, sans-serif;
            background-color: var(--bg-light);
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 30px;
        }

        .container {
            background: var(--card-bg);
            width: 100%;
            max-width: 480px;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            border-top: 5px solid var(--primary);
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: var(--primary);
        }

        label {
            font-size: 12px;
            font-weight: 600;
            color: var(--text-muted);
            margin-top: 14px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid var(--border);
            margin-top: 6px;
            font-size: 14px;
        }

        select[multiple] {
            height: 120px;
        }

        .error-message {
            color: var(--error);
            font-size: 12px;
            margin-top: 4px;
        }

        button {
            width: 100%;
            margin-top: 24px;
            padding: 14px;
            background: var(--primary);
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            font-size: 14px;
            color: var(--text-muted);
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/product/list"
       class="back-link">← Back to Product List</a>

    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/product/add"
               modelAttribute="productDto">

        <label>Product Name *</label>
        <form:input path="identifier" required="true" minlength="2" maxlength="50"/>
        <form:errors path="identifier" cssClass="error-message"/>

        <label>Categories *</label>
        <form:select path="category" multiple="true" required="true">
            <form:options items="${categoryList}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
        <form:errors path="category" cssClass="error-message"/>

        <label>Model *</label>
        <form:select path="models" required="true">
            <form:option value="" label="-- Select Model --"/>
            <form:options items="${models}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
        <form:errors path="models" cssClass="error-message"/>

        <label>Brand *</label>
        <form:select path="brand" required="true">
            <form:option value="" label="-- Select Brand --"/>
            <form:options items="${brand}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
        <form:errors path="brand" cssClass="error-message"/>

        <label>Unit *</label>
        <form:select path="unit" required="true">
            <form:option value="" label="-- Select Unit --"/>
            <form:options items="${unit}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
        <form:errors path="unit" cssClass="error-message"/>

        <button type="submit">Add Product</button>

    </form:form>

</div>

</body>
</html>