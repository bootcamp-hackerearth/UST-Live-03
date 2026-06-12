<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <p style="color:red;">${message}</p>
            <div class="card shadow p-4 rounded">
                <h3 class="text-center mb-4">Add Role</h3>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger text-center">
                        ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/role/add" method="post">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Role Name</label>
                        <input type="text"
                               name="identifier"
                               class="form-control"
                               placeholder="ADMIN"
                               required />
                    </div>
                    <div class="mb-3">
                    <label class="form-label fw-semibold">Description</label>
                    <input type="text"
                           name="description"
                           class="form-control"
                           placeholder="ADMIN"
                           required />
                    </div>

                    <div class="d-flex justify-content-end gap-2">
                        <button type="submit" class="btn btn-success">
                            Save
                        </button>

                        <a href="${pageContext.request.contextPath}/role/list"
                           class="btn btn-secondary">
                            Cancel
                        </a>
                    </div>

                </form>
            </div>

        </div>
    </div>
</div>

</body>
</html>