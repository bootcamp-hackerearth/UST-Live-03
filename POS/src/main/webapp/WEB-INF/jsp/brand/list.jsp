<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-brand-btn {
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

        <h2 class="text-center mb-4">Brand Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/brand/add"
               class="btn btn-success add-brand-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Brand
            </a>
        </div>

        <c:if test="${empty brands}">
            <div class="text-center text-muted p-5">
                No brands available
            </div>
        </c:if>

        <c:if test="${not empty brands}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Brand Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="brand" items="${brands}">
                    <tr>

                        <td class="text-center">${brand.id}</td>

                        <td class="fw-semibold">${brand.identifier}</td>

                        <td>${brand.description}</td>

                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/brand/update"
                                  class="d-inline">


                                <input type="hidden" name="id" value="${brand.id}">
                                <input type="hidden" name="identifier" value="${brand.identifier}">
                                <input type="hidden" name="description" value="${brand.description}">
                                <input type="hidden" name="status" value="${!brand.status}">

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${brand.status ? "checked" : ""}
                                           onchange="this.form.submit()">

                                    <span class="${brand.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${brand.status ? 'Active' : 'Deactive'}
                                    </span>
                                </div>
                            </form>
                        </td>


                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/brand/get?identifier=${brand.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/brand/delete?identifier=${brand.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this brand?');">
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