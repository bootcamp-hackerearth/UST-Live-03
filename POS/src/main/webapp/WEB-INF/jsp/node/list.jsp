<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 900px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th {
            background: #4b6cb7;
            color: white;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            color: #333;
            word-break: break-word;
        }

        tr:hover {
            background: #f7f9ff;
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
            transition: 0.2s ease;
        }

        .action-icon:hover {
            color: #182848;
            transform: scale(1.2);
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }

        .alert-warning {
            background: #fff3cd;
            color: #856404;
        }

        .footer-actions {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            display: inline-block;
            transition: 0.25s ease;
        }

        .btn-home {
            background: #6c757d;
            color: white;
        }

        .btn-home:hover {
            background: #555;
        }

        .btn-add {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-add:hover {
            transform: scale(1.05);
        }

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75, 108, 183, 0.08);
            transition: all 0.25s ease;
        }

        .home-link:hover {
            background: #4b6cb7;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
            transform: translateY(-2px);
        }

    </style>
</head>

<body>

<div class="card-container">

    <a href="/" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>List of Nodes</h2>

    <c:if test="${empty nodes}">
        <div class="alert alert-warning">
            No nodes found
        </div>
    </c:if>

    <c:if test="${not empty nodes}">
        <table>
            <thead>
            <tr>
                <th>Node</th>
                <th>Path</th>
                <th>Roles</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="node" items="${nodes}">
                <tr>
                    <td>${node.identifier}</td>
                    <td>${node.path}</td>
                    <td>${node.roles}</td>

                    <td>
                        <a href="/node/get?identifier=${node.identifier}"
                           class="action-icon"
                           title="Edit">✏️</a>

                        <a href="/node/delete?identifier=${node.identifier}"
                           class="action-icon"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this node?');">
                            🗑
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-actions">
        <a href="/node/add" class="btn btn-add">+ Add New Node</a>
    </div>

</div>

</body>
</html>