<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Racks</title>

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
                    <h4 class="mb-0">Add New Racks</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty racksDto}">
                        <div class="alert alert-success text-center">
                            No Racks Found
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="/racks/add"
                               modelAttribute="racksDto">

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Racks Name</label>
                            <form:input path="identifier"
                                        cssClass="form-control"
                                        type="text"
                                        placeholder="Enter Racks Name"
                                        required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Shelves</label>
                            <form:select path="shelves"
                                         multiple="true"
                                         required="true"
                                         cssClass="form-control">
                                <form:options items="${shelves}"
                                              itemValue="identifier"
                                              itemLabel="identifier"/>
                            </form:select>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">
                                Add Racks
                            </button>
                        </div>
                    </form:form>
                </div>

                <div class="card-footer text-center text-muted small">
                    POS Management System
                </div>

                <div class="text-center mt-3">
                    <a href="/racks/list">← Back to Racks List</a>
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