<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
        }

        .card {
            width: 420px;
            background: #ffffff;
            margin: 60px auto;
            padding: 26px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            padding: 6px 14px;
            background: #eef0f3;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, textarea {
            width: 100%;
            padding: 9px 11px;
            margin-top: 6px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 10px;
            background: #2563eb;
            color: white;
            border-radius: 20px;
            border: none;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 14px;
        }
    </style>
</head>

<body>

<!-- TOP BAR -->
<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">
    <a href="${pageContext.request.contextPath}/role/list" class="back-btn">Back</a>

    <h2>Add Role</h2>

    <!-- ERROR MESSAGE -->
    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <!--  ADD ROLE FORM -->
    <form:form action="${pageContext.request.contextPath}/role/add"
               method="post"
               modelAttribute="roleDto">

        <label>Role Name</label>
        <form:input path="identifier" required="true"/>

        <label>Description</label>
        <form:textarea path="description" rows="3"/>

        <button type="submit">Add Role</button>
    </form:form>
</div>

</body>
</html>