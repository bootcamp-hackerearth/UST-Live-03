<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Rack</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #ddd;
            max-width: 500px;
            margin: 60px auto;
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
        }

        input[readonly] {
            background-color: #e9ecef;
            cursor: not-allowed;
        }
    </style>
</head>

<body>

<div class="card shadow">
    <div class="card-header">
        Edit Rack
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/rack/update"
                   method="post"
                   modelAttribute="rack">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Rack Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Select Shelves</label>
                <form:select path="shelfs"
                             cssClass="form-select"
                             multiple="true">

                    <c:forEach var="shelf" items="${shelves}">
                        <form:option value="${shelf.identifier}">
                            ${shelf.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-4">
                <label class="form-label">Status</label>
                <form:select path="status" cssClass="form-select">
                    <form:option value="true">Active</form:option>
                    <form:option value="false">Deactive</form:option>
                </form:select>
            </div>

            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-check-circle"></i> Update Rack
                </button>

                <a href="${pageContext.request.contextPath}/rack/list"
                   class="btn btn-secondary">
                    Back
                </a>
            </div>

        </form:form>
    </div>
</div>

</body>
</html>