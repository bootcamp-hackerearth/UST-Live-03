<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 80%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
        }

        .btn-edit {
            background-color: #2563eb;
        }

        .btn-delete {
            background-color: #dc2626;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            text-align: center;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            text-align: center;
            border-bottom: 1px solid #e2e8f0;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            gap: 10px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">Home</a>
        <a href="${pageContext.request.contextPath}/category/add" class="btn">+ Add Category</a>
    </div>

    <h2>Category List</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Name</th>
            <th>Super Category</th>
            <th>Action</th>
        </tr>

        <c:forEach var="category" items="${categories}">
            <tr>
                <td>${category.id}</td>
                <td>${category.identifier}</td>
                <td>${category.name}</td>
                <td>
                    <c:choose>
                        <c:when test="${empty category.superCategory}">
                            -
                        </c:when>
                        <c:otherwise>
                            ${category.superCategory}
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}"
                       class="btn btn-edit">Edit</a>

                    <a href="${pageContext.request.contextPath}/category/delete?identifier=${category.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this category?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

    </table>

</div>

</body>
</html>