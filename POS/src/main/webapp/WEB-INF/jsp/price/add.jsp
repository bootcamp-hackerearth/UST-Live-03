<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Price</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        :root {
            --primary: #0f172a;
            --bg-light: #f8fafc;
            --text-main: #0f172a;
            --text-muted: #64748b;
            --border: #e2e8f0;
            --card-bg: #ffffff;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Inter', -apple-system, system-ui, sans-serif;
            background-color: var(--bg-light);
            background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
            background-size: 24px 24px;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px 20px;
        }

        .card {
            background: var(--card-bg);
            border-radius: 12px;
            border-top: 5px solid var(--primary);
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1),
                        0 2px 4px -1px rgba(0,0,0,0.06);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
        }

        .card-header h4 {
            font-size: 24px;
            font-weight: 600;
            color: var(--text-main);
        }

        label {
            font-weight: 600;
            font-size: 14px;
            color: var(--text-main);
        }

        .form-control {
            border-radius: 8px;
            border: 1px solid var(--border);
        }

        .btn-primary {
            background-color: var(--primary);
            border-color: var(--primary);
            font-weight: 600;
        }

        .alert-success {
            background-color: #ecfdf5;
            border: 1px solid #a7f3d0;
            color: #065f46;
            border-radius: 6px;
        }

        .message-box {
            background:#f8d7da;
            color:#721c24;
            padding:10px;
            margin-top:15px;
            border-radius:6px;
            text-align:center;
        }

        .back-link a {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
            font-size: 14px;
        }

        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center">
    <div class="col-md-5">

        <div class="card">

            <div class="card-header text-center">
                <h4 class="mb-0">Add New Price</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty price}">
                    <div class="alert alert-success text-center">
                        ${price}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/price/add"
                           modelAttribute="priceDto">

                    <div class="mb-3">
                        <label class="form-label">Product</label>
                        <form:select path="product" cssClass="form-control" required="true">
                            <form:options items="${products}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                    </div>


                            <div class="mb-3">
                            <label>sum Price *</label>
                            <form:input path="sumPrice"
                                        cssClass="form-control"
                                        type="number"
                                        step="0.01"
                                        min="0"
                                        placeholder="Enter Tot Price"
                                        required="true"/>
                            <form:errors path="sumPrice" cssClass="error-text"/>
                        </div>


                   <div class="mb-3">
                           <label class="form-label fw-semibold">Price Type</label>
                                         <form:select path="priceType"
                                                      cssClass="form-select"
                                                      required="true">
                                             <form:option value="" label="-- Select Price Type --"/>
                                             <form:option value="MRP" label="MRP"/>
                                             <form:option value="Cost Price" label="Cost Price"/>
                                             <form:option value="Selling Price" label="Selling Price"/>
                                         </form:select>
                                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Price
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>

            <div class="text-center mt-3 back-link">
                <a href="/price/list">← Back to Price List</a>
            </div>

        </div>

        <c:if test="${not empty message}">
            <div class="message-box">
                ${message}
            </div>
        </c:if>

    </div>
</div>

</body>
</html>