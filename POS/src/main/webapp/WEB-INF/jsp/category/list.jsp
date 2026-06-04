<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Arial, sans-serif;
            background: linear-gradient(120deg, #e0eafc, #cfdef3);
            padding: 30px;
            margin: 0;
        }

        .container {
            max-width: 800px;
            margin: auto;
            background: #ffffff;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #2c3e50;
        }

        .top-actions {
            display: flex;
            justify-content: space-between;
            margin-bottom: 16px;
        }

        .add-btn, .home-btn {
            padding: 8px 14px;
            border-radius: 6px;
            font-weight: 600;
            text-decoration: none;
            color: #fff;
        }

        .add-btn {
            background: #007bff;
        }

        .home-btn {
            background: #6c757d;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 10px;
            text-align: center;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #f1f5f9;
            font-weight: 700;
            color: #333;
        }

        tr:hover {
            background-color: #f9f9f9;
        }

        .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-edit {
            background-color: #0d6efd;
            color: #fff;
        }

        .btn-delete {
            background-color: #dc3545;
            color: #fff;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .action-form {
            display: inline;
        }

        .empty {
            text-align: center;
            padding: 20px;
            color: #888;
            font-style: italic;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Category List</h2>

    <div class="top-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">
             Home
        </a>

        <a href="${pageContext.request.contextPath}/category/add" class="add-btn">
            + Add Category
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Identifier</th>
            <th>Category</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>

        <c:if test="${empty category}">
            <tr>
                <td colspan="3" class="empty">No categories found</td>
            </tr>
        </c:if>

        <c:forEach var="cat" items="${category}">
            <tr>
                <td>${cat.identifier}</td>
                <td>${cat.supercategory}</td>
                <td>
                    <form class="action-form"
                          action="${pageContext.request.contextPath}/category/get"
                          method="get">
                        <input type="hidden" name="identifier" value="${cat.identifier}">
                        <button type="submit" class="btn btn-edit">Edit</button>
                    </form>
                    <form class="action-form"
                          action="${pageContext.request.contextPath}/category/delete"
                          method="get"
                          onsubmit="return confirm('Are you sure you want to delete this category?');">
                        <input type="hidden" name="identifier" value="${cat.identifier}">
                        <button type="submit" class="btn btn-delete">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>
</div>

</body>
</html>