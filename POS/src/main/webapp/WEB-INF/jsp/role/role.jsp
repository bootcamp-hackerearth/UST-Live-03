<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <style>
        :root {
            --bg: #f6fff8;
            --card: #ffffff;

            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #218838;

            --accent: #ffc107;

            --border: #e5e7eb;

            --radius: 14px;
            --shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: var(--bg);
            padding: 20px;
            position: relative;
        }

        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 50%;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: var(--text);
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: var(--accent);
            color: black;
        }

        .container-box {
            width: 100%;
            max-width: 480px;
        }

        .card {
            background: var(--card);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            padding: 24px;
        }

        h2 {
            text-align: center;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 18px;
            color: var(--text);
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-top: 10px;
        }

        .form-control {
            margin-top: 6px;
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
            width: 100%;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(40,167,69,0.15);
        }

        .btn-update {
            margin-top: 18px;
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: none;
            background: var(--primary);
            color: white;
            font-weight: 600;
            transition: 0.2s;
        }

        .btn-update:hover {
            background: var(--primary-hover);
        }

        .readonly-field {
            background: #f3f4f6;
            cursor: not-allowed;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/role/list" class="back-arrow">
    ←
</a>

<div class="container-box">
    <div class="card">

        <h2>Edit Role</h2>

        <c:if test="${empty role}">
            <div style="color:red; text-align:center;">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty role}">

            <form:form action="${pageContext.request.contextPath}/role/update"
                       method="post"
                       modelAttribute="role">

                <form:hidden path="id"/>
                <form:hidden path="identifier"/>

                <label>Role Name</label>
                <input type="text"
                       class="form-control readonly-field"
                       value="${role.identifier}"
                       readonly />

                <label>Description</label>
                <form:input path="description"
                            cssClass="form-control"
                            required="true"/>

                <button type="submit" class="btn-update">
                    Update Role
                </button>

            </form:form>

        </c:if>

    </div>
</div>

</body>
</html>