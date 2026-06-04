<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>


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
            background: linear-gradient(135deg, #f8fafc, #eef2ff);
            font-family: "Segoe UI", sans-serif;
        }

        .page-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .card-custom {
            width: 100%;
            max-width: 480px;
            border-radius: 14px;
            border: none;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header-custom {
            background: var(--primary-color);
            color: #ffffff;
            padding: 20px;
            border-top-left-radius: 14px;
            border-top-right-radius: 14px;
            text-align: center;
        }

        .card-header-custom h4 {
            margin: 0;
            font-weight: 600;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
        }

        .form-control:focus,
        .form-select:focus {
            box-shadow: none;
            border-color: var(--primary-color);
        }

        .btn-submit {
            background: var(--primary-color);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-size: 16px;
            font-weight: 600;
            color: #fff;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }

        .back-link {
            text-decoration: none;
            font-weight: 500;
            font-size: 14px;
            color: var(--primary-color);
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .error-msg {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            border-radius: 8px;
            font-size: 14px;
            text-align: center;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<div class="page-wrapper">

    <div class="card card-custom">


        <div class="card-header-custom">
            <h4><i class="bi bi-pencil-square"></i> Edit Product</h4>
        </div>


        <div class="card-body p-4">

            <div class="mb-3">
                <a href="${pageContext.request.contextPath}/product/list"
                   class="back-link">
                    <i class="bi bi-arrow-left"></i> Back to Product List
                </a>
            </div>

            <c:if test="${not empty message}">
                <div class="error-msg">
                    ${message}
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/product/update"
                       method="post"
                       modelAttribute="products">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <form:input path="identifier"
                                class="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Category</label>
                    <form:select path="categories"
                                 class="form-select"
                                 required="true">
                        <form:option value="">-- Select Category --</form:option>
                        <c:forEach var="cat" items="${categories}">
                            <form:option value="${cat.identifier}">
                                ${cat.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-4">
                    <label class="form-label">SKU Code</label>
                    <form:input path="skucode"
                                type="number"
                                class="form-control"
                                required="true"/>
                </div>

                <button type="submit"
                        class="btn btn-submit w-100">
                    <i class="bi bi-check-circle"></i> Update Product
                </button>

            </form:form>

        </div>
    </div>

</div>

</body>
</html>