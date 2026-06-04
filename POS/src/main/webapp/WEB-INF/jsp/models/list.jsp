<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Model List</title>

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

        .form-switch .form-check-input {
            width: 45px;
            height: 22px;
            cursor: pointer;
        }

        .table th, .table td {
            vertical-align: middle;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Model Management</span>

        <div class="d-flex gap-2">
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>

            <a href="${pageContext.request.contextPath}/models/add"
               class="btn btn-success btn-sm">
                + Add Model
            </a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-4">
        <h3 class="text-center mb-4 fw-bold">Model List</h3>
        <c:if test="${empty models}">
            <div class="alert alert-warning text-center">
                No models found
            </div>
        </c:if>

        <c:if test="${not empty models}">
            <div class="table-responsive">
                <table class="table table-hover table-bordered text-center align-middle">
                    <thead class="table-dark">
                    <tr>
                        <th>Identifier</th>
                        <th>Model Name</th>
                        <th>Status</th>
                        <th width="200">Actions</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach var="model" items="${models}">
                        <tr>
                            <td>${model.identifier}</td>
                            <td>${model.modelName}</td>
                            <td>
                                <div class="form-check form-switch d-flex justify-content-center">
                                    <input class="form-check-input" type="checkbox"
                                    onclick="toggleModel('${model.identifier}')"
                                    <c:if test="${model.status}">checked</c:if> />
                                </div>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/models/edit/${model.identifier}"
                                   class="btn btn-primary btn-sm">
                                    Edit
                                </a>
                                <a href="${pageContext.request.contextPath}/models/delete/${model.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Delete this model?')">
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
    function toggleModel(identifier) {
        window.location.href =
            "${pageContext.request.contextPath}/models/toggle?identifier=" + identifier;
    }
</script>

</body>
</html>