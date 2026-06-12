<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
            color: #1f2937;
        }

        /* ===== POS TOP BAR ===== */
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

        /* ===== PAGE ===== */
        .page-wrapper {
            display: flex;
            justify-content: center;
            margin-top: 60px;
        }

        .container {
            width: 420px;
            padding: 26px 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            padding: 6px 14px;
            background-color: #eef0f3;
            color: #374151;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin: 14px 0 24px;
            font-size: 22px;
            font-weight: 600;
        }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            width: 100%;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
        }

        select[multiple] {
            height: 120px;
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 10px;
            background-color: #2563eb;
            color: #ffffff;
            border: none;
            border-radius: 20px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<!--  TOP BAR -->
<div class="topbar">
    <div class="top-title">POS Application</div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-wrapper">
    <div class="container">

        <a href="${pageContext.request.contextPath}/user/list" class="back-btn">Back</a>

        <h2>Update User</h2>

        <c:if test="${not empty message}">
            <div style="text-align:center;color:#2563eb;font-size:13px;margin-bottom:10px;">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/user/update"
                   method="post"
                   modelAttribute="user">

            <form:input type="hidden" path="id"/>

            <label>Name</label>
            <form:input path="name" required="true"/>

            <label>Email</label>
            <form:input path="username" type="email" required="true"/>

            <label>Phone Number</label>
            <form:input path="phoneNo"
                        type="tel"
                        required="true"
                        pattern="[0-9]{10}"
                        maxlength="10"/>

            <label>Roles</label>
            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>

            <button type="submit">Update User</button>

        </form:form>


    </div>
</div>

</body>
</html>