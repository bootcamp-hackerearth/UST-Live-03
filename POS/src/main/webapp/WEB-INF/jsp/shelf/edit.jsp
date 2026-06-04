<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Brand</title>


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
    </style>
</head>

<body>

<div class="container mt-5">


    <div class="mb-4 text-center">
        <a href="${pageContext.request.contextPath}/shelf/list"
           class="btn btn-secondary back-btn">
            <i class="bi bi-arrow-left-circle"></i>
            Back to Shelf List
        </a>
    </div>

    <div class="card card-custom p-4">
        <h3 class="text-center mb-4">Edit Shelf</h3>


        <c:if test="${not empty message}">
            <div class="error-msg">
                ${message}
            </div>
        </c:if>


        <form:form action="${pageContext.request.contextPath}/shelf/update"
                   method="post"
                   modelAttribute="shelf">
            <form:hidden path="id"/>


            <div class="mb-3">
                <label class="form-label fw-bold">Shelf Name</label>
                <form:input path="identifier"
                            class="form-control"
                            required="true"  readonly="true"/>
                <form:errors path="identifier" cssClass="text-danger"/>
            </div>



            <div class="mb-4">
                <label class="form-label fw-bold">Status</label>
                <form:select path="status" class="form-select">
                    <form:option value="true">Active</form:option>
                    <form:option value="false">Deactive</form:option>
                </form:select>
            </div>


            <button type="submit" class="btn btn-success w-100 btn-submit">
                <i class="bi bi-check-circle"></i>
                Update Shelf
            </button>

        </form:form>

    </div>
</div>

</body>
</html>