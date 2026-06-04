<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Node</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background: #d1d5db;
        }

        /* CONTAINER */
        .container {
            width: 420px;
            margin: 100px auto;
            background: #f1f5f9;
            padding: 35px;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        /* TITLE */
        h2 {
            text-align: center;
            margin-bottom: 25px;
            font-size: 22px;
            color: #0891b2;
            font-weight: 600;
        }

        /* LABEL */
        label {
            margin-top: 16px;
            display: block;
            font-weight: 600;
            font-size: 13px;
            color: #334155;
        }

        /* INPUT + SELECT */
        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 13px;
            outline: none;
            transition: 0.2s;
        }

        input:focus, select:focus {
            border-color: #0891b2;
            box-shadow: 0 0 0 2px rgba(8,145,178,0.2);
        }

        select[multiple] {
            height: 120px;
        }

        /* BUTTON */
        button {
            margin-top: 28px;
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #0891b2, #0e7490);
            color: #ffffff;
            border: none;
            font-weight: 600;
            border-radius: 20px;
            cursor: pointer;
            transition: 0.25s;
        }

        button:hover {
            background: linear-gradient(135deg, #0e7490, #075985);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(8,145,178,0.4);
        }

        button:active {
            transform: translateY(0);
            box-shadow: none;
        }

        /* BACK LINK */
        a {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #0891b2;
            font-weight: 600;
            text-decoration: none;
            font-size: 13px;
            transition: 0.2s;
        }

        a:hover {
            color: #0e7490;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Node</h2>

    <form action="${pageContext.request.contextPath}/node/update" method="post">

        <input type="hidden" name="id" value="${node.id}" />
        <input type="hidden" name="identifier" value="${node.identifier}" />

        <label for="nodePath">Path</label>
        <input type="text" id="nodePath" name="path" value="${node.path}" required />

        <label for="nodeRoles">Roles</label>
        <select id="nodeRoles" name="roles" multiple required>
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