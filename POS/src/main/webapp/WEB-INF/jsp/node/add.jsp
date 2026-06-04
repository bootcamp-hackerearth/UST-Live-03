<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 450px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            text-align: center;
        }

        .card-header h2 {
            font-size: 22px;
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

        button {
            width: 100%;
            margin-top: 24px;
            padding: 10px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border: none;
            border-radius: 25px;
            font-size: 1rem;
            font-weight: 600;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            color: #0d6efd;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h2>Add Node</h2>
        </div>

        <div class="card-body">

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/node/add" method="post">

                <label>Identifier</label>
                <input type="text" name="identifier" required>

                <label>Path</label>
                <input type="text" name="path" required>

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

            <a class="back-link"
               href="${pageContext.request.contextPath}/node/list">
                ← Back to Node List
            </a>
        </div>

        <div class="card-footer text-center">
            POS Management System
        </div>

    </div>
</div>

</body>
</html>