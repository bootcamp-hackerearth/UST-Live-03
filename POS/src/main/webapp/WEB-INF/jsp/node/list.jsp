<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Node List</title>

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
        padding: 40px 20px;
        display: flex;
        justify-content: center;
        align-items: center;
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
        color: var(--text-main);
    }

    th {
        background-color: #f1f5f9;
        text-transform: uppercase;
        font-size: 12px;
        letter-spacing: 0.05em;
        color: var(--text-muted);
    }

    tr:hover {
        background-color: #f8fafc;
    }

    .role-tag {
        display: inline-block;
        padding: 4px 10px;
        margin: 2px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 600;
        background: #eef2ff;
        color: #4f46e5;
        border: 1px solid #e0e7ff;
    }

    .action-icon {
        color: var(--primary);
        font-size: 16px;
        margin: 0 10px;
        text-decoration: none;
        cursor: pointer;
    }

    .action-icon:hover {
        color: #020617;
    }

    .delete-icon:hover {
        color: #dc2626;
    }

    .empty {
        padding: 30px;
        color: #94a3b8;
        text-align: center;
        font-style: italic;
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
        <h2>Node Management</h2>
        <p>Manage application nodes and route permissions.</p>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Node Name</th>
                <th>Roles</th>
                <th>Path</th>
                <th>Action</th>
            </tr>
        </thead>

        <tbody>
        <c:choose>
            <c:when test="${not empty nodes}">
                <c:forEach var="n" items="${nodes}">
                    <tr>
                        <td>${n.id}</td>
                        <td>${n.identifier}</td>

                        <td>
                            <c:choose>
                                <c:when test="${not empty n.roles}">
                                    <c:forEach var="r" items="${n.roles}">
                                        <span class="role-tag">${r}</span>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>

                        <td>${n.path}</td>

                        <td>

                            <a class="action-icon"
                               href="${pageContext.request.contextPath}/node/get?identifier=${n.identifier}"
                               title="Edit Node">
                                <i class="fa-solid fa-pen"></i>
                            </a>

                            <a class="action-icon delete-icon"
                               href="${pageContext.request.contextPath}/node/delete?identifier=${n.identifier}"
                               onclick="return confirm('Are you sure you want to delete this node?');"
                               title="Delete Node">
                                <i class="fa-solid fa-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>

            <c:otherwise>
                <tr>
                    <td colspan="5" class="empty">
                        No nodes found in the system.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

    <div class="footer-links">
        <a href="${pageContext.request.contextPath}/node/add">Add New Node</a>
        <a href="${pageContext.request.contextPath}/">Home</a>
    </div>

</div>

</body>
</html>