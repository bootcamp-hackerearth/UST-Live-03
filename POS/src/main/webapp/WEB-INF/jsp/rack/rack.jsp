<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Rack</title>

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

        .page-wrapper {
            max-width: 600px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 22px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .form-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        }

        label {
            font-weight: 500;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 23px;
            margin-right: 10px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #ccc;
            border-radius: 34px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 17px;
            width: 17px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #007bff;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>
<body>

<div class="content">
    <div class="page-wrapper">
        <div class="header-banner">
            <h4>Update Rack</h4>
            <p>Edit rack details</p>
        </div>
        <div class="form-card">
            <form action="${pageContext.request.contextPath}/rack/update" method="post">
                <div class="form-group">
                    <label>Identifier</label>
                    <input type="text"
                           class="form-control"
                           name="identifier"
                           value="${rack.identifier}"
                           readonly>
                </div>
                <div class="form-group">
                    <label>Rack Name</label>
                    <input type="text"
                           class="form-control"
                           name="name"
                           value="${rack.name}"
                           required>
                </div>
                <div class="form-group">
                    <label for="shelves">Shelves</label>
                    <select class="form-control"
                            id="shelves"
                            name="shelves"
                            multiple
                            size="5"
                            required>
                        <c:forEach items="${shelfs}" var="shelf">
                            <option value="${shelf.name}">
                                ${shelf.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
          <div class="form-group">
              <label>Status</label>
              <select name="status" class="form-control" required>
                  <option value="true"
                      <c:if test="${rack.status}">selected</c:if>>
                      Active
                  </option>
                  <option value="false"
                      <c:if test="${not rack.status}">selected</c:if>>
                      Inactive
                  </option>
              </select>
          </div>
                <c:if test="${not empty message}">
                    <div class="alert alert-danger">
                        ${message}
                    </div>
                </c:if>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/rack/list"
                       class="btn btn-secondary btn-sm">
                        Back
                    </a>
                    <button type="submit"
                            class="btn btn-primary btn-sm">
                        Update Rack
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>