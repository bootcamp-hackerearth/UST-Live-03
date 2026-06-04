<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            background: #ffffff
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center">Edit Category</h4>

        <c:if test="${empty categoryDto}">
            <div class="alert alert-danger text-center">
                Price not found
            </div>
        </c:if>

        <c:if test="${not empty categoryDto}">
            <form:form action="/category/update"
                       method="post"
                       modelAttribute="categoryDto">

                <form:hidden path="id"/>

                <div class="mb-4">
                    <label class="form-label">Category Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Super Category</label>
                    <form:select path="superCategory" cssClass="form-control">
                        <form:options items="${categorys}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
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

        <div class="text-center mt-3">
            <a href="/price/list">← Back to Price List</a>
        </div>

    </div>

    <c:if test="${not empty message}">
        <div style="
            background:#f8d7da;
            color:#721c24;
            padding:10px;
            margin-bottom:15px;
            border-radius:4px;
            text-align:center;">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>
