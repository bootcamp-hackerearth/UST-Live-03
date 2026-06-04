<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6fa;
        }

        .card-custom {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            border-radius: 12px;
        }

        .add-btn,
        .back-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
        }

        .back-btn {
            margin-right: 15px;
        }

        .email-link {
            font-weight: 700;
            color: #0d6efd;
            text-decoration: none;
        }

        .email-link:hover {
            text-decoration: underline;
        }

        .user-role {
            font-weight: 600;
            color: #198754;
        }
    </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">User Management</h2>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-success add-btn">
                <i class="bi bi-person-plus-fill"></i>
                Add New User
            </a>
        </div>

        <table class="table table-hover table-bordered align-middle">
            <thead class="table-dark text-center">
            <tr>
                <th>Email</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Roles</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>
                        <a class="email-link"
                           href="${pageContext.request.contextPath}/user/get?username=${u.username}">
                            ${u.username}
                        </a>
                    </td>

                    <td>${u.name}</td>
                    <td>${u.phoneNo}</td>

                    <td>
                        <span class="user-role">${u.roles}</span>
                    </td>

                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/user/get?username=${u.username}"
                           class="btn btn-sm btn-warning me-1">
                            <i class="bi bi-pencil-square"></i> Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/user/delete?username=${u.username}"
                           class="btn btn-sm btn-danger"
                           onclick="return confirm('Are you sure you want to delete this user?');">
                            <i class="bi bi-trash"></i> Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty users}">
                <tr>
                    <td colspan="5" class="text-center text-muted">
                        No users available
                    </td>
                </tr>
            </c:if>
            </tbody>

        </table>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    setTimeout(function () {
        let alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            alert.style.display = 'none';
        });
    }, 3000);
</script>

</body>
</html>