<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Rack Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-rack-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
        }
        .back-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
            margin-right: 15px;
        }
        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
        .badge-shelf {
            margin: 2px;
            font-size: 12px;
        }
        .status-active {
            color: #198754;
            font-weight: 600;
        }

        .status-inactive {
            color: #dc3545;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Rack Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/rack/add"
               class="btn btn-success add-rack-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Rack
            </a>
        </div>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success text-center fw-semibold mb-4">
                ${successMessage}
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger text-center fw-semibold mb-4">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty racks}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Rack Name</th>
                    <th>Shelves</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="rack" items="${racks}">
                    <tr>
                        <td class="text-center">${rack.id}</td>

                        <td class="fw-semibold">
                            ${rack.identifier}
                        </td>
                        <td>
                            <c:if test="${empty rack.shelfs}">
                                <span class="text-muted">No shelves</span>
                            </c:if>

                            <c:forEach var="shelf" items="${rack.shelfs}">
                                <span class="badge bg-info text-dark badge-shelf">
                                    ${shelf}
                                </span>
                            </c:forEach>
                        </td>

                      <td class="text-center">
                          <form method="post"
                                action="${pageContext.request.contextPath}/rack/update"
                                class="d-inline">

                              <input type="hidden" name="id" value="${rack.id}">
                              <input type="hidden" name="identifier" value="${rack.identifier}">
                              <input type="hidden" name="status" value="${!rack.status}">

                              <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                  <input class="form-check-input me-2"
                                         type="checkbox"
                                         ${rack.status ? "checked" : ""}
                                         onchange="this.form.submit()">

                                  <span class="${rack.status ? 'status-active' : 'status-inactive'}">
                                      ${rack.status ? 'Active' : 'Deactive'}
                                  </span>
                              </div>
                          </form>
                      </td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/rack/get?identifier=${rack.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/rack/delete?identifier=${rack.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this rack?');">
                                <i class="bi bi-trash"></i>
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>
</div>

</body>
</html>
