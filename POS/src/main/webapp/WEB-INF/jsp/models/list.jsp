<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            width: 800px;
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255,255,255,0.9);
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            font-weight: 600;
        }
        table th { background: #e5e7eb; }
        table td { background: #f9fafb; }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            color: white;
            text-decoration: none;
        }
        .edit-btn { background: #3b82f6; }
        .delete-btn { background: #ef4444; }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }
        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }
        .slider {
            position: absolute;
            cursor: pointer;
            background-color: #dc2626;
            border-radius: 24px;
            top: 0; left: 0; right: 0; bottom: 0;
            transition: 0.4s;
        }
        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.4s;
        }
        input:checked + .slider {
            background-color: #22c55e;
        }
        input:checked + .slider:before {
            transform: translateX(22px);
        }
        .toggle-container {
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="card shadow-lg">
    <div class="card-header text-center">
        <h4 class="mb-0">Model List</h4>
    </div>
    <div class="card-body">
        <c:if test="${empty models}">
            <div class="alert alert-warning text-center">
                No models available
            </div>
        </c:if>
        <c:if test="${not empty models}">
            <table class="table table-bordered table-hover text-center align-middle">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Model Name</th>
                    <th>Status</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="model" items="${models}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${model.identifier}</td>
                        <td>
                            <div class="toggle-container"
                                 onclick="window.location.href='${pageContext.request.contextPath}/models/toggle?identifier=${model.identifier}'">

                                <label class="switch">
                                    <input type="checkbox"
                                           ${model.status == 'ACTIVE' ? 'checked' : ''}
                                           disabled>
                                    <span class="slider"></span>
                                </label>
                            </div>
                        </td>
                        <td>
                            <a class="icon-btn edit-btn"
                               href="${pageContext.request.contextPath}/models/get?identifier=${model.identifier}">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                        </td>
                        <td>
                            <a class="icon-btn delete-btn"
                               href="${pageContext.request.contextPath}/models/delete?identifier=${model.identifier}"
                               onclick="return confirm('Delete this model?')">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
    <div class="card-footer text-center">
        <a class="btn btn-secondary me-2"
           href="${pageContext.request.contextPath}/">
            Home
        </a>
        <a class="btn btn-primary"
           href="${pageContext.request.contextPath}/models/add">
            + Add Model
        </a>
    </div>
</div>
</body>
</html>