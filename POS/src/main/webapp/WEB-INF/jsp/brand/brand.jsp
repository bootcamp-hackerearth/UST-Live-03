<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Brand</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .content {
            margin: 50px auto;
            padding: 20px;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }

        .header-banner h2 {
            margin: 0;
            font-size: 20px;
            font-weight: 600;
        }

        .header-banner p {
            margin: 4px 0 0;
            font-size: 13px;
            opacity: 0.9;
        }

        .welcome-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        }

        .form-control {
            width: 100%;
        }
    </style>
</head>

<body>
<div class="content">
    <div class="form-wrapper">
        <div class="header-banner">
            <h2>Update Brand</h2>
            <p>Modify existing brand details</p>
        </div>
        <div class="welcome-card">
            <h5 class="mb-4">Brand Information</h5>
            <c:if test="${not empty message}">
                <div class="alert alert-danger">${message}</div>
            </c:if>
            <form:form action="${pageContext.request.contextPath}/brand/update"
                       method="post"
                       modelAttribute="brand">
                <form:hidden path="id"/>
                <div class="form-group">
                    <label>Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>
                <div class="form-group">
                    <label>Brand Name</label>
                    <form:input path="brandName"
                                cssClass="form-control"
                                required="required"/>
                </div>
                <div class="form-group">
                    <label>Description</label>
                    <form:textarea path="description"
                                   cssClass="form-control"
                                   rows="3"
                                   required="required"/>
                </div>
                <div class="form-group">
                    <label>Status</label>
                    <form:select path="status" cssClass="form-control">
                        <form:option value="true">Active</form:option>
                        <form:option value="false">Inactive</form:option>
                    </form:select>
                </div>
                <div class="text-right mt-4">
                    <button type="submit" class="btn btn-primary">
                        Update Brand
                    </button>
                    <a href="${pageContext.request.contextPath}/brand/list"
                       class="btn btn-secondary ml-2">
                        Cancel
                    </a>
                </div>
            </form:form>
        </div>
    </div>
</div>
</body>
</html>