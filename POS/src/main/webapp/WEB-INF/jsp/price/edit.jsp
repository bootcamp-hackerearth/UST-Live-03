<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>


<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fb, #eef2ff);
        }

        .page-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 16px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 20px;
            font-size: 14px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

        .card-custom {
            width: 480px;
            border-radius: 14px;
            border: none;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header-custom {
            background: var(--primary-color);
            color: #ffffff;
            padding: 18px;
            text-align: center;
            font-size: 20px;
            font-weight: 600;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px 12px;
            font-size: 14px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: none;
        }

        .error-msg {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 15px;
            font-size: 14px;
        }

        .btn-submit {
            margin-top: 20px;
            width: 100%;
            padding: 12px;
            background: var(--primary-color);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/price/list" class="back-btn">
    ← Back
</a>

<div class="page-wrapper">

    <div class="card card-custom">

        <div class="card-header-custom">
            <i class="bi bi-pencil-square"></i> Edit Price
        </div>

        <div class="card-body p-4">

            <c:if test="${not empty message}">
                <div class="error-msg">
                    ${message}
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/price/update"
                       method="post"
                       modelAttribute="prices">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label class="form-label">Product</label>
                    <form:select path="identifier"
                                 class="form-select"
                                 required="true">
                        <form:option value="">-- Select Product --</form:option>
                        <c:forEach items="${products}" var="p">
                            <form:option value="${p.identifier}">
                                ${p.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label class="form-label">MRP</label>
                    <form:input path="mrp"
                                type="number"
                                step="0.01"
                                class="form-control"
                                required="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Selling Price</label>
                    <form:input path="sellingPrice"
                                type="number"
                                step="0.01"
                                class="form-control"
                                required="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Effective From</label>
                    <form:input path="effectiveFrom"
                                type="date"
                                class="form-control"
                                required="true"/>
                </div>

                <button type="submit" class="btn-submit">
                    <i class="bi bi-check-circle"></i>
                    Update Price
                </button>

            </form:form>

        </div>
    </div>

</div>

</body>
</html>
