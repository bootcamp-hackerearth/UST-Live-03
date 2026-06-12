<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Add Category</title>

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
        --text-main: #0f172a;
        --text-muted: #64748b;
        --border: #e2e8f0;
        --card-bg: #ffffff;
    }

    body {
        margin: 0;
        min-height: 100vh;
        font-family: system-ui, sans-serif;
        background-color: var(--bg-light);
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .container {
        background: var(--card-bg);
        width: 100%;
        max-width: 450px;
        padding: 40px;
        border-radius: 12px;
        border-top: 5px solid var(--primary);
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        position: relative;
    }

    .back-btn {
        position: absolute;
        top: 16px;
        left: 16px;
        text-decoration: none;
        font-size: 14px;
        color: var(--text-muted);
        font-weight: 600;
    }

    h2 {
        text-align: center;
        margin-bottom: 24px;
    }

    label {
        font-size: 12px;
        font-weight: 600;
        margin-top: 16px;
        display: block;
        color: var(--text-muted);
    }

    input, select {
        width: 100%;
        padding: 12px;
        margin-top: 6px;
        border-radius: 6px;
        border: 1px solid var(--border);
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

    .error-message {
        background: #fff1f2;
        color: #be123c;
        padding: 12px;
        margin-bottom: 16px;
        border-radius: 6px;
        text-align: center;
    }
</style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/category/list" class="back-btn">← Back</a>

    <h2>Add New Category</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/category/add"
               modelAttribute="categoryDto">

        <label>Category Name</label>
        <form:input path="identifier"
                    placeholder="Enter category name"
                    required="true"/>

        <label>Super Category</label>
        <form:select path="superCategory">
            <form:option value="" label="-- None (Top Level Category) --"/>
            <form:options items="${categoryList}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <button type="submit">Add Category</button>

    </form:form>

</div>

</body>
</html>