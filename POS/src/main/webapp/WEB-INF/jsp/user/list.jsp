<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | User List</title>

<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<style>
    :root {
        --primary: #0f172a;
        --bg-light: #f8fafc;
        --text-main: #0f172a;
        --text-muted: #64748b;
        --border: #e2e8f0;
        --card-bg: #ffffff;
    }

    * { box-sizing: border-box; }

    body {
        margin: 0;
        min-height: 100vh;
        font-family: 'Inter', -apple-system, system-ui, sans-serif;
        background-color: var(--bg-light);
        background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
        background-size: 24px 24px;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px 20px;
    }

    .container {
        background: var(--card-bg);
        width: 100%;
        max-width: 1100px;
        padding: 40px;
        border-radius: 12px;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1),
                    0 2px 4px -1px rgba(0,0,0,0.06);
        border-top: 5px solid var(--primary);
    }

    .header {
        margin-bottom: 30px;
        text-align: center;
    }

    .header h2 {
        margin: 0;
        font-size: 24px;
        color: var(--text-main);
    }

    .header p {
        margin-top: 6px;
        font-size: 14px;
        color: var(--text-muted);
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }

    th, td {
        padding: 14px;
        border-bottom: 1px solid var(--border);
        text-align: center;
        font-size: 14px;
    }

    th {
        background-color: #f1f5f9;
        text-transform: uppercase;
        font-size: 12px;
        letter-spacing: 0.05em;
        color: var(--text-muted);
    }

    tr:hover { background-color: #f8fafc; }

    .action-icon {
        color: var(--primary);
        font-size: 16px;
        margin: 0 8px;
        text-decoration: none;
        cursor: pointer;
    }

    .action-icon:hover {
        color: #020617;
    }

    .delete-icon:hover {
        color: #dc2626; /* red on hover */
    }

    .alert {
        background-color: #fef3c7;
        border: 1px solid #fde68a;
        color: #92400e;
        padding: 14px;
        border-radius: 6px;
        text-align: center;
        margin-top: 20px;
    }

    .footer-links {
        margin-top: 25px;
        padding-top: 20px;
        border-top: 1px solid var(--border);
        text-align: center;
    }

    .footer-links a {
        color: var(--primary);
        text-decoration: none;
        font-size: 14px;
        font-weight: 600;
        margin: 0 12px;
    }

    .footer-links a:hover {
        text-decoration: underline;
    }
</style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Registered Users</h2>
        <p>Manage staff accounts for the POS system.</p>
    </div>

    <c:if test="${empty users}">
        <div class="alert">No users found</div>
    </c:if>

    <c:if test="${not empty users}">
        <table>
            <thead>
                <tr>
                    <th>Sl.</th>
                    <th>Email</th>
                    <th>Name</th>
                    <th>Phone</th>
                    <th>Roles</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>${user.phoneNo}</td>
                    <td>${user.roles}</td>
                    <td>

                        <a href="${pageContext.request.contextPath}/user/get?username=${user.username}"
                           class="action-icon"
                           title="Edit User">
                            <i class="fa-solid fa-pen"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                           class="action-icon delete-icon"
                           title="Delete User"
                           onclick="return confirm('Are you sure you want to delete this user?');">
                            <i class="fa-solid fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-links">
        <a href="/">Home</a>
        <a href="${pageContext.request.contextPath}/register">Register New User</a>
    </div>

</div>

</body>
</html>