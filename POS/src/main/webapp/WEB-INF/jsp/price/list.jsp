<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Price List</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.container {
    width: 900px;
    padding: 30px;
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.06);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 12px 36px rgba(0, 0, 0, 0.45);
}

h2 {
    text-align: center;
    color: #ffffff;
    margin-bottom: 25px;
}

table {
    width: 100%;
    border-collapse: collapse;
    color: #fff;
}

thead {
    background: rgba(0, 255, 255, 0.2);
}

th, td {
    padding: 12px;
    text-align: center;
}

tbody tr:hover {
    background: rgba(0, 255, 255, 0.1);
}

.btn-update {
    background: #00ffff;
    color: #000;
    border: none;
    padding: 6px 14px;
    border-radius: 8px;
    font-weight: 600;
    margin-right: 6px;
    cursor: pointer;
}

.btn-delete {
    background: #ff4d4d;
    color: #fff;
    border: none;
    padding: 6px 14px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
}

.btn-update:hover,
.btn-delete:hover {
    transform: scale(1.05);
}

.footer-actions {
    margin-top: 30px;
    display: flex;
    justify-content: center;
    gap: 20px;
}

.btn-home {
    background: #6c757d;
    color: #ffffff;
    padding: 10px 24px;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 600;
}

.btn-add {
    background: #00ffff;
    color: #000;
    padding: 10px 28px;
    border-radius: 10px;
    text-decoration: none;
    font-weight: 700;
}

.btn-home:hover,
.btn-add:hover {
    transform: scale(1.05);
    box-shadow: 0 0 15px #00ffff;
}
</style>
</head>

<body>

<div class="container">

    <h2>Price List</h2>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Cost Price</th>
            <th>Selling Price</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:choose>
            <c:when test="${not empty prices}">
                <c:forEach var="price" items="${prices}">
                    <tr>
                        <td>${price.id}</td>
                        <td>${price.identifier}</td>
                        <td>${price.costPrice}</td>
                        <td>${price.sellingPrice}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}">
                                <button type="button" class="btn-update">Update</button>
                            </a>

                            <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}">
                                <button type="button" class="btn-delete">Delete</button>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>

            <c:otherwise>
                <tr>
                    <td colspan="5">No price data available</td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

    <div class="footer-actions">
        <a href="${pageContext.request.contextPath}/" class="btn-home">Home</a>
        <a href="${pageContext.request.contextPath}/price/add" class="btn-add">
            + Add Price
        </a>
    </div>

</div>

</body>
</html>