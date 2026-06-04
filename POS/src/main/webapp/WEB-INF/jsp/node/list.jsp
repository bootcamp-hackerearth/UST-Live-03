<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .card {
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h3 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: rgba(255,255,255,0.18);
            color: #fff;
            padding: 12px 14px;
            font-size: 14px;
            text-align: left;
            backdrop-filter: blur(10px);
        }

        td {
            padding: 12px 14px;
            border-bottom: 1px solid rgba(255,255,255,0.15);
            text-align: left;
            color: #f1f1f1;
            font-size: 14px;
        }

        tr:hover {
            background: rgba(255,255,255,0.08);
        }

        .actions {
            display: flex;
            gap: 18px;
            align-items: center;
        }

        .icon-btn {
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 15px;
        }

        .edit-icon {
            color: #ff7a00;
        }

        .delete-icon {
            color: #ff4d4d;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .footer {
            margin-top: 20px;
            text-align: center;
        }

        .btn-secondary {
            background: rgba(255,255,255,0.2);
            color: #fff;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
        }

        .btn-success {
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 20px;
            color: #ddd;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">
        <h3>Node Management</h3>

        <c:if test="${empty nodes}">
            <div class="empty">No nodes found</div>
        </c:if>

        <c:if test="${not empty nodes}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Node</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="node" items="${nodes}">
                    <tr>
                        <td>${node.id}</td>
                        <td>${node.identifier}</td>

                        <td>
                            <div class="actions">
                                <a href="/node/get?identifier=${node.identifier}" class="icon-btn edit-icon">
                                    <i class="fas fa-pen"></i>
                                </a>

                                <a href="/node/delete?identifier=${node.identifier}"
                                   class="icon-btn delete-icon"
                                   onclick="return confirm('Are you sure you want to delete this node?');">
                                    <i class="fas fa-trash"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>

    <div class="footer">
        <a href="/" class="btn-secondary">Home</a>
        <a href="/node/add" class="btn-success">+ Add New Node</a>

        <div style="margin-top:10px; font-size:12px; color:#ddd;">
            Node Management System
        </div>
    </div>

</div>

</body>
</html>