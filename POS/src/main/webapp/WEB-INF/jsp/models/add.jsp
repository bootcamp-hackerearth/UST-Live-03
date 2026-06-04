<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Add Model</title>

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
            font-family: 'Inter', system-ui, sans-serif;
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
            max-width: 420px;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
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

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h2 {
            margin-bottom: 6px;
            color: var(--text-main);
        }

        .header p {
            font-size: 14px;
            color: var(--text-muted);
        }

        label {
            display: block;
            font-size: 12px;
            font-weight: 600;
            color: var(--text-muted);
            text-transform: uppercase;
            margin-top: 16px;
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid var(--border);
            border-radius: 6px;
            font-size: 15px;
        }

        button {
            width: 100%;
            padding: 14px;
            background-color: var(--primary);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 24px;
        }

        .error-message {
            margin-bottom: 15px;
            color: #dc2626;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/models/list" class="back-btn">
        ← Back
    </a>

    <div class="header">
        <h2>Add New Model</h2>
        <p>Create a model with name and status</p>
    </div>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/models/add"
               modelAttribute="modelsDto">

        <label>Model Name</label>
        <form:input path="identifier"
                    placeholder="Enter model name"
                    required="true"/>

        <label>Status</label>
        <form:select path="status" required="true">
            <form:option value="">-- Select Status --</form:option>
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <label>Description</label>
                <form:textarea path="description"
                               placeholder="Describe the model"
                               required="true"/>

        <button type="submit">Save Model</button>

    </form:form>

</div>

</body>
</html>