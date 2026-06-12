<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Warehouse</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
        }

        .card {
            border-radius: 14px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6) !important;
            border-top-left-radius: 14px;
            border-top-right-radius: 14px;
            color: #111827 !important;
        }

        .form-control {
            border-radius: 8px;
            border: 1px solid #d1d5db;
            background-color: #ffffff;
            transition: all 0.2s ease;
        }

        .form-control:focus {
            border-color: #9ca3af;
            box-shadow: 0 0 6px rgba(156, 163, 175, 0.4);
        }

        .btn-primary {
            background: #3b82f6;
            border: none;
        }

        .btn-primary:hover {
            background: #2563eb;
        }

        .card-footer {
            background-color: #f9fafb !important;
            color: #6b7280;
        }

        .back a {
            color: #374151;
            text-decoration: none;
            font-weight: 500;
        }

        .back a:hover {
            color: #111827;
            text-decoration: underline;
        }

        .error-message {
            color: #dc2626;
            text-align: center;
            margin-top: 10px;
        }
    </style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="error-message">
        ${message}
    </div>
</c:if>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">

            <div class="card-header text-center">
                <h4 class="mb-0">Add Warehouse</h4>
            </div>

            <div class="card-body">

                <form:form method="post"
                           action="${pageContext.request.contextPath}/warehouse/add"
                           modelAttribute="warehouseDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Warehouse Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter warehouse name"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Region</label>
                        <form:input path="region"
                                    cssClass="form-control"
                                    placeholder="Enter region"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Country</label>
                        <form:input path="country"
                                    cssClass="form-control"
                                    placeholder="Enter country"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Location</label>
                        <form:input path="location"
                                    cssClass="form-control"
                                    placeholder="Enter location"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Contact Name</label>
                        <form:input path="contactName"
                                    cssClass="form-control"
                                    placeholder="Enter contact person name"/>
                    </div>

                   <div class="mb-3">
                       <label class="form-label fw-semibold">Contact Number</label>
                       <form:input path="contactNumber"
                                   cssClass="form-control"
                                   placeholder="Enter 10-digit contact number"
                                   required="true"
                                   pattern="[0-9]{10}"
                                   maxlength="10"
                                   title="Please enter a valid 10-digit phone number"/>
                   </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Warehouse
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center small">
                POS Management System
            </div>

            <div class="back text-center pb-3">
                <a href="${pageContext.request.contextPath}/warehouse/list">
                    ← Back to List
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>