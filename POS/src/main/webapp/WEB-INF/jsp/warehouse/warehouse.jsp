<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse Details</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }
    </style>
</head>

<body>
${message}
<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Warehouse</h4>

        <c:if test="${empty warehouses}">
            <div class="alert alert-danger text-center">
                Warehouse not found
            </div>
        </c:if>

        <c:if test="${not empty warehouses}">
            <form:form action="/warehouse/update"
                       method="post"
                       modelAttribute="warehouse">

                <form:hidden path="id" value="${warehouse.id}"/>

                <div class="mb-4">
                     <label class="form-label">Warehouse Code</label>
                     <form:input path="identifier"
                                 cssClass="form-control"
                                 placeholder="Warehouse Code"
                                 readonly="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Warehouse Name</label>
                     <form:input path="name"
                                 cssClass="form-control"
                                 placeholder="Enter warehouse Name"
                                 required="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Address</label>
                     <form:input path="address"
                                 cssClass="form-control"
                                 placeholder="Enter Address"
                                 required="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Region</label>
                     <form:input path="region"
                                 cssClass="form-control"
                                 pattern="^[a-zA-Z ]{2,30}"
                                 title= "Region name should only contain alphabets"
                                 placeholder="Enter Region"
                                 required="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Country</label>
                     <form:input path="country"
                                 cssClass="form-control"
                                 placeholder="Enter Country"
                                 pattern="^[a-zA-Z ]{2,30}"
                                 title= "Region name should only contain alphabets"
                                 required="true"/>
                </div>

                <div class="mb-4">
                      <label class="form-label">Contact</label>
                      <form:input path="phoneNo"
                                  cssClass="form-control"
                                  type="tel"
                                  maxlength="10"
                                  oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                                  placeholder="Enter Phone No"
                                  pattern="[0-9]{10}"
                                  title="Phone no. should be exactly 10 digits"
                                  required="true"/>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/warehouse/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>
    </div>
</div>
</body>
</html>