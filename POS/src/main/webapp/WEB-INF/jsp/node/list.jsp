<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
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
        .h4{
            background-color: #ffffff;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header text-black text-center ">
                    <h4 class="mb-0">List of Nodes</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty nodes}">
                        <div class="alert alert-warning text-center">
                            No roles found
                        </div>
                    </c:if>

                    <c:if test="${not empty nodes}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Node</th>
                                <th>Path</th>
                                <th>Roles</th>
                                <th>Delete</th>
                                <th>Update</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="node" items="${nodes}">
                                <tr>
                                    <td>
                                        <a href="/node/get?identifier=${node.identifier}"
                                           class="text-decoration-none fw-semibold">
                                            ${node.id}
                                        </a>
                                    </td>
                                    <td>${node.identifier}</td>
                                    <td>${node.path}</td>
                                    <td>${node.roles}</td>
                                    <td>
                                      <a href="/node/delete?identifier=${node.identifier}"
                                         class="btn btn-pos-delete btn-sm"
                                         onclick="return confirm('Are you sure you want to delete this role?');">
                                         delete</a>
                                    </td>
                                     <td>
                                          <a class="btn btn-pos-update btn-sm"
                                        href="/node/get?identifier=${node.identifier}" >
                                            Update
                                          </a>
                                     </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="/node/add" class="btn btn-success">
                        + Add New Node
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>