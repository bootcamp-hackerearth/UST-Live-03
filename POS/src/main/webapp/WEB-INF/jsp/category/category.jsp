<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }
    </style>
</head>

<body>
${message}
<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Category</h4>
        <c:if test="${empty categories}">
            <div class="alert alert-danger text-center">
                Category not found
            </div>
        </c:if>

        <c:if test="${not empty categories}">
            <form:form action="/category/update"
                       method="post"
                       modelAttribute="category">

                <form:hidden path="id" value="${category.id}"/>
                <div class="mb-4">
                     <label class="form-label">Category Name</label>
                     <form:input path="identifier"
                                 cssClass="form-control"
                                 placeholder="Enter category Name"
                                 readonly="true"/>
                </div>

                <div class="mb-3">
                    <div class="form-group">
                      <label>Super Category List</label>
                      <form:select path="superCategory"
                                   cssClass="form-control">
                                   <form:option value="" label="-- Select Super Categories--"/>
                                   <form:options items="${categories}"
                                        itemValue="identifier"
                                        itemLabel="identifier"/>
                                   </form:select>
                    </div>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/category/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>
            </form:form>
        </c:if>
    </div>
</div>
</body>
</html>