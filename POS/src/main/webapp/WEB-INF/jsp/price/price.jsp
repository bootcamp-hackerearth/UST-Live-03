<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Price</title>

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

        * { box-sizing: border-box; }

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
            width: 100%;
            max-width: 420px;
            background: var(--card-bg);
            border-radius: 12px;
            border-top: 5px solid var(--primary);
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1),
                        0 2px 4px -1px rgba(0,0,0,0.06);
        }

        h4 {
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

        .alert-danger {
            background-color: #fef2f2;
            border: 1px solid #fecaca;
            color: #991b1b;
            border-radius: 6px;
        }

        .message-box {
            background:#f8d7da;
            color:#721c24;
            padding:10px;
            margin:15px;
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

<div class="card">

    <div class="card-body">

        <h4 class="text-center mb-4">Edit Price</h4>

        <c:if test="${empty priceDto}">
            <div class="alert alert-danger text-center">
                Price not found
            </div>
        </c:if>

        <c:if test="${not empty priceDto}">
            <form:form action="/price/update"
                       method="post"
                       modelAttribute="priceDto">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">SumPrice</label>
                    <form:input path="sumPrice"
                                cssClass="form-control"
                                type="number"
                                step="0.01"
                                placeholder="Enter costPrice"
                                required="true"/>
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

                <div class="d-flex justify-content-between">
                    <a href="/price/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>

        <div class="text-center mt-4 back-link">
            <a href="/price/list">← Back to price List</a>
        </div>

    </div>

    <c:if test="${not empty message}">
        <div class="message-box">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>
