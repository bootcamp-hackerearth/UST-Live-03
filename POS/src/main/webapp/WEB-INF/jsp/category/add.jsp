<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .card {
            width: 420px;
            margin: 100px auto;
            background: #fff;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 13px;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
        }

        .helper-text {
            font-size: 11px;
            color: #6c757d;
            margin-top: 4px;
        }
    </style>
</head>

<body>


<a href="${pageContext.request.contextPath}/category/list" class="back-btn">
    ← Back
</a>

    <div class="card">
    <h2 class="text-center">Add Category</h2>

        <c:if test="${not empty errorMessage}">
                <div class="error-msg">
                    ${errorMessage}
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success text-center">
                    ${successMessage}
                </div>
            </c:if>


    <form:form action="${pageContext.request.contextPath}/category/add"
               method="post"
               modelAttribute="category">


        <label>Category Name</label>
        <form:input path="identifier" required="true"
                    placeholder="e.g. CLOTH"/>
        <div class="helper-text">
            This will be used as the category identifier
        </div>


        <label>Super Category (Optional)</label>
        <form:select path="superCategory">
            <form:option value="">-- None --</form:option>
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>
        <div class="helper-text">
            Leave empty to create a top-level category
        </div>


        <input type="submit" value="Add Category" class="btn-submit"/>

    </form:form>
</div>

</body>
</html>