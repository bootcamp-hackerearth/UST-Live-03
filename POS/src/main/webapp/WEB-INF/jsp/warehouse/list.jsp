<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Warehouse List</title>

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

        .welcome-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
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
            height: 23px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: 0.4s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 17px;
            width: 17px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #007bff;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>
<body>
<div class="content">
    <div class="page-wrapper">
        <div class="header-banner">
            <h2>Warehouse List</h2>
            <p>View and manage warehouses</p>
        </div>
        <div class="welcome-card">
            <div class="d-flex justify-content-between mb-3">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-sm">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/warehouse/add"
                   class="btn btn-primary btn-sm">
                    + Add Warehouse
                </a>
            </div>
            <c:if test="${empty warehouses}">
                <div class="alert alert-info text-center">
                    No Warehouses Available
                </div>
            </c:if>
            <c:if test="${not empty warehouses}">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Warehouse Name</th>
                        <th>Location</th>
                        <th>Contact Person</th>
                        <th>Phone Number</th>
                        <th>Status</th>
                        <th style="width:180px;">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${warehouses}" var="wh">
                        <tr>
                            <td>${wh.identifier}</td>
                            <td>${wh.location}</td>
                            <td>${wh.contactPerson}</td>
                            <td>${wh.phoneNumber}</td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/warehouse/toggleStatus"
                                      method="get">
                                    <input type="hidden" name="identifier"
                                           value="${wh.identifier}"/>
                                    <label class="switch">
                                        <input type="checkbox"
                                               onchange="this.form.submit()"
                                               <c:if test="${wh.status}">checked</c:if>>
                                        <span class="slider"></span>
                                    </label>
                                </form>
                                <div class="mt-1 text-primary" style="font-size:12px;">
                                    <c:choose>
                                        <c:when test="${wh.status}">
                                            Warehouse is active
                                        </c:when>
                                        <c:otherwise>
                                            Warehouse is inactive
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${wh.identifier}"
                                   class="btn btn-success btn-sm mr-2">
                                    Update
                                </a>
                                <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${wh.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Are you sure you want to delete this warehouse?');">
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