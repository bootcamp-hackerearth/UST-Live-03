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
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Category to Category List</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty category}">
                    <div class="alert alert-success text-center">
                        ${category}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/category/add"
                           modelAttribute="categoryDto">
                            
                     <div class="mb-3">
                          <label class="form-label fw-semibold">Category Name</label>
                          <form:input path="identifier"
                                      cssClass="form-control"
                                      placeholder="Enter Category Name"
                                      required="true"/>
                     </div>

                     <div class="mb-3">
                          <div class="form-group">
                             <label>Super Category List</label>
                             <form:select path="superCategory"
                                          cssClass="form-control">
                                          <form:option value="" label="-- Select Super Categories--"/>
                                          <form:options items="${categories}"
                                              itemValue="identifier"
                                              itemLabel="identifier"/>
                             </form:select>
                          </div>
                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Category
                        </button>
                    </div>

                    <div class="d-flex justify-content-between">
                         <a href="${pageContext.request.contextPath}/category/list"
                         class="btn btn-outline-secondary">
                              Back to Categories List
                         </a>
                    </div>

                <c:if test="${not empty message}">
                        <p class="error">${message}</p>
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