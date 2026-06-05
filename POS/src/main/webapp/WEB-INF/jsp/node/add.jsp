<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-primary {
            background-color: #0d6efd;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5">

            <div class="card shadow-sm">

                <div class="card-header text-center py-3">
                    <h5 class="mb-0">Add Node</h5>
                </div>

                <div class="card-body p-4">

                    <form:form
                            action="/node/add"
                            method="post"
                            modelAttribute="nodeDto">

                        <div class="mb-3">
                            <label class="form-label">Node Name</label>
                            <form:input
                                    path="identifier"
                                    cssClass="form-control"
                                    required="true"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Node Path</label>
                            <form:input
                                    path="path"
                                    cssClass="form-control"
                                    required="true"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Additional Roles</label>
                            <form:select
                                    path="roles"
                                    multiple="true"
                                    cssClass="form-control">
                                <form:options
                                        items="${roles}"
                                        itemValue="identifier"
                                        itemLabel="identifier"/>
                            </form:select>
                            <div class="form-text">
                                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                            </div>
                        </div>

                        <div class="d-grid mt-4">
                            <button type="submit" class="btn btn-primary">
                                Add Node
                            </button>
                        </div>

                    </form:form>

                </div>

                <div class="card-footer text-muted text-center small">
                    POS Management System
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>