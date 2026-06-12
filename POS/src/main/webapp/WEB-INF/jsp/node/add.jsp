<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

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
        .card-header{
        background: #ffffff;
        }
        .form-control {
            border-radius: 8px;
        }
        .btn-update {
                    width: 100%;
                    padding: 12px;
                    background: #4b6cb7;
                    border: none;
                    color: white;
                    font-weight: 600;
                    border-radius: 8px;
                }

                .btn-update:hover {
                    background: #182848;
                }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center text-black">
                <h4 class="mb-0">Add New Node</h4>
            </div>

            <div class="card-body">

                <form:form method="post"
                           action="/node/add"
                           modelAttribute="nodeDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    pattern="^[A-Za-z)-9_-]+$"
                                    placeholder="Enter node name" />
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Path</label>
                        <form:input path="path"
                                    cssClass="form-control"
                                    pattern="^/.*"
                                    placeholder="Eg: /node/list" />
                    </div>

                     <div class="mb-3">
                          <label>Roles</label>

                             <div class="mb-1 text-muted">
                                    Current:
                               <c:forEach var="r" items="${nodeDto.roles}">
                                        <span class="badge bg-secondary me-1">${r}</span>
                                    </c:forEach>
                                </div>

                                <form:select path="roles" multiple="true" cssClass="form-control">
                                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier" />
                                </form:select>


                                <small>
                                    Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
                                </small>
                            </div>

                    <div class="d-grid">
                        <button type="submit" class="btn-update">
                            Add Node
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
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

</body>
</html>