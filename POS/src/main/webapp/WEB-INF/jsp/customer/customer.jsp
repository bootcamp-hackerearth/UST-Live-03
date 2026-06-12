<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #f7f7fb;
        }
        .card {
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.08);
        }
        .section-title {
            margin-top: 30px;
            margin-bottom: 15px;
            color: #dc2626;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <div class="card">

        <h4 class="text-primary fw-bold mb-3">Update Customer</h4>

        <c:if test="${not empty message}">
            <div class="alert alert-danger">${message}</div>
        </c:if>

        <form:form method="post"
                   action="/customer/update"
                   modelAttribute="customer">

            <form:hidden path="identifier"/>

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Customer Name</label>
                    <form:input path="name" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                       <label class="form-label fw-semibold">
                           Phone Number <span style="color:red">*</span>
                       </label>
                       <form:input path="phoneNo"
                                   cssClass="form-control"
                                   placeholder="Enter 10-digit number"
                                   required="required"
                                   readonly="true"
                                   inputmode="numeric"
                                   pattern="^[6-9][0-9]{9}$"
                                   maxlength="10"
                                   title="Phone number cannot be edited"/>
                   </div>
            </div>

                 <div class="col-md-6">
                           <label class="form-label fw-semibold">Email</label>
                        <form:input path="email"
                                    cssClass="form-control"
                                    type="email"
                                    required="true"
                                    pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
                                    title="Enter a valid email address"
                                    placeholder="Enter Email"/>

                <div class="col-md-6">
                    <label class="form-label">Party Type</label>
                    <form:select path="partyType" cssClass="form-control">
                        <form:option value="" label="-- Select Party Type --"/>
                        <form:option value="Customer" label="Customer"/>
                        <form:option value="Dealer" label="Dealer"/>
                         <form:option value="Whole Saler" label="Whole saler"/>
                    </form:select>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Balance</label>
                    <form:input path="balance" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Balance Type</label>
                    <form:select path="balanceType" cssClass="form-control">
                        <form:option value="" label="-- Select Balance Type --"/>
                        <form:option value="Due" label="Due"/>
                        <form:option value="Advance" label="Advance"/>
                    </form:select>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Credit Limit</label>
                    <form:input path="creditLimit" cssClass="form-control"/>
                </div>
            </div>
      <div class="col-md-6">
                    <label class="form-label fw-semibold">Status</label>
                  <form:select path="status" cssClass="form-control">
                      <form:option value="true">Active</form:option>
                      <form:option value="false">Inactive</form:option>
                  </form:select>
                  </div>

            <div class="section-title">Billing Address</div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Address Line</label>
                    <form:input path="billingAddress.addressLine" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">City</label>
                    <form:input path="billingAddress.city" cssClass="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">State</label>
                    <form:input path="billingAddress.state" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Zip Code</label>
                    <form:input path="billingAddress.zip" cssClass="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Country</label>
                    <form:input path="billingAddress.country" cssClass="form-control"/>
                </div>
            </div>

            <div class="section-title">Shipping Address</div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Address Line</label>
                    <form:input path="shippingAddress.addressLine" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">City</label>
                    <form:input path="shippingAddress.city" cssClass="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">State</label>
                    <form:input path="shippingAddress.state" cssClass="form-control"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Zip Code</label>
                    <form:input path="shippingAddress.zip" cssClass="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-1">
                <div class="col-md-6">
                    <label class="form-label">Country</label>
                    <form:input path="shippingAddress.country" cssClass="form-control"/>
                </div>
            </div>

            <div class="mt-4">
                <button type="submit" class="btn btn-primary">
                    Update Customer
                </button>

                <a href="/customer/list" class="btn btn-secondary ms-2">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>

</div>
</body>
</html>