<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Add Node</title>

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
            margin-bottom: 20px;
            font-size: 22px;
            color: #0891b2;
            font-weight: 600;
        }

        /* ERROR MESSAGE */
        .error-message {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 13px;
            text-align: center;
            font-weight: 500;
        }

        /* LABEL */
        label {
            margin-top: 16px;
            display: block;
            font-weight: 600;
            font-size: 13px;
            color: #334155;
        }

        /* INPUTS */
        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #cbd5f5;
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
        }

        a:hover {
            color: #0e7490;
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

        <label for="identifier">Identifier</label>
        <input type="text" id="identifier" name="identifier" required />

        <label for="path">Path</label>
        <input type="text" id="path" name="path" required />

        <label for="roles">Roles</label>
        <select id="roles" name="roles" multiple required>
            <c:forEach var="role" items="${roles}">
                <option value="${role.identifier}">
                    ${role.identifier}
                </option>
            </c:forEach>
        </select>

        <button type="submit">Save</button>
    </form>

    <a href="${pageContext.request.contextPath}/node/list">
        Back to Node List
    </a>

</div>

</body>
</html>