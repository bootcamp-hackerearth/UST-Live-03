<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Shelves</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
        }

        .card {
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
        }

        .form-control, .form-select {
            border-radius: 8px;
        }

        .btn-primary {
            background: #3b82f6;
        }
    </style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="text-danger text-center mt-3">${message}</div>
</c:if>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header text-center">
                <h4>Update Shelves</h4>
            </div>

            <div class="card-body">
                <form:form method="post"
                           action="${pageContext.request.contextPath}/shelves/update"
                           modelAttribute="shelves">

                    <div class="mb-3">
                        <label class="form-label">Shelf Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    readonly="true"/>
                    </div>

                    <div class="d-grid">
                        <button class="btn btn-primary">Update Shelves</button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>