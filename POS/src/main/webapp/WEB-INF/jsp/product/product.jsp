<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            border-radius: 12px;
            border: 1px solid #ddd;
            max-width: 600px;
            margin: 50px auto;
        }

        .card-header {
            background: #000;
            color: #fff;
            font-weight: 600;
            text-align: center;
            padding: 14px;
            font-size: 18px;
        }

        .form-control, .form-select {
            border-radius: 6px;
        }

        .readonly-field {
            background-color: #e9ecef;
            pointer-events: none;
        }
    </style>
</head>

<body>

<div class="card shadow">

    <div class="card-header">
        <i class="bi bi-pencil-square me-2"></i> Edit Product
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/product/update"
                   modelAttribute="product">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label>Identifier *</label>
                <form:input path="identifier"
                            cssClass="form-control readonly-field"/>
            </div>

            <div class="mb-3">
                <label>Category *</label>
                <form:select path="category" cssClass="form-select">
                    <form:option value="">-- Select Category --</form:option>
                    <c:forEach items="${categories}" var="c">
                        <form:option value="${c.identifier}">
                            ${c.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label>Brand *</label>
                <form:select path="brand" cssClass="form-select">
                    <form:option value="">-- Select Brand --</form:option>
                    <c:forEach items="${brands}" var="b">
                        <form:option value="${b.identifier}">
                            ${b.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label>Model *</label>
                <form:select path="model" cssClass="form-select">
                    <form:option value="">-- Select Model --</form:option>
                    <c:forEach items="${models}" var="m">
                        <form:option value="${m.identifier}">
                            ${m.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label>Unit *</label>
                <form:select path="unit" cssClass="form-select">
                    <form:option value="">-- Select Unit --</form:option>
                    <c:forEach items="${units}" var="u">
                        <form:option value="${u.identifier}">
                            ${u.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label>Quantity *</label>
                <form:input path="quantity"
                            type="number"
                            cssClass="form-control"/>
            </div>

            <div class="mb-3">
                <label>Status *</label><br/>

                <label class="me-3">
                    <form:radiobutton path="status" value="true"/> ACTIVE
                </label>

                <label>
                    <form:radiobutton path="status" value="false"/> INACTIVE
                </label>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-save me-1"></i> Update Product
                </button>

                <a href="${pageContext.request.contextPath}/product/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>