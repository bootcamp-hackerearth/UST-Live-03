<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Category List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            padding-top: 40px;
        }

        .card {
            width: 90%;
            max-width: 900px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            font-weight: 600;
            text-align: center;
            border-radius: 12px 12px 0 0;
        }

        .table th {
            background-color: #4e73df;
            color: #ffffff;
            font-weight: 500;
        }

        .btn-sm {
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
        }

        /* UPDATE BUTTON */
        .btn-outline-primary {
            background-color: #1cc88a;
            border-color: #1cc88a;
            color: #ffffff;
        }

        .btn-outline-primary:hover {
            background-color: #17a673;
            border-color: #17a673;
            color: #ffffff;
        }

        .btn-outline-danger {
            background-color: #e74a3b;
            border-color: #e74a3b;
            color: #ffffff;
        }

        .btn-outline-danger:hover {
            background-color: #c0392b;
            border-color: #c0392b;
            color: #ffffff;
        }
    </style>
</head>

<body>

<div class="card">
    <div class="card-header">
        Category List
    </div>
    <div class="card-body">
        <div class="mb-3 d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-secondary btn-sm">
                Back to Home
            </a>
            <a href="${pageContext.request.contextPath}/category/add"
               class="btn btn-primary btn-sm">
                + Add Category
            </a>
        </div>
        <table class="table table-bordered table-hover">
            <thead>
            <tr>
                <th>Identifier</th>
                <th>Name</th>
                <th>Super Category</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${empty categories}">
                <tr>
                    <td colspan="4" class="text-center text-muted">
                        No categories found
                    </td>
                </tr>
            </c:if>
            <c:forEach var="cat" items="${categories}">
                <tr>
                    <td>${cat.identifier}</td>
                    <td>${cat.name}</td>
                    <td>${cat.superCategory}</td>
                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}"
                           class="btn btn-outline-primary btn-sm mr-1">
                            Update
                        </a>
                        <a href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                           class="btn btn-outline-danger btn-sm"
                           onclick="return confirm('Are you sure you want to delete this category?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </body>
        </table>
    </div>
</div>
</body>
</html>