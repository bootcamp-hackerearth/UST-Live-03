<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .edit-card {
            width: 460px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 18px;
            box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
            color: #333;
        }

        label {
            font-weight: 600;
            font-size: 0.9rem;
            color: #555;
            margin-top: 12px;
        }

        input, select {
            width: 100%;
            padding: 10px 14px;
            margin-top: 6px;
            border-radius: 10px;
            border: 1px solid #ddd;
            font-size: 0.9rem;
        }

        select[multiple] {
            height: 120px;
        }

        /* Gradient Update Button */
        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: #ffffff;
            font-weight: 600;
            border-radius: 25px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        button:hover {
            background: linear-gradient(90deg, #00c6ff, #0072ff);
            transform: translateY(-1px);
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            font-weight: 600;
            color: #0d6efd;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="edit-card">

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

    <a class="back-link" href="${pageContext.request.contextPath}/node/list">
        ← Back to Node List
    </a>

</div>

</body>
</html>