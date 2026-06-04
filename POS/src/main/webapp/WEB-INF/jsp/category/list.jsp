<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <!--  Bootstrap CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <style>
        body {
            background-color: #f4f6f9;
        }
        .table thead th {
            background-color: #f1f1f1;
            font-weight: 600;
            text-align: center;
        }
        .table td {
            vertical-align: middle;
            text-align: center;
        }
        .actions a {
            margin-right: 8px;
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <!--  Header -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Category List</h3>

        <!--  Buttons -->
        <div class="d-flex gap-2">
            <a href="/" class="btn btn-secondary">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/category/add"
               class="btn btn-primary">
                + Add Category
            </a>
        </div>
    </div>

    <!--  Table -->
    <div class="card shadow">
        <div class="card-body p-0">
            <table class="table table-bordered table-hover mb-0">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Super Category</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="cat" items="${categories}">
                    <tr>
                        <td>${cat.id}</td>
                        <td>${cat.identifier}</td>

                        <td>
                            <c:choose>
                                <c:when test="${not empty cat.supercategory}">
                                    ${cat.supercategory}
                                </c:when>
                                <c:otherwise>
                                    -
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/category/save?id=${cat.id}"
                               class="btn btn-warning btn-sm">
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/category/delete?id=${cat.id}"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Are you sure?');">
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty categories}">
                    <tr>
                        <td colspan="4">No categories found</td>
                    </tr>
                </c:if>
                </tbody>

            </table>
        </div>
    </div>

</div>
</body>
</html>