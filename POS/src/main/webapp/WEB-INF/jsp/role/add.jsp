<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        :root {
            --bg:#f6fff8;
            --card:#ffffff;
            --text:#1f2937;
            --muted:#6b7280;
            --primary:#28a745;
            --accent:#ffc107;
            --danger:#dc2626;
            --danger-bg:#fee2e2;
            --danger-border:#fca5a5;
            --success:#16a34a;
            --success-bg:#dcfce7;
            --success-border:#86efac;
            --border:#e5e7eb;
            --radius:14px;
            --shadow:0 10px 30px rgba(0,0,0,.08);
        }

        body {
            background:var(--bg);
            min-height:100vh;
            display:flex;
            justify-content:center;
            align-items:center;
            padding:20px;
            position:relative;
        }

        .back-arrow {
            position:absolute;
            top:20px;
            left:20px;
            width:42px;
            height:42px;
            border-radius:50%;
            display:flex;
            align-items:center;
            justify-content:center;
            background:var(--card);
            border:1px solid var(--border);
            text-decoration:none;
            color:var(--text);
            font-size:18px;
            box-shadow:var(--shadow);
            transition:.2s;
        }
        .back-arrow:hover {
            background:var(--accent);
            color:black;
        }

        .card {
            width:420px;
            padding:22px;
            border-radius:var(--radius);
            box-shadow:var(--shadow);
        }

        label {
            font-size:13px;
            color:var(--muted);
        }

        .server-msg {
            text-align:center;
            padding:10px;
            border-radius:10px;
            margin-bottom:12px;
            font-size:13px;
        }
        .server-msg.error {
            background:var(--danger-bg);
            color:var(--danger);
            border:1px solid var(--danger-border);
        }
        .server-msg.success {
            background:var(--success-bg);
            color:var(--success);
            border:1px solid var(--success-border);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/role/list" class="back-arrow">←</a>

<div class="card">

    <h5 class="text-center mb-3">Add Role</h5>

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType}">
            ${message}
        </div>
    </c:if>

    <form:form modelAttribute="roleDto"
               method="post"
               action="${pageContext.request.contextPath}/role/add">

        <div class="mb-3">
            <label>Role Name</label>
            <form:input path="identifier" cssClass="form-control" required="true"/>
        </div>

        <div class="mb-3">
            <label>Description</label>
            <form:input path="description" cssClass="form-control" required="true"/>
        </div>

        <button class="btn btn-success w-100">Add Role</button>

    </form:form>

</div>

</body>
</html>