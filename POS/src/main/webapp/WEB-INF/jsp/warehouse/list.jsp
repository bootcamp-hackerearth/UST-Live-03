<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-warehouse-btn {
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
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="card card-custom p-4">
        <h2 class="text-center mb-4">Warehouse Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i> Back
            </a>

            <a href="${pageContext.request.contextPath}/warehouse/add"
               class="btn btn-success add-warehouse-btn">
                <i class="bi bi-plus-circle"></i> Add Warehouse
            </a>
        </div>

        <c:if test="${empty warehouses}">
            <div class="text-center text-muted p-5">
                No warehouses available
            </div>
        </c:if>

        <c:if test="${not empty warehouses}">
            <table class="table table-bordered table-hover align-middle">

                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Warehouse Name</th>
                    <th>Location</th>
                    <th>Manager</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="warehouse" items="${warehouses}">
                    <tr>

                        <td class="text-center">${warehouse.id}</td>
                        <td>${warehouse.identifier}</td>
                        <td class="text-center">${warehouse.location}</td>
                        <td class="text-center">${warehouse.manager}</td>

                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/warehouse/toggle">

                                <input type="hidden"
                                       name="identifier"
                                       value="${warehouse.identifier}"/>

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${warehouse.status ? "checked" : ""}
                                           onchange="this.form.submit()"/>

                                    <span class="${warehouse.status ? 'text-success' : 'text-danger'} fw-semibold">
                                            ${warehouse.status ? "Active" : "Inactive"}
                                    </span>
                                </div>
                            </form>
                        </td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${warehouse.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i> Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Delete this warehouse?')">
                                <i class="bi bi-trash"></i> Delete
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