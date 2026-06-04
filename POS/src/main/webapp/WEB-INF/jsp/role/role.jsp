<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <style>
        body {
            background: #f5f7fb;
        }

        .page-container {
            max-width: 500px;
            margin: auto;
        }

        .card-custom {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            padding: 30px;
        }

        h2 {
            text-align: center;
            font-weight: 600;
            margin-bottom: 25px;
            color: #198754;
        }

        .form-label {
            font-weight: 600;
            color: #333;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px;
        }

        .form-control[readonly] {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .btn-primary, .btn-secondary {
            padding: 10px 30px;
            border-radius: 30px;
            font-weight: 600;
        }

        .action-buttons {
            text-align: center;
            margin-top: 25px;
        }

        .error-text {
            color: red;
            text-align: center;
            margin-top: 10px;
        }
    </style>
</head>

<body>

<div class="container mt-5 page-container">
    <div class="card-custom">

        <h2>Edit Role</h2>

        <form action="${pageContext.request.contextPath}/role/update" method="post">

            <input type="hidden" name="id" value="${role.id}" />

            <div class="mb-3">
                <label class="form-label">Role Name</label>
                <input type="text"
                       name="identifier"
                       value="${role.identifier}"
                       class="form-control"
                       readonly />
            </div>

            <div class="mb-3">
                <label class="form-label">Description</label>
                <input type="text"
                       name="description"
                       value="${role.description}"
                       class="form-control"
                       required />
            </div>

            <c:if test="${not empty error}">
                <div class="error-text">${error}</div>
            </c:if>

            <div class="action-buttons">
                <button type="submit" class="btn btn-primary">Update</button>

                <a href="${pageContext.request.contextPath}/role/list"
                   class="btn btn-secondary ms-3">Cancel</a>
            </div>
        </form>

    </div>
</div>

</body>
</html>