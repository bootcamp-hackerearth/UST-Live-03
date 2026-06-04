<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Models List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        h4{
            background-color: ;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
        .toggle-container {
            cursor: pointer;
            display: inline-block;
        }

        /* Switch container */
        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }

        /* Hide checkbox */
        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        /* Slider */
        .slider {
            position: absolute;
            cursor: pointer;
            background-color: #d1d5db; /* OFF gray */
            border-radius: 24px;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            transition: 0.4s;
        }

        /* Circle knob */
        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:not(:checked) + .slider {
        background-color: #dc2626; /* RED */
        }

        input:checked + .slider {
        background-color: #22c55e; /* GREEN */
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">Models List</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty models}">
                        <div class="alert alert-warning text-center">
                            No Models Found
                        </div>
                    </c:if>

                    <c:if test="${not empty models}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Models</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="models" items="${models}">
                                <tr>
                                    <td>${models.id}</td>
                                    <td>${models.identifier}</td>
                                    <td>${models.description}</td>
                                    <td>
                                        <div class="toggle-container"
                                             onclick="window.location.href='${pageContext.request.contextPath}/models/toggle?identifier=${models.identifier}'">
                                             <label class="switch">
                                             <input type="checkbox" ${models.status ? "checked" : ""} disabled>
                                               <span class="slider"></span>
                                             </label>
                                        </div>
                                    </td>
                                    <td>
                                    <a class="btn btn-warning btn-sm"
                                         href="${pageContext.request.contextPath}/models/get?identifier=${models.identifier}">
                                         Edit
                                    </a>
                                    </td>
                                    <td>
                                        <a href="/models/delete?identifier=${models.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this models?');">
                                           Delete
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
                    <a href="/models/add" class="btn btn-success">
                        + Add New Models
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>