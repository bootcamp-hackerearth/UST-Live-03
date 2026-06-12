<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Add Role</title>

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
        --text-main: #0f172a;
        --text-muted: #64748b;
        --border: #e2e8f0;
        --card-bg: #ffffff;
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
        background: var(--card-bg);
        width: 100%;
        max-width: 450px;
        padding: 40px;
        border-radius: 12px;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1),
                    0 2px 4px -1px rgba(0,0,0,0.06);
        border-top: 5px solid var(--primary);
        position: relative;
    }

    .back-btn {
        position: absolute;
        top: 18px;
        left: 18px;
        text-decoration: none;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-muted);
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

    .error-message {
        padding: 12px;
        border-radius: 6px;
        margin-bottom: 20px;
        text-align: center;
        font-size: 14px;
        background: #fff1f2;
        border: 1px solid #fecdd3;
        color: #be123c;
        font-weight: 500;
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

    input, textarea {
        width: 100%;
        padding: 12px 14px;
        border: 1px solid var(--border);
        border-radius: 6px;
        font-size: 15px;
        resize: vertical;
    }

    textarea { min-height: 90px; }

    input:focus, textarea:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(15,23,42,0.1);
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
        margin-top: 20px;
    }

    button:hover {
        background-color: #020617;
    }
</style>
</head>

<body>

<div class="container">

    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/role/list" class="back-btn">
        ← Back
    </a>

    <div class="header">
        <h2>Add New Role</h2>
        <p>Create a role and define its access scope.</p>
    </div>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/role/add"
               modelAttribute="roleDto">

        <label>Role Name</label>
        <form:input path="identifier"
                    placeholder="Enter role name"
                    required="true" />

        <label style="margin-top:16px;">Description</label>
        <form:textarea path="description"
                       placeholder="Describe the role permissions and purpose"
                        required="true" />

        <button type="submit">Add Role</button>

    </form:form>

</div>

</body>
</html>