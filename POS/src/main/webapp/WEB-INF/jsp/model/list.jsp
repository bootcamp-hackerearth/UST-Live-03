<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-model-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
        }

        .back-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
            margin-right: 15px;
        }

        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }

        .form-check-input {
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Model Management</h2>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success text-center">
                ${successMessage}
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger text-center">
                ${errorMessage}
            </div>
        </c:if>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/model/add"
               class="btn btn-success add-model-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Model
            </a>
        </div>

        <c:if test="${empty models}">
            <div class="text-center text-muted p-5">
                No models available
            </div>
        </c:if>

        <c:if test="${not empty models}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Model Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="model" items="${models}">
                    <tr>

                        <td class="text-center">${model.id}</td>
                        <td class="fw-semibold">${model.identifier}</td>

                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/model/update"
                                  class="d-inline">

                                <input type="hidden" name="id" value="${model.id}">
                                <input type="hidden" name="identifier" value="${model.identifier}">
                                <input type="hidden" name="status" value="${!model.status}">

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${model.status ? "checked" : ""}
                                           onchange="this.form.submit()">

                                    <span class="${model.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${model.status ? 'Active' : 'Deactive'}
                                    </span>
                                </div>
                            </form>
                        </td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/model/get?identifier=${model.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/model/delete?identifier=${model.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this model?');">
                                <i class="bi bi-trash"></i>
                                Delete
                            </a>
                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>
</div>
<script>
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(e => e.style.display = 'none');
    }, 3000);
</script>
</body>
</html>