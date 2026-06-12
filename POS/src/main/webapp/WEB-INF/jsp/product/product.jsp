<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .edit-card {
            width: 460px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 18px;
            box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
            color: #333;
        }

        label {
            font-weight: 600;
            font-size: 0.9rem;
            margin-top: 12px;
            display: block;
            color: #444;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 8px;
            border: 1px solid #ddd;
            font-size: 0.9rem;
        }

        input[readonly] {
            background-color: #f1f3f5;
        }

        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: #ffffff;
            font-weight: 600;
            border-radius: 25px;
            font-size: 1rem;
            cursor: pointer;
        }

        button:hover {
            opacity: 0.95;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            font-weight: 600;
            color: #0d6efd;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="edit-card">

    <h2>Update Product</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-danger text-center">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/product/update" method="post">

        <label>Identifier</label>
        <input type="text"
               name="identifier"
               value="${product.identifier}"
               readonly />

        <label>Product Name</label>
        <input type="text"
               name="productname"
               value="${product.productname}"
               required />

        <label>Category</label>
        <select name="category" required>
            <option value="">-- Select Category --</option>

            <c:forEach var="cat" items="${categories}">
                <option value="${cat.identifier}">
                    ${cat.identifier}
                </option>
            </c:forEach>
        </select>
          <label>Brand</label>
                                <select name="brand" required>
                                    <option value="">-- Select Brand --</option>
                                  <c:forEach var="brand" items="${brands}">
                                 <option value="${brand.identifier}">
                                 ${brand.identifier}
                            </option>
                        </c:forEach>

                      </select>

                           </select>
                            <label>Model</label>
                             <select name="model" required>
                             <option value="">-- Select Model --</option>
                              <c:forEach var="model" items="${models}">
                              <option value="${model.identifier}">
                                ${model.identifier}
                                </option>
                                </c:forEach>
                              </select>

                                  </select>
                                   <label>Unit</label>
                                   <select name="unit" required>
                                    <option value="">-- Select Unit --</option>
                                     <c:forEach var="unit" items="${units}">
                                     <option value="${unit.identifier}">
                                       ${unit.identifier}
                                         </option>
                                         </c:forEach>
                                         </select>

        <button type="submit">Update Product</button>
    </form>

    <a class="back-link"
       href="${pageContext.request.contextPath}/product/list">
        ← Back to Product List
    </a>

</div>

</body>
</html>
