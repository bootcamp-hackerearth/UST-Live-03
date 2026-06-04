<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Unit List</title>

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
        width: 42px;
        height: 20px;
        cursor: pointer;
    }
</style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Unit Management</span>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-light btn-sm">Home</a>

            <a href="${pageContext.request.contextPath}/unit/add"
               class="btn btn-light btn-sm fw-semibold">+ Add Unit</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">
        <h3 class="fw-bold mb-3 text-center">Unit List</h3>
        <div class="table-responsive">
            <table class="table table-hover table-striped mb-0">

                <thead class="table-dark">
                <tr>
                    <th>Identifier</th>
                    <th>Unit Name</th>
                    <th>Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:if test="${empty units}">
                    <tr>
                        <td colspan="4" class="text-center py-4 text-muted">
                            No units found.
                        </td>
                    </tr>
                </c:if>

                <c:forEach items="${units}" var="unit">
                    <tr>
                        <td class="fw-semibold">
                            ${unit.identifier}
                        </td>

                        <td>
                            ${unit.unitName}
                        </td>

                        <td class="text-center">
                            <div class="form-check form-switch d-flex justify-content-center">
                                <input class="form-check-input"
                                       type="checkbox"
                                       onchange="toggleStatus('${unit.identifier}')"
                                       <c:if test="${unit.status}">checked</c:if> />
                            </div>

                        </td>
                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/unit/get?identifier=${unit.identifier}"
                               class="btn btn-sm btn-outline-primary me-2">
                                Update
                            </a>

                            <a href="${pageContext.request.contextPath}/unit/delete?identifier=${unit.identifier}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Are you sure you want to delete this unit?');">
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
    function toggleStatus(identifier) {
        window.location.href =
            "${pageContext.request.contextPath}/unit/toggleStatus?identifier=" + identifier;
    }
</script>

</body>
</html>