<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #f4f7f6, #eef2ff);
        }


        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
        }


        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 16px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 20px;
            font-size: 14px;
        }

        .back-btn:hover {
            background: #5a6268;
        }


        .card {
            width: 450px;
            margin: 100px auto;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: var(--primary-color);
            padding: 18px;
            text-align: center;
            color: white;
            font-size: 20px;
            font-weight: 600;
        }

        .card-body {
            padding: 28px 35px;
        }

        label {
            display: block;
            margin-top: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #444;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 11px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary-color);
        }

        .error-msg {
            margin-bottom: 15px;
            padding: 10px;
            text-align: center;
            border-radius: 8px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 14px;
        }


        .btn-submit {
            margin-top: 28px;
            width: 100%;
            padding: 12px;
            background: var(--primary-color);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/product/list" class="back-btn">← Back</a>

<div class="card">

    <div class="card-header">
        Add Product
    </div>

    <div class="card-body">

        <c:if test="${not empty successMessage}">
                   <div class="alert alert-success text-center">
                       ${successMessage}
                   </div>
               </c:if>

               <c:if test="${not empty errorMessage}">
                   <div class="alert alert-danger text-center">
                       ${errorMessage}
                   </div>
               </c:if>

        <form:form action="add" method="post" modelAttribute="products">

            <label>Product Name</label>
            <form:input path="identifier" required="true"/>

            <label>Category</label>
            <form:select path="categories" required="true">
                <form:option value="">-- Select Category --</form:option>
                <c:forEach var="cat" items="${categories}">
                    <form:option value="${cat.identifier}">
                        ${cat.identifier}
                    </form:option>
                </c:forEach>
            </form:select>

            <label>SKU Code</label>
            <form:input path="skucode" type="number" required="true"/>

            <input type="submit" value="Add Product" class="btn-submit"/>

        </form:form>
    </div>
</div>

</body>
</html>