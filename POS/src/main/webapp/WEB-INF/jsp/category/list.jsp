<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category Management | POS</title>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            min-height: 100vh;
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

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .add-btn {
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
            font-size: 13px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            color: #ddd;
            padding: 12px;
            font-size: 14px;
            text-align: left;
            border-bottom: 1px solid rgba(255,255,255,0.2);
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            color: #f1f1f1;
            text-align: left;
        }

        tr:hover {
            background: rgba(255,255,255,0.05);
        }

        .actions {
            display: flex;
            gap: 18px;
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

        .empty {
            text-align: center;
            padding: 20px;
            color: #ddd;
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

        .btn-secondary:hover {
            background: rgba(255,255,255,0.3);
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <div class="header">
            <h3>Category Management</h3>
            <a href="/category/add" class="add-btn">+ Add Category</a>
        </div>

        <c:if test="${empty categories}">
            <div class="empty">No categories found</div>
        </c:if>

        <c:if test="${not empty categories}">
            <table>
                <thead>
                <tr>
                    <th>Category</th>
                    <th>Super Category</th>
                    <th>Action</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="cat" items="${categories}">
                    <tr>
                        <td>${cat.identifier}</td>
                        <td>${cat.superCategory}</td>
                        <td>
                            <div class="actions">
                                <a href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}" class="icon-btn edit-icon">
                                    <i class="fas fa-pen"></i>
                                </a>

                                <a href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                                   class="icon-btn delete-icon"
                                   onclick="return confirm('Delete this category?')">
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
    </div>

</div>

</body>
</html>