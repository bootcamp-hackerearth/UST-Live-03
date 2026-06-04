<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Update Brand</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control,
        .form-select,
        textarea {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }

        textarea {
            resize: none;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">

        <span class="navbar-brand fw-bold">
            Brand Management
        </span>

        <a href="/brand/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>

    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center"
     style="min-height: 100vh;">

    <div class="card shadow p-4"
         style="width: 550px;">

        <h3 class="text-center mb-4 fw-bold">
            Update Brand
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="/brand/update"
              method="post">

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Identifier
                </label>

                <input type="text"
                       name="identifier"
                       class="form-control"
                       value="${brandDto.identifier}"
                       readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Brand Name
                </label>

                <input type="text"
                       name="brandName"
                       class="form-control"
                       value="${brandDto.brandName}"
                       readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Description
                </label>

                <textarea name="description"
                          class="form-control"
                          rows="4"
                          required>${brandDto.description}</textarea>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Status
                </label>

                <select name="status"
                        class="form-select">

                    <option value="true"
                        <c:if test="${brandDto.status}">
                            selected
                        </c:if>>
                        Active
                    </option>

                    <option value="false"
                        <c:if test="${!brandDto.status}">
                            selected
                        </c:if>>
                        Inactive
                    </option>

                </select>
            </div>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary w-100">
                    Update
                </button>

                <a href="/brand/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>

            </div>

        </form>

    </div>

</div>

</body>
</html>