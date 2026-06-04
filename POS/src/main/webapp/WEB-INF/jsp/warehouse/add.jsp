<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

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
        background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
        min-height: 100vh;
    }

    .card {
        border-radius: 12px;
    }

    .form-control {
        border-radius: 8px;
    }

    .card-header {
        background: #ffffff;
    }

    .error {
        color: red;
        font-size: 0.875rem;
    }
</style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-6">

        <div class="card shadow-lg">

            <div class="card-header text-center">
                <h4 class="mb-0">Add New Warehouse</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty warehouse}">
                    <div class="alert alert-success text-center">
                        ${warehouse}
                    </div>
                </c:if>

                <c:if test="${not empty message}">
                    <div class="alert alert-danger text-center">
                        ${message}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/warehouse/add"
                           modelAttribute="warehouseDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Warehouse Name
                        </label>

                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter Warehouse Name"
                                    required="true"/>

                        <form:errors path="identifier"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Country
                        </label>

                        <form:input path="country"
                                    cssClass="form-control"
                                    placeholder="Enter Country"
                                    required="true"
                                    pattern="^[A-Za-z ]{2,50}$"
                                    title="Country should contain only letters and spaces"/>

                        <form:errors path="country"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Region
                        </label>

                        <form:input path="region"
                                    cssClass="form-control"
                                    placeholder="Enter Region"
                                    required="true"
                                    pattern="^[A-Za-z ]{2,50}$"
                                    title="Region should contain only letters and spaces"/>

                        <form:errors path="region"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Contact Name
                        </label>

                        <form:input path="contactName"
                                    cssClass="form-control"
                                    placeholder="Enter Contact Name"
                                    required="true"
                                    pattern="^[A-Za-z ]{3,50}$"
                                    title="Contact name should contain only letters and spaces"/>

                        <form:errors path="contactName"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Contact Number
                        </label>

                        <form:input path="contactNumber"
                                    cssClass="form-control"
                                    type="tel"
                                    placeholder="Enter Contact Number"
                                    required="true"
                                    pattern="[0-9]{10}"
                                    title="Enter a valid 10-digit mobile number"/>

                        <form:errors path="contactNumber"
                                     cssClass="error"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Location
                        </label>

                        <form:input path="location"
                                    cssClass="form-control"
                                    placeholder="Enter Location"
                                    required="true"
                                    pattern="^[A-Za-z0-9 ,./()-]{5,100}$"
                                    title="Location can contain letters, numbers, spaces and address symbols"/>

                        <form:errors path="location"
                                     cssClass="error"/>
                    </div>

                    <div class="d-grid">
                        <button type="submit"
                                class="btn btn-primary btn-lg">
                            Add Warehouse
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>

        </div>

        <div class="text-center mt-3">
            <a href="/warehouse/list">
                ← Back to Warehouse List
            </a>
        </div>

    </div>
</div>

</body>
</html>