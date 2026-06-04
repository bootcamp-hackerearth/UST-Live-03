<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #ffffff;
        }

        .card {
            width: 360px;
            margin: 40px auto;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            position: relative;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            background: #ffffff;
            border: 1px solid teal;
            color: teal;
            text-decoration: none;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin: 12px 0;
            font-size: 20px;
        }

        /* ===== FORM ===== */
        label {
            display: block;
            margin-top: 12px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            height: 34px;
            padding: 8px 10px;
            margin-top: 4px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        select[multiple] {
            height: 90px;
            border-radius: 12px;
        }

        button {
            margin-top: 16px;
            width: 100%;
            height: 34px;
            padding: 8px 10px;
            background: teal;
            color: #ffffff;
            border: none;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
        }

    </style>
</head>

<body>

<div class="card">

    <div class="app-title">POS Application</div>

    <a href="${pageContext.request.contextPath}/node/list" class="back-btn">Back</a>

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
        <button type="submit">Update Node</button>
    </form>
</div>

</body>
</html>