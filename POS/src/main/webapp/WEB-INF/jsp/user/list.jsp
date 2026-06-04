<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | User List</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            background: #ffffff;
            padding: 0; /* Remove padding to let header flush to edges */
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 20px;
            color: #FFFFFF;
            text-align: center;
        }

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .content-body {
            padding: 30px;
        }

        h2 {
            color: #1F2937;
            font-size: 18px;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .header-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 20px;
        }

        .btn-register {
            padding: 10px 20px;
            background: #0B3C5D;
            color: white;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            transition: opacity 0.2s;
        }

        .btn-register:hover {
            opacity: 0.9;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            margin-bottom: 20px;
        }

        thead {
            background: #F9FAFB;
            border-bottom: 2px solid #E5E7EB;
        }

        th {
            padding: 15px;
            text-align: left;
            color: #4B5563;
            font-size: 12px;
            text-transform: uppercase;
            font-weight: 700;
        }

        td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #F3F4F6;
            color: #1F2937;
        }

        tbody tr:hover {
            background-color: #F9FAFB;
        }

        .role-badge {
            background: #E5E7EB;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            margin: 2px;
            display: inline-block;
            color: #374151;
            font-weight: 600;
        }

        .action-cell {
            display: flex;
            gap: 10px;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
            transition: opacity 0.2s;
        }

        .btn-view {
            background: #E5E7EB;
            color: #1F2937;
        }

        .btn-delete {
            background: #FEE2E2;
            color: #DC2626;
        }

        .btn-action:hover {
            opacity: 0.8;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #9CA3AF;
        }

        .back-link {
            display: block;
            margin-top: 10px;
            font-size: 14px;
            color: #6B7280;
            text-decoration: none;
            font-weight: 600;
            text-align: center;
        }

        .back-link:hover {
            color: #0B3C5D;
            text-decoration: underline;
        }

        .toast {
            position: fixed;
            top: 24px;
            right: -400px;
            min-width: 280px;
            padding: 16px 20px;
            border-radius: 12px;
            color: #FFFFFF;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            z-index: 9999;
            transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        }

        .toast-success { background: #16A34A; }
        .toast-error { background: #DC2626; }
        .toast.show { right: 24px; }

    </style>
</head>

<body>

<c:set var="toastClass" value="${colour eq 'red' ? 'toast-error' : 'toast-success'}" />

<c:if test="${not empty message}">
    <div id="toast" class="toast ${toastClass}">
        ${message}
    </div>
</c:if>

    <div class="container">
        <div class="brand-header">
            <h1>POS Retail Management</h1>
        </div>

        <div class="content-body">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2>User Administration</h2>
                <a href="${pageContext.request.contextPath}/register" class="btn-register">
                    + Add New User
                </a>
            </div>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email Address</th>
                        <th>Phone</th>
                        <th>Roles</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty users}">
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td style="font-weight: 600; color: #6B7280;">#${user.id}</td>
                                    <td>${user.name}</td>
                                    <td>${user.username}</td>
                                    <td>${user.phoneNo}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty user.roles}">
                                                <c:forEach var="r" items="${user.roles}">
                                                    <span class="role-badge">${r}</span>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color:#9CA3AF; font-style: italic;">No roles</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="action-cell">
                                            <a href="${pageContext.request.contextPath}/user/get?username=${user.username}" class="btn-action btn-view">
                                                View
                                            </a>
                                            <a href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                                               class="btn-action btn-delete"
                                               onclick="return confirm('Are you sure you want to delete this user?')">
                                                Delete
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="no-data">
                                    No user records found in the system.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <a href="${pageContext.request.contextPath}/" class="back-link">
                ← Back to Dashboard
            </a>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const toast = document.getElementById("toast");
            if (toast) {
                setTimeout(() => toast.classList.add("show"), 200);
                setTimeout(() => toast.classList.remove("show"), 4000);
            }
        });
    </script>
</body>
</html>