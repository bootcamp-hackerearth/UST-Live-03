<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-unit-btn {
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

        <h2 class="text-center mb-4">Unit Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/unit/add"
               class="btn btn-success add-unit-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Unit
            </a>
        </div>

        <c:if test="${empty units}">
            <div class="text-center text-muted p-5">
                No units available
            </div>
        </c:if>

        <c:if test="${not empty units}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Unit Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="unit" items="${units}">
                    <tr>

                <td class="text-center">${unit.id}</td>
                <td class="fw-semibold">${unit.identifier}</td>
                <td class="text-center">
                    <form method="post"
                          action="${pageContext.request.contextPath}/unit/update"
                          class="d-inline">

                        <input type="hidden" name="id" value="${unit.id}">
                        <input type="hidden" name="identifier" value="${unit.identifier}">

                        <input type="hidden" name="status" value="${!unit.status}">

                        <div class="form-check form-switch d-flex justify-content-center align-items-center">
                            <input class="form-check-input me-2"
                                   type="checkbox"
                                   ${unit.status ? "checked" : ""}
                                   onchange="this.form.submit()">

                            <span class="${unit.status ? 'text-success' : 'text-danger'} fw-semibold">
                                ${unit.status ? 'Active' : 'Deactive'}
                            </span>
                        </div>
                    </form>
                </td>

                <td class="text-center">
                    <a href="${pageContext.request.contextPath}/unit/get?identifier=${unit.identifier}"
                       class="btn btn-sm btn-warning">
                        <i class="bi bi-pencil-square"></i>
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/unit/delete?identifier=${unit.identifier}"
                       class="btn btn-sm btn-danger"
                       onclick="return confirm('Are you sure you want to delete this unit?');">
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

</body>
</html>