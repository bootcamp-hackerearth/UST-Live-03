<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Rack Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #2e2e2e;
        }

        .page-header {
            background-color: #000;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #fff;
        }

        .page-header h3 {
            margin: 0;
            font-weight: 600;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0,0,0,0.06);
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
        }

        .badge-shelf {
            margin: 2px;
            font-size: 12px;
            background-color: #0dcaf0;
            color: #000;
        }

        .action-icon {
            font-size: 1.1rem;
            margin: 0 8px;
            transition: transform 0.15s ease;
        }

        .action-icon:hover {
            transform: scale(1.15);
        }

        .edit-icon {
            color: #0d6efd;
        }

        .delete-icon {
            color: #dc3545;
        }

        .add-btn {
            background-color: #198754;
            border-color: #198754;
            color: #fff;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Rack Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/rack/add"
               class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Rack
            </a>
        </div>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success text-center fw-semibold">
            ${successMessage}
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger text-center fw-semibold">
            ${errorMessage}
        </div>
    </c:if>

    <div class="card shadow">
        <div class="card-body p-0">

            <c:if test="${not empty racks}">
                <table class="table table-hover align-middle mb-0">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Rack Name</th>
                        <th>Shelves</th>
                        <th>Status</th>
                        <th class="text-center" style="width:160px;">Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="rack" items="${racks}">
                        <tr>
                            <td>${rack.id}</td>
                            <td class="fw-semibold">${rack.identifier}</td>

                            <td>
                                <c:if test="${empty rack.shelfs}">
                                    <span class="text-muted">No shelves</span>
                                </c:if>
                                <c:forEach var="shelf" items="${rack.shelfs}">
                                    <span class="badge badge-shelf">${shelf}</span>
                                </c:forEach>
                            </td>

                            <td>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/rack/update"
                                      class="d-inline">
                                    <input type="hidden"
                                           name="${_csrf.parameterName}"
                                           value="${_csrf.token}" />
                                    <input type="hidden" name="id" value="${rack.id}">
                                    <input type="hidden" name="identifier" value="${rack.identifier}">
                                    <input type="hidden" name="status" value="${!rack.status}">

                                    <div class="form-check form-switch">
                                        <input class="form-check-input"
                                               type="checkbox"
                                               ${rack.status ? "checked" : ""}
                                               onchange="this.form.submit()">
                                        <span class="fw-semibold ms-2
                                            ${rack.status ? 'text-success' : 'text-secondary'}">
                                            ${rack.status ? 'ACTIVE' : 'INACTIVE'}
                                        </span>
                                    </div>
                                </form>
                            </td>

                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/rack/get?identifier=${rack.identifier}"
                                   class="action-icon edit-icon">
                                    <i class="bi bi-pencil-square"></i>
                                </a>

                                <a href="${pageContext.request.contextPath}/rack/delete?identifier=${rack.identifier}"
                                   class="action-icon delete-icon"
                                   onclick="return confirm('Are you sure you want to delete this rack?');">
                                    <i class="bi bi-trash"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty racks}">
                <div class="text-center text-muted py-5">
                    No racks available.
                </div>
            </c:if>

        </div>
    </div>
</div>

</body>
</html>
