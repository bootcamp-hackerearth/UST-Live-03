<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #eee;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .header-actions {
            display: flex;
            gap: 10px;
        }
        h2 { margin: 0; color: #333; }

        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background-color: #f8f9fa;
            color: #666;
            text-align: left;
            padding: 12px;
            border-bottom: 2px solid #dee2e6;
            font-size: 14px;
        }
        td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            vertical-align: middle;
        }
        tr:hover { background-color: #fafafa; }

        /* Buttons */
        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            display: inline-block;
            border: none;
            cursor: pointer;
            font-weight: bold;
        }
        .btn-back { background-color: #6c757d; color: white; }
        .btn-add { background-color: #28a745; color: white; }
        .btn-edit { background-color: #007bff; color: white; margin-right: 5px; padding: 6px 12px; }
        .btn-delete { background-color: #dc3545; color: white; padding: 6px 12px; }

        /* Role Badges */
        .badge {
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            background: #e9ecef;
            color: #495057;
            text-transform: uppercase;
            font-weight: bold;
            display: inline-block;
            margin-right: 4px;
            border: 1px solid #ddd;
        }

        .success-msg {
            background-color: #d4edda;
            color: #155724;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: center;
            border: 1px solid #c3e6cb;
        }

        .empty-msg {
            text-align: center;
            padding: 50px;
            color: #999;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header">
        <h2>Node Management</h2>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-back">← Back to Home</a>
            <a href="${pageContext.request.contextPath}/node/add" class="btn btn-add">+ Add Node</a>
        </div>
    </div>

    <c:if test="${not empty success}">
        <div class="success-msg">${success}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty nodes}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Node Name</th>
                        <th>Path</th>
                        <th>Roles</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="node" items="${nodes}">
                        <tr>
                            <td><strong>${node.id}</strong></td>
                            <td>${node.identifier}</td>
                            <td><code>${node.path}</code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty node.roles}">
                                        <c:forEach var="role" items="${node.roles}">
                                            <span class="badge">${role}</span>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #ccc;">No Roles</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div style="display: flex;">
                                    <form action="${pageContext.request.contextPath}/node/get" method="get">
                                        <input type="hidden" name="identifier" value="${node.identifier}" />
                                        <button type="submit" class="btn btn-edit">Edit</button>
                                    </form>

                                    <form action="${pageContext.request.contextPath}/node/delete" method="get"
                                          onsubmit="return confirm('Are you sure you want to delete this node?');">
                                        <input type="hidden" name="identifier" value="${node.identifier}" />
                                        <button type="submit" class="btn btn-delete">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="empty-msg">
                <p>No nodes found in the system.</p>
                <a href="${pageContext.request.contextPath}/node/add">Create your first node</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>