<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>

    <!-- Bootstrap CSS -->
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
        Add Category
    </div>

    <div class="card-body">

        <!--  FORM -->
        <form:form action="${pageContext.request.contextPath}/category/add"
                   method="post"
                   modelAttribute="categoryDto">

            <!-- Identifier -->
            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            required="true"/>
            </div>

            <!--  Super Category (DROPDOWN) -->
            <div class="mb-3">
                <label class="form-label">Super Category</label>

                <select name="supercategory" class="form-select">
                    <!-- Parent category option -->
                    <option value="">
                        -- Parent Category (No Super Category) --
                    </option>

                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.identifier}">
                            ${cat.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!--  Buttons -->
            <div class="d-flex justify-content-end gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Save
                </button>

                <a href="${pageContext.request.contextPath}/category/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>