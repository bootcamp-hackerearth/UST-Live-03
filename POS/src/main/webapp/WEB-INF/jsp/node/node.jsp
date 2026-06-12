<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background:#ffffff;
            min-height: 100vh;
        }
        .card {
            border-radius: 15px;
        }
        select[multiple] {
            height: 130px;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4>Edit Node</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty node}">
                        <div class="alert alert-danger text-center">
                            Node not found
                        </div>
                    </c:if>

                    <c:if test="${not empty node}">

                        <form:form method="post"
                                   action="/node/update"
                                   modelAttribute="node">

                            <div class="mb-3">
                                <label class="form-label">Node Identifier</label>
                                <form:input path="identifier"
                                            cssClass="form-control"
                                            readonly="true"/>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Node Path</label>
                                <form:input path="path"
                                            cssClass="form-control"
                                            required="true"/>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Roles</label>
                                <form:select path="roles"
                                             multiple="true"
                                             cssClass="form-control">
                                    <form:options items="${roles}"
                                                  itemValue="identifier"
                                                  itemLabel="identifier"/>
                                </form:select>
                            </div>

                            <div class="d-flex justify-content-between">
                                <a href="${pageContext.request.contextPath}/node/list"
                                   class="btn btn-secondary">
                                    Cancel
                                </a>

                                <button type="submit" class="btn btn-success">
                                    Update Node
                                </button>
                            </div>

                        </form:form>

                    </c:if>

                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>