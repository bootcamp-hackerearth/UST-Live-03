<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Category Management | List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .container {
            margin-top: 50px;
        }

        .page-header {
            background: #ffffff;
            padding: 20px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-container {
            background: #ffffff;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .table thead {
            background-color: #0d6efd;
            color: white;
        }

        .table thead th {
            border: none;
            padding: 15px;
        }

        .form-check-input {
            cursor: pointer;
            width: 2.5em;
            height: 1.25em;
        }

        .form-check-input:checked {
            background-color: #198754;
            border-color: #198754;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="page-header">
        <h2 class="mb-0 text-primary">Category List</h2>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/category/add" class="btn btn-primary">
                Add New Category
            </a>
        </div>
    </div>

    <!-- TABLE -->
    <div class="table-container">
        <table class="table table-hover align-middle">
            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Super Category</th>
                <th class="text-center">Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="item" items="${category}">
                <tr>

                    <td class="fw-bold">#${item.id}</td>

                    <td>${item.identifier}</td>

                    <td>${item.superCategory}</td>

                    <!-- ✅ STATUS TOGGLE -->
                    <td class="text-center">
                        <form method="get"
                              action="${pageContext.request.contextPath}/category/toggle"
                              class="d-inline">

                            <input type="hidden" name="identifier" value="${item.identifier}"/>
                            <input type="hidden" name="status" value="${!item.status}"/>

                            <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                <input class="form-check-input me-2"
                                       type="checkbox"
                                       ${item.status ? "checked" : ""}
                                       onchange="this.form.submit()"/>

                                <span class="${item.status ? 'text-success' : 'text-danger'} fw-bold small">
                                    ${item.status ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </form>
                    </td>

                    <!-- ACTIONS -->
                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/category/get?identifier=${item.identifier}"
                           class="btn btn-outline-primary btn-sm me-1">
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/category/delete?identifier=${item.identifier}"
                           class="btn btn-outline-danger btn-sm"
                           onclick="return confirm('Delete this category?')">
                            Delete
                        </a>
                    </td>

                </tr>
            </c:forEach>

            <c:if test="${empty category}">
                <tr>
                    <td colspan="5" class="text-center py-4 text-muted">
                        No categories found.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>