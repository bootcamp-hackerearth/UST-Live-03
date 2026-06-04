<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
            max-width: 450px;
            margin: auto;
        }

        .back-btn {
            font-size: 16px;
            padding: 10px 25px;
            border-radius: 30px;
        }

        .btn-submit {
            font-size: 16px;
            padding: 10px;
            border-radius: 8px;
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

        .helper-text {
            font-size: 11px;
            color: #6c757d;
        }
    </style>
</head>

<body>

<div class="container mt-5">


    <div class="mb-4 text-center">
        <a href="${pageContext.request.contextPath}/category/list"
           class="btn btn-secondary back-btn">
            <i class="bi bi-arrow-left-circle"></i>
            Back to Category List
        </a>
    </div>

    <div class="card card-custom p-4">
        <h3 class="text-center mb-4">Edit Category</h3>


        <c:if test="${not empty message}">
            <div class="error-msg">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/category/update"
                   method="post"
                   modelAttribute="category">


            <form:hidden path="id"/>


            <div class="mb-3">
                <label class="form-label fw-bold">Category Name</label>
                <form:input path="identifier"
                            class="form-control"
                            readonly="true"/>
                <div class="helper-text">
                    Category name cannot be changed
                </div>
            </div>


            <div class="mb-4">
                <label class="form-label fw-bold">Super Category</label>
                <form:select path="superCategory" class="form-select">
                    <form:option value="">-- None --</form:option>
                    <c:forEach var="cat" items="${categories}">
                        <c:if test="${cat.identifier ne category.identifier}">
                            <form:option value="${cat.identifier}">
                                ${cat.identifier}
                            </form:option>
                        </c:if>
                    </c:forEach>
                </form:select>
                <div class="helper-text">
                    Only root categories can be selected
                </div>
            </div>


            <button type="submit" class="btn btn-success w-100 btn-submit">
                <i class="bi bi-check-circle"></i>
                Update Category
            </button>

        </form:form>
    </div>

</div>

</body>
</html>
