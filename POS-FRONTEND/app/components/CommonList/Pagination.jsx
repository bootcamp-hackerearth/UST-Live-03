import PropTypes from "prop-types";

const Pagination = ({
  page,
  setPage,
  totalPages
}) => {

  return (
    <div className="pagination">

      <button
        className="pageBtn"
        disabled={page === 0}
        onClick={() => setPage(page - 1)}
      >
        ←
      </button>

      {Array.from({ length: totalPages }, (_, index) => {
        const pageNumber = index + 1;

        return (
          <button
            key={pageNumber}
            className={
              page === index
                ? "pageBtn activePage"
                : "pageBtn"
            }
            onClick={() => setPage(index)}
          >
            {pageNumber}
          </button>
        );
      })}


      <button
        className="pageBtn"
        disabled={page === totalPages - 1}
        onClick={() => setPage(page + 1)}
      >
        →
      </button>

    </div>
  );
};

Pagination.propTypes = {
  page: PropTypes.number.isRequired,
  setPage: PropTypes.func.isRequired,
  totalPages: PropTypes.number.isRequired,
};

export default Pagination;