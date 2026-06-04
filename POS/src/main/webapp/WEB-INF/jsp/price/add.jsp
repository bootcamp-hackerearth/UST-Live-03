<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
        }

        .card-header {
            background-color: #000;
            color: white;
            font-weight: 600;
            text-align: center;
            padding: 14px;
        }

        .form-label {
            font-weight: 500;
        }

        .readonly-field {
            background-color: #e9ecef;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow">
                <div class="card-header">
                    Add Price
                </div>

                <div class="card-body">

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            ${message}
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="${pageContext.request.contextPath}/price/add"
                               modelAttribute="price">

                        <div class="mb-3">
                            <label class="form-label">Identifier</label>
                            <form:input path="identifier"
                                        id="identifier"
                                        cssClass="form-control readonly-field"
                                        readonly="true"
                                        placeholder="Auto-generated" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product</label>
                            <form:select path="product" cssClass="form-select" id="product">
                                <form:option value="">-- Select Product --</form:option>
                                <form:options items="${products}"
                                              itemValue="identifier"
                                              itemLabel="identifier" />
                            </form:select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Price Amount</label>
                            <form:input path="priceAmount"
                                        cssClass="form-control"
                                        type="number"
                                        step="0.01" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Price Type</label>
                            <form:select path="type" cssClass="form-select" id="type">
                                <form:option value="">-- Select --</form:option>
                                <form:option value="MRP">MRP</form:option>
                                <form:option value="SELLING">Selling</form:option>
                            </form:select>
                        </div>

                        <button type="submit" class="btn btn-success w-100">
                            Save Price
                        </button>

                        <a href="${pageContext.request.contextPath}/price/list"
                           class="btn btn-secondary w-100 mt-2">
                            Cancel
                        </a>

                    </form:form>

                </div>
            </div>

        </div>
    </div>
</div>

<script>
    function generateIdentifier() {
        const product = document.getElementById("product").value;
        const type = document.getElementById("type").value;

        if (product && type) {
            document.getElementById("identifier").value = product + "-" + type;
        } else {
            document.getElementById("identifier").value = "";
        }
    }

    document.getElementById("product").addEventListener("change", generateIdentifier);
    document.getElementById("type").addEventListener("change", generateIdentifier);
</script>

</body>
</html>