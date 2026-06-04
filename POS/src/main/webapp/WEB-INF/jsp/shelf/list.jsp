<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Shelf List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .btn {
            border-radius: 10px;
        }

        .table th, .table td {
            vertical-align: middle;
        }

        .form-switch .form-check-input {
            width: 45px;
            height: 22px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">

        <span class="navbar-brand fw-bold">Shelf Management</span>
        <div class="d-flex gap-2">

            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-light btn-sm">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/shelf/add"
               class="btn btn-light btn-sm fw-semibold">
                + Add Shelf
            </a>

        </div>

    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">
        <h3 class="fw-bold mb-3 text-center">Shelf List</h3>
        <c:if test="${empty shelves}">
            <div class="text-center py-4 text-muted">
                No shelves available
            </div>
        </c:if>
        <div class="table-responsive">
            <table class="table table-hover table-striped mb-0">
                <thead class="table-dark">
                <tr>
                    <th>Identifier</th>
                    <th>Name</th>
                    <th class="text-center">Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>

                <c:forEach var="s" items="${shelves}">
                    <tr>
                        <td class="fw-semibold">${s.identifier}</td>
                        <td>${s.name}</td>
                        <td class="text-center">
                            <div class="form-check form-switch d-flex justify-content-center">
                                <input class="form-check-input"
                                       type="checkbox"
                                       <c:if test="${s.status}">checked</c:if>
                                       onchange="toggleShelf('${s.identifier}')" />
                            </div>
                        </td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/shelf/edit?identifier=${s.identifier}"
                              class="btn btn-sm btn-outline-primary me-2">
                                Update
                            </a>

                            <a href="${pageContext.request.contextPath}/shelf/delete?identifier=${s.identifier}"
                              class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Delete this shelf?');">
                                Delete
                            </a>
                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
    function toggleShelf(identifier) {
        window.location.href =
            "${pageContext.request.contextPath}/shelf/toggle?identifier=" + identifier;
    }
</script>

</body>
</html>