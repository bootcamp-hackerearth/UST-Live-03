<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Model</title>

    <!-- Bootstrap -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">



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

        .form-check-input {
            width: 2.5em;
            height: 1.25em;
            cursor: pointer;
        }

        .form-check-input:checked {
            background-color: #198754;
            border-color: #198754;
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
    </style>
</head>

<body>

<div class="container mt-5">

    <!-- BACK BUTTON -->
    <div class="mb-4 text-center">
        <a href="${pageContext.request.contextPath}/model/list"
           class="btn btn-secondary back-btn">
            <i class="bi bi-arrow-left-circle"></i>
            Back to Model List
        </a>
    </div>

    <div class="card card-custom p-4">
        <h3 class="text-center mb-4">Edit Model</h3>

        <!-- ERROR MESSAGE -->
        <c:if test="${not empty message}">
            <div class="error-msg">
                ${message}
            </div>
        </c:if>

        <!-- ✅ EDIT FORM -->
        <form:form action="${pageContext.request.contextPath}/model/update"
                   method="post"
                   modelAttribute="models">

            <!-- ID -->
            <form:hidden path="id"/>

            <!-- IDENTIFIER -->
            <div class="mb-3">
                <label class="form-label fw-bold">Model Name</label>
                <form:input path="identifier"
                            class="form-control"
                            readonly="true"/>
            </div>

            <!-- ✅ STATUS TOGGLE -->
            <div class="mb-4 d-flex justify-content-between align-items-center">

                <div>
                    <label class="form-label fw-bold mb-0">Status</label>
                    <div class="${models.status ? 'text-success' : 'text-danger'} fw-semibold">
                        ${models.status ? 'Active' : 'Inactive'}
                    </div>
                </div>

                <!-- Toggle Form -->
                <form method="get"
                      action="${pageContext.request.contextPath}/model/toggle"
                      class="d-inline">

                    <input type="hidden" name="identifier" value="${models.identifier}">
                    <input type="hidden" name="status" value="${!models.status}">

                    <div class="form-check form-switch">
                        <input class="form-check-input"
                               type="checkbox"
                               ${models.status ? "checked" : ""}
                               onchange="this.form.submit()">
                    </div>
                </form>
            </div>

            <!-- SUBMIT -->
            <button type="submit" class="btn btn-success w-100 btn-submit">
                <i class="bi bi-check-circle"></i>
                Update Model
            </button>

        </form:form>

    </div>
</div>

</body>
</html>