<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            min-height: 100vh;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
        }

        .container {
            width: 420px;
            margin: 80px auto;

            background: #ffffff;
            padding: 30px;

            border-radius: 12px;
            border: 1px solid #e5e7eb;

            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;

            font-size: 20px;
            font-weight: 600;

            color: #111827;
        }

        label {
            margin-top: 16px;
            display: block;

            font-weight: 600;
            font-size: 13px;

            color: #374151;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;

            border: 1px solid #d1d5db;
            border-radius: 8px;

            font-size: 14px;

            background: #f9fafb;
            transition: 0.2s ease;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #3b82f6;

            background: #ffffff;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
        }

        select[multiple] {
            height: 110px;
        }

        button {
            margin-top: 26px;
            width: 100%;
            padding: 11px;

            background: #2563eb;
            color: #ffffff;

            border: none;
            font-weight: 600;

            border-radius: 8px;
            cursor: pointer;

            transition: 0.2s ease;
        }

        button:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        a {
            display: block;
            text-align: center;
            margin-top: 18px;

            color: #2563eb;
            font-weight: 600;

            text-decoration: none;
            font-size: 13px;
        }

        a:hover {
            color: #1d4ed8;
            text-decoration: underline;
        }

        .error-message {
            margin-bottom: 15px;
            text-align: center;
            color: #dc2626;
            font-size: 13px;
            font-weight: 500;
        }
    </style>
</head>

<body>
<div class="container">

    <h2>Add Node</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/node/add" method="post">

        <label>Identifier</label>
        <input type="text" name="identifier" required />

        <label>Path</label>
        <input type="text" name="path" required />

        <label>Roles</label>
        <select name="roles" multiple required>
            <c:forEach var="role" items="${roles}">
                <option value="${role.identifier}">
                    ${role.identifier}
                </option>
            </c:forEach>
        </select>

        <button type="submit">Save</button>
    </form>

    <div class="back">
        <a href="${pageContext.request.contextPath}/node/list">← Back to List</a>
    </div>
</div>
</body>
</html>