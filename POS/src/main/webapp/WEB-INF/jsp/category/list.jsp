<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Category List</title>
    <style>
        body {
            background-color: #FFF8F0;
            font-family: "Segoe UI", Arial, sans-serif;
            padding-top: 40px;
        }

        .container {
            width: 90%;
            margin: auto;
        }

        h2 {
            text-align: center;
            color: #4B2E2B;
            font-weight: 600;
            margin-bottom: 25px;
        }

        .top-bar {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-bottom: 25px;
        }

        .btn {
            background-color: #6b4a46;
            color: #FFF8F0;
            border-radius: 10px;
            padding: 8px 18px;
            text-decoration: none;
            font-weight: 500;
        }

        .btn:hover {
            background-color: #543835;
        }

        .btn-edit {
            background-color: #4B2E2B;
        }

        .btn-edit:hover {
            background-color: #3a2421;
        }

        .btn-delete {
            background-color: #4b2e2b;
        }

        .btn-delete:hover {
            background-color: #3a2421;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 10px 25px rgba(75, 46, 43, 0.15);
        }

        th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            padding: 14px;
            font-size: 13px;
            text-transform: uppercase;
        }

        td {
            padding: 14px;
            text-align: center;
            font-size: 14px;
        }

        tr:nth-child(even) {
            background-color: #efe4dc;
        }

        tr:hover {
            background-color: #f7ebe4;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            gap: 8px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cfc4bb;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
             position: absolute;
             content: "";
             height: 16px;
             width: 16px;
             left: 3px;
             bottom: 3px;
             background-color: white;
             border-radius: 50%;
             transition: 0.4s;
        }

        input:checked + .slider {
             background-color: #6b4a46;
        }

        input:checked + .slider:before {
             transform: translateX(24px);
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
            <th>Status</th>
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
                <td class="text-center">
                    <form action="${pageContext.request.contextPath}/category/toggleStatus" method="post">
                        <input type="hidden" name="identifier" value="${category.identifier}"/>
                            <label class="switch">
                                <input type="checkbox" onchange="this.form.submit()"
                                    <c:if test="${category.status}">checked</c:if>>
                                    <span class="slider"></span>
                            </label>
                    </form>
                        <small class="text-primary">
                            <c:choose>
                                <c:when test="${category.status}">Active</c:when>
                                <c:otherwise>Inactive</c:otherwise>
                            </c:choose>
                        </small>
                </td>
                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}"
                        class="btn btn-edit"
                        title = "Edit Category">
                        <i class="fa-solid fa-pen"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/category/delete?identifier=${category.identifier}"
                        class="btn btn-delete"
                        onclick="return confirm('Are you sure you want to delete this category?');"
                        title = "Delete Category">
                        <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>