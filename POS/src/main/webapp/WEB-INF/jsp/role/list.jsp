<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6fa;
        }
        .card-custom {
            max-width: 900px;
            margin: auto;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card card-custom p-4">


        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle me-2"></i>
                ${successMessage}
                <button class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle me-2"></i>
                ${errorMessage}
                <button class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>


        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="mb-0">Role List</h3>
            <div>
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-outline-secondary me-2">
                    <i class="bi bi-arrow-left-circle"></i> Home
                </a>
                <a href="${pageContext.request.contextPath}/role/add"
                   class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> Add Role
                </a>
            </div>
        </div>

        <table class="table table-hover table-bordered align-middle">
            <thead class="table-dark text-center">
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Description</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td class="text-center">${role.id}</td>
                    <td class="text-center fw-semibold">${role.identifier}</td>
                    <td>${role.description}</td>


                    <td class="text-center">

                        <span class="badge ${role.status ? 'bg-success' : 'bg-secondary'} mb-2">
                            ${role.status ? 'Active' : 'Inactive'}
                        </span>

                        <form action="${pageContext.request.contextPath}/role/toggle"
                              method="post"
                              style="display:inline;">
                            <input type="hidden" name="identifier" value="${role.identifier}">

                            <button type="submit"
                                    class="btn btn-sm ${role.status ? 'btn-secondary' : 'btn-success'} ms-2">
                                <i class="bi ${role.status ? 'bi-toggle-off' : 'bi-toggle-on'}"></i>
                            </button>
                        </form>
                    </td>


                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}"
                           class="btn btn-sm btn-warning me-1">
                            Edit
                        </a>

                        <form action="${pageContext.request.contextPath}/role/delete"
                              method="post"
                              style="display:inline;">
                            <input type="hidden" name="identifier" value="${role.identifier}">
                            <button type="submit"
                                    class="btn btn-sm btn-danger"
                                    onclick="return confirm('Are you sure you want to delete this role?');">
                                Delete
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>