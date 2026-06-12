<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <style>
        body {
            font-family: 'Segoe UI', Arial;
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

            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;

            color: #111827;
            font-weight: 600;
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
            color: #fff;

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
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Node</h2>

    <form action="${pageContext.request.contextPath}/node/update" method="post">

        <input type="hidden" name="id" value="${node.id}" />
        <input type="hidden" name="identifier" value="${node.identifier}" />

        <label>Path</label>
        <input type="text" name="path" value="${node.path}" required />

        <label>Roles</label>
        <select name="roles" multiple required>
            <c:forEach var="role" items="${roles}">
                <option value="${role.identifier}"
                    <c:if test="${node.roles.contains(role.identifier)}">selected</c:if>>
                    ${role.identifier}
                </option>
            </c:forEach>
        </select>

        <button type="submit">Update</button>

    </form>

    <a href="${pageContext.request.contextPath}/node/list">
        Back to Node List
    </a>

</div>
</body>
</html>