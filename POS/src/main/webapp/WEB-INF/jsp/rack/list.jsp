<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Rack List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

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

        .table th,
        .table td {
            vertical-align: middle;
        }

        .badge {
            border-radius: 8px;
            margin-right: 4px;
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

        <span class="navbar-brand fw-bold">
            Rack Management
        </span>

        <div class="d-flex gap-2">

            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-light btn-sm">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/rack/add"
               class="btn btn-light btn-sm fw-semibold">
                + Add Rack
            </a>

        </div>

    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">

        <h3 class="fw-bold mb-3 text-center">
            Rack List
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <c:if test="${empty racks}">
            <div class="text-center text-muted py-4">
                No racks available
            </div>
        </c:if>

        <c:if test="${not empty racks}">
            <div class="table-responsive">

                <table class="table table-hover table-striped mb-0">

                    <thead class="table-dark">
                        <tr>
                            <th>Identifier</th>
                            <th>Rack Name</th>
                            <th>Shelves</th>
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="r" items="${racks}">
                            <tr>

                                <td class="fw-semibold">
                                    <a href="${pageContext.request.contextPath}/rack/edit?identifier=${r.identifier}"
                                       class="text-decoration-none">
                                        ${r.identifier}
                                    </a>
                                </td>

                                <td>
                                    ${r.name}
                                </td>

                                <td>

                                    <c:choose>

                                        <c:when test="${empty r.shelfIdentifiers}">
                                            <span class="text-muted">
                                                -
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <c:forEach var="s" items="${r.shelfIdentifiers}">
                                                <span class="badge bg-primary">
                                                    ${s}
                                                </span>
                                            </c:forEach>
                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>
                                    <div class="form-check form-switch d-flex justify-content-center">

                                        <input class="form-check-input"
                                               type="checkbox"
                                               onclick="toggleRack('${r.identifier}')"
                                               <c:if test="${r.status}">checked</c:if>/>

                                    </div>
                                </td>

                                <td class="text-center">

                                    <a href="${pageContext.request.contextPath}/rack/edit?identifier=${r.identifier}"
                                       class="btn btn-sm btn-outline-primary me-2">
                                        Update
                                    </a>

                                    <a href="${pageContext.request.contextPath}/rack/delete?identifier=${r.identifier}"
                                       class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('Are you sure you want to delete this rack?');">
                                        Delete
                                    </a>

                                </td>

                            </tr>
                        </c:forEach>

                    </tbody>

                </table>

            </div>
        </c:if>

    </div>
</div>

<script>
    function toggleRack(identifier) {
        window.location.href =
            "${pageContext.request.contextPath}/rack/toggle?identifier=" + identifier;
    }
</script>

</body>

</html>