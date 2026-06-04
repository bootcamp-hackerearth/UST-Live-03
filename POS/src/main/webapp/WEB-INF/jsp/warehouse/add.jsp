<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body { background:#f6f7f9; font-family:"Segoe UI"; margin:0; }

        .topbar {
            height:56px; background:#020617; display:flex;
            justify-content:space-between; align-items:center; padding:0 20px;
        }
        .top-title { color:#e5e7eb; font-weight:600; }
        .logout-btn { background:#dc2626; color:white; border:none;
            padding:7px 16px; border-radius:6px; }

        .card {
            width:420px; margin:60px auto; background:#fff;
            padding:26px; border-radius:12px; position:relative;
        }

        .back-btn {
            position:absolute; top:18px; left:18px;
            background:#eef0f3; padding:6px 14px;
            border-radius:6px; text-decoration:none; font-weight:600;
        }

        label { margin-top:14px; display:block; font-weight:600; }
        input { width:100%; padding:9px; margin-top:6px; }

        button {
            margin-top:22px; width:100%;
            background:#2563eb; color:white;
            border:none; padding:10px; border-radius:20px;
        }

        .error { color:#dc2626; text-align:center; font-weight:600; }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <a href="${pageContext.request.contextPath}/wareHouse/list" class="back-btn">Back</a>

    <h2 style="text-align:center;">Add Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/wareHouse/add"
               method="post"
               modelAttribute="wareHouseDto">

        <label>Warehouse Name</label>
        <form:input path="identifier" required="true"/>

        <label>Location</label>
        <form:input path="location" required="true"/>

        <label>Manager</label>
        <form:input path="manager" required="true"/>

        <button type="submit">Add Warehouse</button>
    </form:form>
</div>

</body>
</html>