<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }

        .page-container {
            max-width: 700px;
            margin: 50px auto;
        }

        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 25px;
        }

        .card-header h3 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
        }

        .card-body {
            padding: 30px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control {
            border-radius: 8px;
        }

        .form-control:focus {
            box-shadow: none;
            border-color: #0d6efd;
        }

        .success-box {
            background-color: #d1e7dd;
            color: #0f5132;
            border: 1px solid #badbcc;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        .error-box {
            background-color: #f8d7da;
            color: #842029;
            border: 1px solid #f5c2c7;
            padding: 12px;
            border-radius: 8px;
            margin-top: 20px;
            text-align: center;
        }

        .btn {
            min-width: 120px;
        }
    </style>
</head>

<body>

<div class="container-fluid">

    <div class="page-container">

        <div class="card">

            <div class="card-header">
                <h3>Add New Role</h3>
            </div>

            <div class="card-body">

                <c:if test="${not empty role}">
                    <div class="success-box">
                        ${role}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/role/add"
                           modelAttribute="roleDto">

                    <div class="mb-4">
                        <label class="form-label">
                            Role Name
                        </label>

                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter role name"
                                    required="required"/>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">
                            Role Description
                        </label>

                        <form:input path="description"
                                    cssClass="form-control"
                                    placeholder="Enter description"
                                    required="required"/>
                    </div>

                    <div class="d-flex justify-content-between mt-4">

                        <a href="/role/list"
                           class="btn btn-secondary">
                            Cancel
                        </a>

                        <button type="submit"
                                class="btn btn-primary">
                            Add Role
                        </button>

                    </div>

                    <c:if test="${not empty message}">
                        <div class="error-box">
                            ${message}
                        </div>
                    </c:if>

                </form:form>

            </div>

        </div>

    </div>

</div>

</body>
</html>