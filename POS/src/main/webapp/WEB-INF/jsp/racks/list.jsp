<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Rack List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
        h4{
            background-color: #ffffff;
        }

        .btn-pos-update {
                        background-color: #fdfafc;
                        border-color: #4b6cb7;
                        color: #000;
                }

                .btn-pos-update:hover {
                    background-color: #3f5fa7;
                    border-color: #3f5fa7;
                    color: #fff;
                }

                .btn-pos-delete {
                    background-color: #f5f7fa;
                    border: 1px solid #dc3545;
                    color: #dc3545;
                }

                .btn-pos-delete:hover {
                    background-color: #dc3545;
                    color: #fff;
                }
                 .switch {
                                                    position: relative;
                                                    display: inline-block;
                                                    width: 60px;
                                                    height: 34px;
                                                }

                                                .switch input {
                                                    opacity: 0;
                                                    width: 0;
                                                    height: 0;
                                                }

                                                .slider {
                                                    position: absolute;
                                                    cursor: pointer;
                                                    top: 0; left: 0; right: 0; bottom: 0;
                                                    background-color: #ccc;
                                                    transition: 0.4s;
                                                    border-radius: 34px;
                                                }

                                                .slider:before {
                                                    position: absolute;
                                                    content: "";
                                                    height: 26px;
                                                    width: 26px;
                                                    left: 4px;
                                                    bottom: 4px;
                                                    background-color: white;
                                                    transition: 0.4s;
                                                    border-radius: 50%;
                                                }

                                                input:checked + .slider {
                                                    background-color: #4CAF50;
                                                }

                                                input:checked + .slider:before {
                                                    transform: translateX(26px);
                                                }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header text-black text-center">
                    <h4 class="mb-0">List of Rack</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty racks}">
                        <div class="alert alert-warning text-center">
                            No Rack found
                        </div>
                    </c:if>

                    <c:if test="${not empty racks}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Rack Name</th>
                                <th>Shelves</th>
                                <th>Status</th>
                                <th>Delete</th>
                                <th>Update</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="rack" items="${racks}">
                                <tr>
                                    <td>
                                        <a href=""
                                           class="text-decoration-none fw-semibold">
                                            ${rack.id}
                                        </a>
                                    </td>
                                    <td>${rack.identifier}</td>
                                    <td>${rack.shelves}</td>
                                    <td>

                                                                        <label class="switch">
                                                                            <input type="checkbox"
                                                                                   onclick="toggle('${rack.identifier}', this)"
                                                                                   ${rack.status ? 'checked' : ''}>
                                                                            <span class="slider"></span>
                                                                        </label>

                                                                        </td>



                                    <td>
                                        <a href="/racks/delete?identifier=${rack.identifier}"
                                           class="btn btn-pos-delete btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this Rack?');">
                                            Delete
                                        </a>
                                    </td>
                                    <td>
                                          <a class="btn btn-pos-update btn-sm"
                                     href="/racks/get?identifier=${rack.identifier}"
                                                                    >
                                                                             Update
                                                                   </a>
                                                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="/racks/add" class="btn btn-success">
                        + Add New Rack
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

<script>
function toggle(identifier, checkbox) {
    fetch('<%= request.getContextPath() %>/racks/toggle?identifier=' + identifier)
        .catch(err => {
            console.error(err);
            checkbox.checked = !checkbox.checked;
        });
}
</script>

</body>
</html>