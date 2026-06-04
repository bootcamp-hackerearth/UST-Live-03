<%@ page contentType="text/html;charset=UTF-8" %>
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
        .add-user-btn {
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

        .username-link {
            color: #5563DE;
            font-weight: 600;
            text-decoration: none;
        }

        .username-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>


    <c:if test="${not empty successMessage}">
    <div class="alert alert-success text-center">
    ${successMessage}
    </div>
    </c:if>


    <c:if test="${not empty infoMessage}">
    <div class="alert alert-info text-center">
    ${infoMessage}
    </div>
    </c:if>

<div class="container mt-4">

    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">User Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-success add-user-btn">
                <i class="bi bi-person-plus"></i>
                Add New User
            </a>
        </div>

        <c:if test="${empty users}">
            <div class="text-center text-muted p-5">
                No users found
            </div>
        </c:if>

        <c:if test="${not empty users}">
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
                            <a class="username-link"
                               href="${pageContext.request.contextPath}/user/get?username=${u.username}">
                                ${u.username}
                            </a>
                        </td>
                        <td>${u.name}</td>
                        <td>${u.phoneNo}</td>
                        <td>${u.roles}</td>
                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/user/get?username=${u.username}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/user/delete?username=${u.username}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this user?');">
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