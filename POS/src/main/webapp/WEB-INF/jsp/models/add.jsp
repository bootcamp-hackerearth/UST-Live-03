<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Model</title>

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
            border: 1px solid #e5e7eb;
            background: rgba(255,255,255,0.9);
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            border-radius: 14px 14px 0 0;
        }
        .form-control, .form-select {
            border-radius: 8px;
        }
        .btn-primary {
            background: #3b82f6;
            border: none;
        }
        .btn-primary:hover {
            background: #2563eb;
        }
        .error-message {
            color: #dc2626;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<c:if test="${not empty message}">
    <div class="error-message">${message}</div>
</c:if>
<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-6">
        <div class="card shadow-lg">
            <div class="card-header text-center">
                <h4 class="mb-0">Add Model</h4>
            </div>
            <div class="card-body">
                <form:form method="post"
                           action="${pageContext.request.contextPath}/models/add"
                           modelAttribute="modelsDto">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Model Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter model name"
                                    required="true"/>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Save Model
                        </button>
                    </div>
                </form:form>
            </div>
            <div class="card-footer text-center small">
                POS Management System
            </div>
            <div class="text-center pb-3">
                <a href="${pageContext.request.contextPath}/models/list">
                    ← Back to List
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>