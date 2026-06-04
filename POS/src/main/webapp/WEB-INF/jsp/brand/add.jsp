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
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control {
            border-radius: 8px;
        }

        .bottom-error {
             margin-top: 12px;
             padding: 10px;
             text-align: center;
             border-radius: 6px;
             background: #fee2e2;
             color: #b91c1c;
             font-size: 13px;
             }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Brand to Brand List</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty brand}">
                    <div class="alert alert-success text-center">
                        ${brand}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/brand/add"
                           modelAttribute="brandDto">

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Brand Name</label>
                          <form:input path="identifier"
                                      cssClass="form-control"
                                      placeholder="Enter Brand Name"
                                      required="true"/>
                     </div>

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Description</label>
                          <form:input path="description"
                                      cssClass="form-control"
                                      placeholder="Enter Description"
                                      required="true"/>
                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Brand
                        </button>
                    </div>

                    <div class="d-flex justify-content-between">
                         <a href="${pageContext.request.contextPath}/brand/list"
                         class="btn btn-outline-secondary">
                              Back to Brands List
                         </a>
                    </div>

                <c:if test="${not empty message}">
                        <div class="bottom-error">${message}</div>
                    </c:if>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>