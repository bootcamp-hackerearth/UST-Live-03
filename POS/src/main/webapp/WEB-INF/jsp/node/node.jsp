<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100vh;
            width: 100vw;
            overflow: hidden;
        }

        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #ede9fe, #ddd6fe);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            width: 420px;
            background: #ffffff;
            padding: 25px 30px;
            border-radius: 12px;
            box-shadow: 0 20px 40px rgba(76, 29, 149, 0.18);
            box-sizing: border-box;
        }

        h2 {
            text-align: center;
            margin-top: 0;
            margin-bottom: 20px;
            color: #6d28d9;
            font-weight: 600;
        }

        label {
            margin-top: 14px;
            display: block;
            font-weight: 600;
            font-size: 13px;
            color: #4c1d95;
        }

        input, select {
            width: 100%;
            margin-top: 5px;
            padding: 9px;
            border: 1px solid #c4b5fd;
            border-radius: 6px;
            font-size: 13px;
            box-sizing: border-box;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #a78bfa;
            box-shadow: 0 0 0 0.15rem rgba(167, 139, 250, 0.35);
        }

        select[multiple] {
            height: 100px;
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 11px;
            background: #7c3aed;
            color: #ffffff;
            border: none;
            font-weight: 600;
            border-radius: 6px;
            cursor: pointer;
            font-size: 13px;
        }

        button:hover {
            background: #6d28d9;
        }

        a {
            display: block;
            text-align: center;
            margin-top: 16px;
            color: #6d28d9;
            font-weight: 600;
            text-decoration: none;
            font-size: 13px;
        }

        a:hover {
            text-decoration: underline;
            color: #5b21b6;
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
        ← Back to Node List
    </a>

</div>
</body>
</html>