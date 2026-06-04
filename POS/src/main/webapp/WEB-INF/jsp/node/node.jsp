<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
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

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .home-btn {
            padding: 6px 14px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .home-btn:hover {
            background-color: #334155;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
        }

        /* ===== PAGE ===== */
        .page-wrapper {
            display: flex;
            justify-content: center;
            margin-top: 60px;
        }

        .card {
            width: 420px;
            background: #ffffff;
            padding: 26px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        /* BACK BUTTON */
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
            border: 1px solid #d1d5db;
        }

        .back-btn:hover {
            background-color: #e5e7eb;
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
            font-size: 14px;
            color: #1f2937;
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
            font-size: 14px;
            cursor: pointer;
        }

        button:hover {
            background-color: #1d4ed8;
        }
    </style>
</head>

<body>

<!-- POS TOP BAR -->
<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-wrapper">
    <div class="card">

        <!-- BACK BUTTON -->
        <a href="${pageContext.request.contextPath}/node/list" class="back-btn">Back</a>

        <h2>Edit Node</h2>

        <!-- UPDATE FORM  -->
        <form action="${pageContext.request.contextPath}/node/update" method="post">

            <input type="hidden" name="id" value="${node.id}" />
            <input type="hidden" name="identifier" value="${node.identifier}" />

            <label>Path</label>
            <input type="text" name="path" value="${node.path}" required />

            <label>Roles</label>
            <select name="roles" multiple required>
                <c:forEach var="role" items="${roles}">
                    <option value="${role.identifier}"
                        <c:if test="${node.roles.contains(role.identifier)}">selected</c:if>>
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>

            <button type="submit">Update Node</button>

        </form>
    </div>
</div>

</body>
</html>