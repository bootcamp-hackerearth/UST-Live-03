<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit Management</title>

    <!-- Bootstrap -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">



    <style>
            body {
                background-color: #f4f7f6;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                margin-top: 40px;
                margin-bottom: 40px;
            }

            /* ===== Header ===== */
            .page-header {
                background: #ffffff;
                padding: 22px 26px;
                border-radius: 14px;
                box-shadow: 0 4px 14px rgba(0,0,0,0.06);
                margin-bottom: 28px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            /* ===== Table Card ===== */
            .table-container {
                background: #ffffff;
                border-radius: 14px;
                padding: 20px;
                box-shadow: 0 12px 30px rgba(0,0,0,0.08);
            }

            .table thead th {
                background-color: #0d6efd;
                color: #ffffff;
                padding: 14px;
                border: none;
            }

            .table tbody td {
                vertical-align: middle;
            }

            /* ===== Text styles ===== */
            .warehouse-name {
                font-weight: 600;
                color: #2c3e50;
            }

            .identifier-code {
                font-size: 0.9rem;
                color: #0d6efd;
                background: #eef3ff;
                padding: 3px 6px;
                border-radius: 6px;
            }

            /* ===== Toggle ===== */
            .status-label {
                font-size: 0.9rem;
                font-weight: 600;
            }

            /* ===== Buttons ===== */
            .btn-action {
                padding: 5px 12px;
                border-radius: 8px;
            }
        </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Unit Management</h2>

        <!-- ACTION BUTTONS -->
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

        <!-- EMPTY MESSAGE -->
        <c:if test="${empty units}">
            <div class="text-center text-muted p-5">
                No units available
            </div>
        </c:if>

        <!-- UNIT TABLE -->
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

                        <!-- ID -->
                        <td class="text-center">${unit.id}</td>

                        <!-- UNIT NAME -->
                        <td class="fw-semibold">${unit.identifier}</td>

                        <!-- ✅ STATUS TOGGLE -->
                        <td class="text-center">
                            <form method="get"
                                  action="${pageContext.request.contextPath}/unit/toggle"
                                  class="d-inline">

                                <input type="hidden" name="identifier"
                                       value="${unit.identifier}">
                                <input type="hidden" name="status"
                                       value="${!unit.status}">

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${unit.status ? "checked" : ""}
                                           onchange="this.form.submit()">

                                    <span class="${unit.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${unit.status ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                            </form>
                        </td>

                        <!-- ACTIONS -->
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