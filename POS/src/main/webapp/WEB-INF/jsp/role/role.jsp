<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Role</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background: #d1d5db;
        }

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

        /* ERROR */
        .error {
            background: rgba(239,68,68,0.1);
            color: #dc2626;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            font-size: 13px;
            margin-bottom: 15px;
        }

        /* LABEL */
        label {
            margin-top: 16px;
            display: block;
            font-weight: 600;
            font-size: 13px;
            color: #334155;
        }

        /* INPUT */
        input {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 13px;
            outline: none;
            transition: 0.2s;
            box-sizing: border-box; /* Keeps nested elements well-behaved within parent box dimensions */
        }

        input:focus {
            border-color: #0891b2;
            box-shadow: 0 0 0 2px rgba(8,145,178,0.2);
        }

        input[readonly] {
            background: #e2e8f0;
            cursor: not-allowed;
        }

        /* BUTTONS */
        .btn-group {
            display: flex;
            justify-content: space-between;
            margin-top: 25px;
        }

        .btn {
            padding: 10px 20px;
            border-radius: 20px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            transition: 0.25s;
        }

        /* CANCEL */
        .cancel-btn {
            background: #64748b;
            color: white;
        }

        .cancel-btn:hover {
            background: #475569;
        }

        /* UPDATE */
        .update-btn {
            background: linear-gradient(135deg, #0891b2, #0e7490);
            color: white;
        }

        .update-btn:hover {
            background: linear-gradient(135deg, #0e7490, #075985);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(8,145,178,0.4);
        }

        /* BACK LINK */
        .back {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #0891b2;
            font-weight: 600;
            text-decoration: none;
            font-size: 13px;
        }

        .back:hover {
            color: #0e7490;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Role</h2>

    <c:if test="${empty role}">
        <div class="error">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty role}">

        <form:form action="/role/update"
                   method="post"
                   modelAttribute="role">

            <form:hidden path="id"/>

            <label for="identifier">
                Role Name
                <form:input path="identifier" id="identifier" readonly="true"/>
            </label>

            <label for="description">
                Description
                <form:input path="description" id="description"/>
            </label>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/role/list"
                   class="btn cancel-btn">Cancel</a>

                <button type="submit" class="btn update-btn">
                    Update
                </button>
            </div>

        </form:form>

    </c:if>

</div>

</body>
</html>