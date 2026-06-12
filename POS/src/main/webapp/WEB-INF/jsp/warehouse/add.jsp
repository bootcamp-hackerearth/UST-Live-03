<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Retail POS | Add Warehouse</title>

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
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
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        border-top: 5px solid var(--primary);
    }

    h2 {
        text-align: center;
        margin-bottom: 24px;
    }

    .error-message {
        background: #fff1f2;
        color: #be123c;
        padding: 12px;
        margin-bottom: 16px;
        border-radius: 6px;
        text-align: center;
    }

    label {
        font-size: 12px;
        font-weight: 600;
        color: var(--text-muted);
        margin-top: 16px;
        display: block;
    }

    input, textarea {
        width: 100%;
        padding: 12px;
        border-radius: 6px;
        border: 1px solid var(--border);
        margin-top: 6px;
        font-family: inherit;
    }

    textarea {
        resize: none;
        height: 80px;
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

    button:hover {
        background: #020617;
    }

    .back-link {
        display: block;
        margin-bottom: 10px;
        color: #475569;
        text-decoration: none;
        font-size: 14px;
    }
</style>
</head>

<body>

<div class="container">

    <a class="back-link" href="${pageContext.request.contextPath}/warehouse/list">
        ← Back
    </a>

    <h2>Add Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/warehouse/add"
               modelAttribute="warehouse">

        <label>Identifier</label>
        <form:input path="identifier" required="true"/>

        <label>Country</label>
        <form:input path="country" required="true"/>

         <label>Region</label>
                <form:input path="region" required="true"/>

        <label>Address</label>
        <form:textarea path="address" required="true"/>

        <label>Phone Number</label>
                <form:input path="phoneNo"
                            type="tel"
                            maxlength="10"
                            required="true"
                            inputmode="numeric"
                            pattern="[0-9]{10}"
                            title="Enter a valid 10-digit mobile number"
                            oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

        <button type="submit">Save Warehouse</button>

    </form:form>

</div>

</body>
</html>