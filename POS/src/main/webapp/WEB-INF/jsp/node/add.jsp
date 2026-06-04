<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Add Node</title>

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
        --text-main: #0f172a;
        --text-muted: #64748b;
        --border: #e2e8f0;
        --card-bg: #ffffff;
        --danger-bg: #fff1f2;
        --danger-border: #fecdd3;
        --danger-text: #be123c;
    }

    * { box-sizing: border-box; }

    body {
        margin: 0;
        min-height: 100vh;
        font-family: 'Inter', -apple-system, system-ui, sans-serif;
        background-color: var(--bg-light);
        background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
        background-size: 24px 24px;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px 20px;
    }

    .container {
        position: relative;
        background: var(--card-bg);
        width: 100%;
        max-width: 460px;
        padding: 40px;
        border-radius: 12px;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1),
                    0 2px 4px -1px rgba(0,0,0,0.06);
        border-top: 5px solid var(--primary);
    }

    .back-btn {
        position: absolute;
        top: 18px;
        left: 18px;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-muted);
        text-decoration: none;
    }

    .back-btn:hover {
        color: var(--primary);
    }

    .header {
        text-align: center;
        margin-bottom: 30px;
    }

    .header h2 {
        margin: 0;
        font-size: 24px;
        color: var(--text-main);
    }

    .header p {
        margin-top: 6px;
        font-size: 14px;
        color: var(--text-muted);
    }

    label {
        display: block;
        font-size: 12px;
        font-weight: 600;
        color: var(--text-muted);
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin-bottom: 6px;
    }

    .form-control {
        width: 100%;
        padding: 12px 14px;
        font-size: 15px;
        border: 1px solid var(--border);
        border-radius: 6px;
        margin-bottom: 18px;
    }

    .form-control:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(15,23,42,0.1);
    }

    select[multiple] {
        min-height: 130px;
    }

    button {
        width: 100%;
        padding: 14px;
        background-color: var(--primary);
        color: white;
        border: none;
        border-radius: 6px;
        font-size: 15px;
        font-weight: 600;
        cursor: pointer;
        margin-top: 10px;
    }

    button:hover {
        background-color: #020617;
    }

    .error-message {
        background: var(--danger-bg);
        border: 1px solid var(--danger-border);
        color: var(--danger-text);
        padding: 12px;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 500;
        text-align: center;
        margin-bottom: 20px;
    }

    .footer-text {
        text-align: center;
        margin-top: 25px;
        font-size: 12px;
        color: var(--text-muted);
    }
</style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/node/list" class="back-btn">
        ← Back
    </a>

    <div class="header">
        <h2>Add New Node</h2>
        <p>Create and assign roles to a system node.</p>
    </div>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/node/add"
               modelAttribute="nodeDto">

        <label>Node Name</label>
        <form:input path="identifier"
                    cssClass="form-control"
                    placeholder="Enter node name"
                    required="required"/>

        <label>Path</label>
        <form:input path="path"
                    cssClass="form-control"
                    placeholder="/api/v1/dashboard"
                    required="required"/>

        <label>Assign Roles</label>
        <form:select path="roles"
                     multiple="true"
                     required="true"
                     cssClass="form-control">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <button type="submit">
            Save Node
        </button>

    </form:form>

    <div class="footer-text">
        POS Management System
    </div>

</div>

</body>
</html>