<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">Category List</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty categories}">
                        <div class="alert alert-warning text-center">
                            No Categories Found
                        </div>
                    </c:if>

                    <c:if test="${not empty categories}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Category</th>
                                <th>Super Category</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="category" items="${categories}">
                                <tr>
                                    <td>${category.id}</td>
                                    <td>${category.identifier}</td>
                                    <td>${category.superCategory}</td>
                                    <td>
                                    <a class="btn btn-warning btn-sm"
                                         href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}">
                                         Edit
                                    </a>
                                    </td>
                                    <td>
                                        <a href="/category/delete?identifier=${category.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this category?');">
                                           Delete
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>
                    <a href="/category/add" class="btn btn-success">
                        + Add New Category
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>