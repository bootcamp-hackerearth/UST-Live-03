<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Unit</title>

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
    </style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center text-black">
                <h4 class="mb-0">Add New Unit</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty units}">
                    <div class="alert alert-success text-center">
                        ${units}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/unit/add"
                           modelAttribute="unitDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Unit</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    type="text"
                                    placeholder="Enter Unit Name"
                                    required="true"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Unit Description</label>
                        <form:input path="description"
                                     cssClass="form-control"
                                     type="text"
                                     placeholder="Enter Unit Description"/>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Unit
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>

            <div class="text-center mt-3">
                <a href="/unit/list">← Back to Unit List</a>
            </div>
        </div>

        <c:if test="${not empty message}">
            <div style="
                background:#f8d7da;
                color:#721c24;
                padding:10px;
                margin-bottom:15px;
                border-radius:4px;
                text-align:center;">
                ${message}
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
