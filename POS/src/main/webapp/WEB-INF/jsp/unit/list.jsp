<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Unit List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .content {
            margin: 50px auto;
            padding: 20px;
        }

        .page-wrapper {
            max-width: 900px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 22px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .list-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        }

        table th {
            font-weight: 600;
            font-size: 14px;
            background-color: #f8f9fc;
        }

        table td {
            font-size: 14px;
            vertical-align: middle;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #ccc;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #007bff;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>
<body>
<div class="content">
    <div class="page-wrapper">
        <div class="header-banner">
            <h4>Unit List</h4>
            <p>View and manage units</p>
        </div>
        <div class="list-card">
            <div class="d-flex justify-content-between mb-3">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-sm">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/unit/add"
                   class="btn btn-primary btn-sm">
                    + Add Unit
                </a>
            </div>
            <c:if test="${empty units}">
                <div class="alert alert-info text-center">
                    No units found.
                </div>
            </c:if>
            <c:if test="${not empty units}">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Identifier</th>
                        <th>Unit Name</th>
                        <th>Status</th>
                        <th style="width:180px;">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${units}" var="unit">
                        <tr>
                            <td>${unit.identifier}</td>
                            <td>${unit.name}</td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/unit/toggleStatus"
                                      method="get">
                                    <input type="hidden" name="identifier"
                                           value="${unit.identifier}"/>
                                    <label class="switch">
                                        <input type="checkbox"
                                               onchange="this.form.submit()"
                                               <c:if test="${unit.status}">checked</c:if>>
                                        <span class="slider"></span>
                                    </label>
                                </form>
                                <small class="text-primary">
                                    <c:choose>
                                        <c:when test="${unit.status}">
                                            Active
                                        </c:when>
                                        <c:otherwise>
                                            Inactive
                                        </c:otherwise>
                                    </c:choose>
                                </small>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/unit/get?identifier=${unit.identifier}"
                                   class="btn btn-success btn-sm mr-1">
                                    Update
                                </a>
                                <a href="${pageContext.request.contextPath}/unit/delete?identifier=${unit.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Delete this unit?');">
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
</div>
</body>
</html>