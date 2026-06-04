<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelf Management</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>

<body>

<div class="container mt-4">
    <div class="card shadow p-4">
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show">
            ${successMessage}
            <button class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show">
            ${errorMessage}
            <button class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

        <h2 class="text-center mb-4">Shelf Management</h2>


        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-3">
                <i class="bi bi-arrow-left-circle"></i> Back
            </a>

            <a href="${pageContext.request.contextPath}/shelf/add"
               class="btn btn-success">
                <i class="bi bi-plus-circle"></i> Add Shelf
            </a>
        </div>


        <c:if test="${empty shelves}">
            <div class="text-center text-muted p-4">
                No shelves available
            </div>
        </c:if>


        <c:if test="${not empty shelves}">
            <table class="table table-bordered table-hover align-middle">

                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="shelf" items="${shelves}">
                    <tr>

                        <td class="text-center">${shelf.id}</td>
                        <td>${shelf.identifier}</td>


                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/shelf/toggle">

                                <input type="hidden" name="identifier"
                                       value="${shelf.identifier}" />

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${shelf.status ? "checked" : ""}
                                           onchange="this.form.submit()" />

                                    <span class="${shelf.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${shelf.status ? "Active" : "Inactive"}
                                    </span>
                                </div>
                            </form>
                        </td>


                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/shelf/get?identifier=${shelf.identifier}"
                               class="btn btn-warning btn-sm">
                                <i class="bi bi-pencil-square"></i> Edit
                            </a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/shelf/delete"
                                  class="d-inline"
                                  onsubmit="return confirm('Delete this shelf?');">

                                <input type="hidden" name="identifier"
                                       value="${shelf.identifier}" />

                                <button class="btn btn-danger btn-sm">
                                    <i class="bi bi-trash"></i> Delete
                                </button>
                            </form>
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