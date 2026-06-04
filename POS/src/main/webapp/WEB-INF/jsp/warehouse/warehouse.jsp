<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

    <style>
        body { background:#f6f7f9; font-family:"Segoe UI"; margin:0; }

        .card {
            width:420px; margin:60px auto; background:#fff;
            padding:26px; border-radius:12px; position:relative;
        }

        .back-btn {
            position:absolute; top:18px; left:18px;
            background:#eef0f3; padding:6px 14px;
            border-radius:6px; text-decoration:none;
        }

        label { display:block; margin-top:14px; font-weight:600; }
        input { width:100%; padding:9px; margin-top:6px; }

        input[readonly] { background:#f1f5f9; }

        button {
            margin-top:22px; width:100%;
            background:#2563eb; color:white;
            border:none; padding:10px; border-radius:20px;
        }

        .error { color:#dc2626; text-align:center; font-weight:600; }
    </style>
</head>

<body>

<div class="card">

    <a href="${pageContext.request.contextPath}/wareHouse/list" class="back-btn">Back</a>

    <h2 style="text-align:center;">Edit Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/wareHouse/update"
               method="post"
               modelAttribute="wareHouse">

        <form:hidden path="id"/>

        <label>Warehouse Name</label>
        <form:input path="identifier" readonly="true"/>

        <label>Location</label>
        <form:input path="location"/>

        <label>Manager</label>
        <form:input path="manager"/>

        <button type="submit">Update Warehouse</button>
    </form:form>

</div>

</body>
</html>