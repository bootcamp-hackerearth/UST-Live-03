<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
            color: #111827;
        }

        /* ===== TOP BAR ===== */
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
            padding: 7px 16px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
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
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        /* ===== PAGE ===== */
        .page-title {
            text-align: center;
            padding: 22px 0 14px;
            font-size: 28px;
            font-weight: 700;
            color: #020617;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 0 auto 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
            padding: 26px;
        }

        /* ACTION BAR */
        .list-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 18px;
        }

        .add-btn {
            padding: 9px 18px;
            background-color: #2563eb;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
        }

        .add-btn:hover {
            background-color: #1d4ed8;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 15px;
        }

        thead {
            background-color: #e5e7eb;
        }

        th {
            padding: 16px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: #020617;
            border-bottom: 1px solid #cbd5e1;
            text-align: center;
            font-weight: 700;
        }

        td {
            padding: 16px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
            color: #111827;
            font-weight: 500;
        }

        tbody tr:hover {
            background-color: #f1f5f9;
        }

        .action-link {
            padding: 7px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            color: #ffffff;
            display: inline-block;
        }

        .edit {
            background-color: #2563eb;
        }

        .edit:hover {
            background-color: #1d4ed8;
        }

        .delete {
            background-color: #dc2626;
            margin-left: 8px;
        }

        .delete:hover {
            background-color: #b91c1c;
        }
    </style>
</head>

<body>

<!-- TOP BAR -->
<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-title">Role Management</div>

<div class="container">

    <!-- ADD ROLE BUTTON -->
    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/role/add" class="add-btn">
            Add Role
        </a>
    </div>

    <c:if test="${empty roles}">
        <div style="text-align:center; padding:28px; color:#374151; font-size:16px;">
            No roles found
        </div>
    </c:if>

    <c:if test="${not empty roles}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="role" items="${roles}">
                    <tr>
                        <td>${role.id}</td>
                        <td>${role.identifier}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty role.description}">
                                    ${role.description}
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}"
                               class="action-link edit">
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
                               class="action-link delete"
                               onclick="return confirm('Are you sure you want to delete this role?');">
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

</body>
</html>