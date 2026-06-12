<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management | List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

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

<div class="container">

    <!-- ✅ HEADER -->
    <div class="page-header">
        <h3 class="mb-0 text-primary">Warehouse Management</h3>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-secondary btn-sm">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/warehouse/add"
               class="btn btn-primary btn-sm">
                + Add Warehouse
            </a>
        </div>
    </div>

    <!-- ✅ TABLE -->
    <div class="table-container">
        <table class="table table-hover align-middle">

            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Location</th>
                <th>Manager</th>
                <th class="text-center">Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="w" items="${warehouse}">
                <tr>

                    <td class="fw-semibold">#${w.id}</td>

                    <td>
                        <span class="identifier-code">${w.identifier}</span>
                    </td>

                    <td class="warehouse-name">${w.location}</td>

                    <td>${w.manager}</td>

                    <!-- ✅ STATUS TOGGLE -->
                    <td class="text-center">
                        <form method="get"
                              action="${pageContext.request.contextPath}/warehouse/toggle"
                              class="d-inline">

                            <input type="hidden" name="identifier" value="${w.identifier}">
                            <input type="hidden" name="status" value="${!w.status}">

                            <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                <input class="form-check-input"
                                       type="checkbox"
                                       ${w.status ? "checked" : ""}
                                       onchange="this.form.submit()">

                                <span class="status-label ${w.status ? 'text-success' : 'text-danger'}">
                                    ${w.status ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </form>
                    </td>

                    <!-- ✅ ACTIONS -->
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-1">

                            <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${w.identifier}"
                               class="btn btn-outline-primary btn-sm btn-action">
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${w.identifier}"
                               class="btn btn-outline-danger btn-sm btn-action"
                               onclick="return confirm('Confirm deletion of this warehouse?')">
                                Delete
                            </a>

                        </div>
                    </td>

                </tr>
            </c:forEach>

            <c:if test="${empty warehouse}">
                <tr>
                    <td colspan="6" class="text-center py-5 text-muted">
                        No warehouse records found.
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