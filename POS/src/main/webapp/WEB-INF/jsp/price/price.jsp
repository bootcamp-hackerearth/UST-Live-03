<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update Price</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.container {
    width: 350px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

label {
    color: #ccc;
    font-size: 14px;
}

input {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;
    border-radius: 8px;
    border: none;
    outline: none;
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}

input:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

button {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 8px;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.cancel-btn {
    display: block;
    text-align: center;
    margin-top: 10px;
    color: #ccc;
    text-decoration: none;
}

.cancel-btn:hover {
    color: #fff;
}

.message-box {
    margin-top: 10px;
    font-size: 13px;
    color: #ffb3b3;
    text-align: center;
}
</style>
</head>

<body>

<div class="container">
    <h2>Update Price</h2>

    <form:form
        action="${pageContext.request.contextPath}/price/update"
        method="post"
        modelAttribute="priceDto">

        <form:hidden path="id"/>

        <label>Product Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Cost Price</label>
        <form:input path="costPrice" type="number" step="0.01"/>

        <label>Selling Price</label>
        <form:input path="sellingPrice" type="number" step="0.01"/>

        <button type="submit">Update</button>

        <a href="${pageContext.request.contextPath}/price/list"
           class="cancel-btn">Cancel</a>

        <c:if test="${not empty message}">
            <div class="message-box">${message}</div>
        </c:if>

    </form:form>
</div>

</body>
</html>