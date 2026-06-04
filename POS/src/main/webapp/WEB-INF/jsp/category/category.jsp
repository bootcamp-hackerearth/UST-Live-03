<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>

    <!--  Bootstrap CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background-color: #f4f6f9;
        }
        .card {
            max-width: 500px;
            margin: 60px auto;
        }
        .card-header {
            font-weight: 600;
        }
        label {
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="card shadow">
    <div class="card-header bg-primary text-white">
        Edit Category
    </div>

    <div class="card-body">

        <!--  OPENING form:form TAG (THIS WAS MISSING) -->
        <form:form action="${pageContext.request.contextPath}/category/save"
                   method="post"
                   modelAttribute="categoryDto">

            <!--  Hidden ID -->
            <form:hidden path="id"/>

            <!-- Identifier -->
            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true"/>
            </div>

            <!--  Super Category DROPDOWN -->
            <div class="mb-3">
                <label class="form-label">Super Category</label>

                <select name="supercategory" class="form-select">
                    <!-- Parent option -->
                    <option value="">-- Parent Category (No Super Category) --</option>

                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.identifier}"
                            <c:if test="${cat.identifier == categoryDto.supercategory}">
                                selected
                            </c:if>>
                            ${cat.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!--  Buttons -->
            <div class="d-flex justify-content-end gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Update
                </button>

                <a href="${pageContext.request.contextPath}/category/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        <!--  CLOSING form:form TAG -->
        </form:form>

    </div>
</div>

</body>
</html>