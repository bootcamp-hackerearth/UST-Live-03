import { forwardRef } from "react";
import PropTypes from "prop-types";

function PrintableReceipt(
  {
    order,
    cartEntries,
    selectedCustomer,
    paymentType,
    getProductName
  },
  ref
) {
  return (
    <div
      ref={ref}
      style={{
        textAlign: "center",
        width: "80mm",
        padding: "10px",
        fontFamily: "monospace",
      }}
    >
      <div
        ref={ref}
        style={{
          text: "center", 
          width: "80mm",
          padding: "10px",
          fontFamily: "monospace",
        }}
      >
        <h2 style={{ textAlign: "center" }}>
          RETAIL STORE
        </h2>

        <hr />

        <p>
          Customer: {selectedCustomer?.name}
        </p>

        <p>
          Payment: {paymentType}
        </p>

        <p>
          Date: {new Date().toLocaleString()}
        </p>

        <hr />

        {cartEntries.map((item) => (
          <div
            key={item.identifier}
            style={{
              display: "flex",
              justifyContent: "space-between",
            }}
          >
            <span>{getProductName(item.product)}</span>
            <span>x{item.quantity}</span>
          </div>
        ))}

        <hr />

        <p>
          Original Price:
          ₹{order?.originalPrice}
        </p>

        <p>
          Discount:
          ₹{order?.discount}
        </p>

        <h3>
          Total:
          ₹{order?.totalPrice}
        </h3>
      </div>
    </div>
  );
}

PrintableReceipt.propTypes = {
  order: PropTypes.object,

  cartEntries: PropTypes.arrayOf(
    PropTypes.object
  ).isRequired,

  selectedCustomer: PropTypes.shape({
    name: PropTypes.string,
  }),

  paymentType: PropTypes.string,

  getProductName: PropTypes.func.isRequired,
};

export default forwardRef(PrintableReceipt);