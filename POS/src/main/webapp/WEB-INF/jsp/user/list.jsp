<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>


    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            min-height: 100vh;
        }

        .card {
            border-radius: 15px;
        }

        .table th {
            background-color: #343a40;
            color: white;
        }

        a.user-link {
            text-decoration: none;
            font-weight: 500;
        }

        a.user-link:hover {
            text-decoration: underline;
        }

        h4{
            background-color: #ffffff;
          }
            .btn-pos-update {
                              background-color: #4b6cb7;
                              border-color: #4b6cb7;
                              color: #fff;
                          }

                          .btn-pos-update:hover {
                              background-color: #3f5fa7;
                              border-color: #3f5fa7;
                              color: #fff;
                          }

                          .btn-pos-delete {
                              background-color: #f5f7fa;
                              border: 1px solid #dc3545;
                              color: #dc3545;
                          }

                          .btn-pos-delete:hover {
                              background-color: #dc3545;
                              color: #fff;
                          }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <h3 class="text-center mb-4 text-black">User Management</h3>


            <c:if test="${empty users}">
                <div class="alert alert-warning text-center">
                    No users found
                </div>
            </c:if>

            <c:if test="${not empty users}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead>
                        <tr>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Phone</th>
                            <th>Roles</th>
                            <th>Delete</th>
                            <th>Update</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>
                                    <a class="user-link"
                                       href="/user/get?username=${user.username}">
                                        ${user.username}
                                    </a>
                                </td>
                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>
                                <td>
                                    <a class="btn btn-pos-delete btn-sm"
                                       href="/user/delete?username=${user.username}"
                                       onclick="return confirm('Are you sure you want to delete this user?');">
                                        Delete
                                    </a>

                                    <td>
                                       <a class="btn btn-pos-update btn-sm"
                                        href="/user/get?username=${user.username}"
                                >
                                         Update
                               </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

        </div>

        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="/" class="btn btn-secondary">
                    Home
                </a>

                <a href="/register" class="btn btn-success">
                    Register
                </a>
            </div>

            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>