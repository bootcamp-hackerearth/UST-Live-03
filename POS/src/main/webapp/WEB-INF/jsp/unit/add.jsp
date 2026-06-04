<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Unit</title>

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
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .top-title { color: #e5e7eb; font-weight: 600; }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
        }

        .card {
            width: 420px;
            margin: 60px auto;
            background: white;
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
            border-radius: 6px;
            color: #374151;
            font-weight: 600;
            text-decoration: none;
        }

        h2 { text-align: center; margin-bottom: 16px; }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input {
            width: 100%;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
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
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">
    <a href="${pageContext.request.contextPath}/unit/list" class="back-btn">Back</a>

    <h2>Add Unit</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/unit/add"
               method="post"
               modelAttribute="unitDto">

        <label>Unit Name</label>
        <form:input path="identifier" required="true"/>

        <button type="submit">Add Unit</button>
    </form:form>
</div>

</body>
</html>