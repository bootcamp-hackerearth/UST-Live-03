<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Stock</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    background-color: #f1f5f9;
    background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
    background-size: 24px 24px;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    color: #334155;
}

.container {
    width: 100%;
    max-width: 420px;
    background: #ffffff;
    padding: 40px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1),
                0 10px 10px -5px rgba(0,0,0,0.04);
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-weight: 700;
    font-size: 24px;
    color: #0f172a;
}

.input-group {
    margin-bottom: 20px;
}

label {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 8px;
    display: block;
    color: #64748b;
}

input {
    width: 100%;
    padding: 12px 16px;
    border-radius: 10px;
    border: 1px solid #cbd5e1;
    background: #ffffff;
    color: #1e293b;
    font-size: 15px;
}

input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 0 4px rgba(99,102,241,0.1);
}

button {
    width: 100%;
    padding: 14px;
    margin-top: 10px;
    border-radius: 10px;
    border: none;
    background: #1e293b;
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

button:hover {
    background: #0f172a;
}

.bottom-error {
    margin-top: 20px;
    padding: 12px;
    text-align: center;
    border-radius: 8px;
    background: #fff1f2;
    border: 1px solid #fecdd3;
    color: #be123c;
    font-size: 14px;
    font-weight: 500;
}

.link-btn {
    display: block;
    text-align: center;
    margin-top: 25px;
    color: #64748b;
    text-decoration: none;
    font-size: 14px;
    font-weight: 500;
}

.link-btn:hover {
    color: #4f46e5;
}
</style>
</head>

<body>

<div class="container">

    <h2>Edit Stock</h2>

    <form:form action="${pageContext.request.contextPath}/stock/update"
               method="post"
               modelAttribute="stock">

        <form:hidden path="id"/>

        <div class="input-group">
            <label>Stock Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="input-group">
            <label>Quantity</label>
            <form:input path="quantity" type="number" min="0" required="true"/>
        </div>

        <div class="input-group">
            <label>Minimum Stock Level</label>
            <form:input path="minimumstock" type="number" min="0" required="true"/>
        </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/stock/list"
           class="link-btn">
            ← Back to Stock List
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="bottom-error">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>