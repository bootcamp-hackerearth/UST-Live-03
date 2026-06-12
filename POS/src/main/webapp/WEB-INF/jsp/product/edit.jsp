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
            max-width: 500px;
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header-custom {
            background: var(--primary-color);
            color: white;
            padding: 18px;
            text-align: center;
        }

        .form-label {
            font-weight: 600;
        }

        select[multiple] {
            height: 100px;
        }

        .btn-submit {
            background: var(--primary-color);
            color: white;
            border-radius: 10px;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }

        .hint {
            font-size: 12px;
            color: gray;
        }

        .error-msg {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            text-align: center;
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="page-wrapper">

    <div class="card card-custom">

        <div class="card-header-custom">
            <h4><i class="bi bi-pencil-square"></i> Edit Product</h4>
        </div>

        <div class="card-body">

            <a href="${pageContext.request.contextPath}/product/list"
               class="btn btn-sm btn-outline-secondary mb-3">
                ← Back
            </a>

            <c:if test="${not empty message}">
                <div class="error-msg">${message}</div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/product/update"
                       method="post"
                       modelAttribute="products">

                <form:hidden path="id"/>


                <div class="mb-3">
                    <label class="form-label">SKU Code</label>
                    <form:input path="identifier" class="form-control" readonly="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Category</label>
                    <form:select path="categories" class="form-select" multiple="true">
                        <c:forEach var="cat" items="${categories}">
                            <form:option value="${cat.identifier}">
                                ${cat.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                    <div class="hint">Hold Ctrl / Cmd to select multiple</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Brand</label>
                    <form:select path="brand" class="form-select" required="true">
                        <form:option value="">-- Select Brand --</form:option>
                        <c:forEach var="bran" items="${brand}">
                            <form:option value="${bran.identifier}">
                                ${bran.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Unit</label>
                    <form:select path="unit" class="form-select" required="true">
                        <form:option value="">-- Select Unit --</form:option>
                        <c:forEach var="uni" items="${unit}">
                            <form:option value="${uni.identifier}">
                                ${uni.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Model</label>
                    <form:select path="model" class="form-select" required="true">
                        <form:option value="">-- Select Model --</form:option>
                        <c:forEach var="mode" items="${model}">
                            <form:option value="${mode.identifier}">
                                ${mode.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <form:input path="name" class="form-control" required="true"/>
                </div>

                <button type="submit" class="btn btn-submit w-100">
                    <i class="bi bi-check-circle"></i> Update Product
                </button>

            </form:form>

        </div>
    </div>

</div>
</body>
</html>
