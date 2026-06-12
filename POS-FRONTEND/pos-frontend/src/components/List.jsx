import React, { useEffect, useState } from "react";
import axios from "axios";

const List = ({ keys, routeName }) => {
  const [data, setData] = useState([]);

  useEffect(() => {
    const paginationDto = {
      page: 0,
      sizePerPage: 10,
    };

    const token = localStorage.getItem("token");

    const fetchData = async () => {
      try {
        const response = await axios.post(
          `http://localhost:8080/api/${routeName}/list`,
          paginationDto,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        setData(response.data?.data || response.data || []);
      } catch (err) {
        console.log(err);
      }
    };

    fetchData();
  }, [routeName]);

  if (!data || data.length === 0) {
    return <p>No data available</p>;
  }

  return (
    <div>
      <h1>{routeName} List</h1>

      <table border="1">
        <thead>
          <tr>
            {keys.map((key, index) => (
              <th key={index}>{key}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {data.map((item, index) => (
            <tr key={index}>
              {keys.map((key, index1) => (
                <td key={index1}>{item[key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      <br />

      <a href={`/${routeName}/add`}>
        <button>Add {routeName}</button>
      </a>
    </div>
  );
};

export default List;
