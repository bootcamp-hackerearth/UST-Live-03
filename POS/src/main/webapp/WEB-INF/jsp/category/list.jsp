<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            width: 800px;
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            border-top-left-radius: 15px;
            border-top-right-radius: 15px;
            color: #111827;
            font-weight: 600;
        }
        table th {
            background: #e5e7eb;
            color: #111827;
        }
        table td {
            background: #f9fafb;
            color: #111827;
        }
        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.2s;
            color: white;
            text-decoration: none;
        }
        .icon-btn:hover {
            transform: scale(1.1);
        }
        .edit-btn {
            background: #3b82f6;
        }
        .delete-btn {
            background: #ef4444;
        }
        .btn-primary {
            background: #3b82f6;
            border: none;
        }
        .btn-primary:hover {
            background: #2563eb;
        }
        .btn-secondary {
            background: #6b7280;
            border: none;
        }
        .btn-secondary:hover {
            background: #4b5563;
        }
    </style>
</head>
<body>
<div class="card shadow-lg">
    <div class="card-header text-center">
        <h4 class="mb-0">Category List</h4>
    </div>
    <div class="card-body">
        <c:if test="${empty categories}">
            <div class="alert alert-warning text-center">
                No categories available
            </div>
        </c:if>
        <c:if test="${not empty categories}">
            <table class="table table-bordered table-hover text-center align-middle">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Super Category</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cat" items="${categories}">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${cat.identifier}</td>
                        <td>${cat.superCategory}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}"
                               class="icon-btn edit-btn">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                               class="icon-btn delete-btn"
                               onclick="return confirm('Are you sure you want to delete this category?');">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
    <div class="card-footer text-center d-flex justify-content-center gap-3">
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-secondary">
            Home
        </a>
        <a href="${pageContext.request.contextPath}/category/add"
           class="btn btn-primary">
            + Add Category
        </a>
    </div>
</div>
</body>
</html>