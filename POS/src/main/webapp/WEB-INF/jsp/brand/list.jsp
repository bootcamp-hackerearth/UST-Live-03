<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Brand Management | List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f8f9fa;
            padding: 40px 0;
            font-family: 'Segoe UI', sans-serif;
        }

        .card-custom {
            border-radius: 15px;
            border: none;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .table thead {
            background-color: #212529;
            color: white;
        }

        .form-check-input {
            cursor: pointer;
            width: 2.5em;
            height: 1.25em;
        }

        .form-check-input:checked {
            background-color: #198754;
            border-color: #198754;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card card-custom p-4">

        <!-- HEADER -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="mb-0 text-primary fw-bold">Brand Management List</h2>

            <div>
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary rounded-pill me-2">
                    Back
                </a>

                <a href="${pageContext.request.contextPath}/brand/add"
                   class="btn btn-success rounded-pill">
                    Add New Brand
                </a>
            </div>
        </div>

        <!-- MESSAGE -->
        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <!-- TABLE -->
        <div class="table-responsive">
            <table class="table table-hover table-bordered align-middle">
                <thead class="text-center">
                    <tr>
                        <th style="width: 10%;">ID</th>
                        <th style="width: 25%;">Identifier</th>
                        <th style="width: 25%;">Description</th>
                        <th style="width: 15%;">Status</th>
                        <th style="width: 25%;">Actions</th>
                    </tr>
                </thead>

                <tbody>
                <c:forEach var="item" items="${brands}">
                    <tr>
                        <td class="text-center fw-bold">#${item.id}</td>

                        <td>${item.identifier}</td>

                        <td>${item.description}</td>

                        <!-- ✅ TOGGLE STATUS -->
                        <td class="text-center">
                            <form method="get"
                                  action="${pageContext.request.contextPath}/brand/toggle">

                                <input type="hidden" name="identifier" value="${item.identifier}"/>
                                <input type="hidden" name="status" value="${!item.status}"/>

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${item.status ? "checked" : ""}
                                           onchange="this.form.submit()"/>

                                    <span class="${item.status ? 'text-success' : 'text-danger'} fw-bold small">
                                        ${item.status ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                            </form>
                        </td>

                        <!-- ACTIONS -->
                        <td class="text-center">
                            <div class="d-flex justify-content-center gap-2">

                                <a href="${pageContext.request.contextPath}/brand/get?identifier=${item.identifier}"
                                   class="btn btn-warning btn-sm fw-bold">
                                    Edit
                                </a>

                                <a href="${pageContext.request.contextPath}/brand/delete?identifier=${item.identifier}"
                                   class="btn btn-danger btn-sm fw-bold"
                                   onclick="return confirm('Delete brand: ${item.identifier}?')">
                                    Delete
                                </a>

                            </div>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty brands}">
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">
                            No brands found.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>