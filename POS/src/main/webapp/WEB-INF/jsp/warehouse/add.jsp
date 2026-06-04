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
                <h4 class="mb-0">Add New Warehouse to Warehouse List</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty warehouse}">
                    <div class="alert alert-success text-center">
                        ${warehouse}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/warehouse/add"
                           modelAttribute="warehouseDto">
                            
                     <div class="mb-3">
                          <label class="form-label fw-semibold">Warehouse Tag</label>
                          <form:input path="identifier"
                                      cssClass="form-control"
                                      placeholder="Enter Warehouse Tag"
                                      required="true"/>
                     </div>

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Warehouse Name</label>
                          <form:input path="name"
                          cssClass="form-control"
                          placeholder="Enter Warehouse Name"
                          required="true"/>
                     </div>

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Address</label>
                          <form:input path="address"
                          cssClass="form-control"
                          placeholder="Enter Address"
                          required="true"/>
                     </div>

                     <div class="mb-3">
                            <label class="form-label fw-semibold">Region</label>
                            <form:input path="region"
                                        cssClass="form-control"
                                        pattern="[a-zA-Z ]{2,30}"
                                        title= "Region name should only contain alphabets"
                                        placeholder="Enter Region"
                                        required="true"/>
                     </div>

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Country</label>
                          <form:input path="country"
                                      cssClass="form-control"
                                      placeholder="Enter Country"
                                      pattern="[a-zA-Z ]{2,30}"
                                      title= "Country name should only contain alphabets"
                                      required="true"/>
                     </div>

                     <div class="mb-3">
                           <label class="form-label fw-semibold">Contact</label>
                           <form:input path="phoneNo"
                                       cssClass="form-control"
                                       pattern="[0-9]{10}"
                                       type="tel"
                                       maxlength="10"
                                       oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                                       title= "Contact no. should only contain numbers"
                                       placeholder="Enter Phone Number"
                                       required="true"/>
                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Warehouse
                        </button>
                    </div>

                    <div class="d-flex justify-content-between">
                         <a href="${pageContext.request.contextPath}/warehouse/list"
                         class="btn btn-outline-secondary">
                              Back to Warehouses List
                         </a>
                    </div>

                <c:if test="${not empty message}">
                        <div class="bottom-error">
                        ${message}
                        </div>
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