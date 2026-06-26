import PropTypes from "prop-types";
import { Wallet, Smartphone, CreditCard, CheckCircle2 } from "lucide-react";

export default function PaymentModal({
  selectedCart,
  paymentType,
  setPaymentType,
  onCompletePayment,
}) {
  return (
    <div className="space-y-4">

      <div className="rounded-xl border border-violet-200 bg-violet-50 p-3">
        <p className="text-[10px] uppercase tracking-wider text-slate-500">
          Amount Payable
        </p>

        <div className="mt-1 flex items-end justify-between">
          <span className="text-xl font-bold text-violet-700">
            ₹{selectedCart?.totalPrice ?? 0}
          </span>

          {selectedCart?.discount > 0 && (
            <span className="text-[10px] text-slate-400 line-through">
              ₹{selectedCart?.originalPrice}
            </span>
          )}
        </div>
      </div>

      {/* Payment Methods */}
      <div>
        <p className="mb-2 text-[11px] font-semibold text-slate-600 uppercase tracking-wide">
          Payment Method
        </p>

        <div className="grid grid-cols-3 gap-2">

          {/* Cash */}
          <button
            onClick={() => setPaymentType("CASH")}
            className={`
              rounded-xl border p-3 transition-all
              ${
                paymentType === "CASH"
                  ? "border-violet-500 bg-violet-50"
                  : "border-slate-200 hover:border-violet-200"
              }
            `}
          >
            <Wallet
              size={18}
              className={`mx-auto ${
                paymentType === "CASH"
                  ? "text-violet-600"
                  : "text-slate-500"
              }`}
            />

            <p className="mt-1 text-[11px] font-medium">
              Cash
            </p>
          </button>

          {/* UPI */}
          <button
            onClick={() => setPaymentType("UPI")}
            className={`
              rounded-xl border p-3 transition-all
              ${
                paymentType === "UPI"
                  ? "border-violet-500 bg-violet-50"
                  : "border-slate-200 hover:border-violet-200"
              }
            `}
          >
            <Smartphone
              size={18}
              className={`mx-auto ${
                paymentType === "UPI"
                  ? "text-violet-600"
                  : "text-slate-500"
              }`}
            />

            <p className="mt-1 text-[11px] font-medium">
              UPI
            </p>
          </button>

          {/* Card */}
          <button
            onClick={() => setPaymentType("CARD")}
            className={`
              rounded-xl border p-3 transition-all
              ${
                paymentType === "CARD"
                  ? "border-violet-500 bg-violet-50"
                  : "border-slate-200 hover:border-violet-200"
              }
            `}
          >
            <CreditCard
              size={18}
              className={`mx-auto ${
                paymentType === "CARD"
                  ? "text-violet-600"
                  : "text-slate-500"
              }`}
            />

            <p className="mt-1 text-[11px] font-medium">
              Card
            </p>
          </button>

        </div>
      </div>

      {/* Selected Method */}
      {paymentType && (
        <div className="flex items-center gap-2 rounded-lg border border-green-200 bg-green-50 p-2">
          <CheckCircle2
            size={14}
            className="text-green-600"
          />

          <span className="text-[11px] text-green-700 font-medium">
            {paymentType} selected
          </span>
        </div>
      )}

      {/* Proceed */}
      <button
        disabled={!paymentType}
        onClick={onCompletePayment}
        className="
          w-full
          rounded-xl
          bg-violet-600
          py-2.5
          text-[12px]
          font-semibold
          text-white
          transition
          hover:bg-violet-700
          disabled:opacity-50
          disabled:cursor-not-allowed
        "
      >
        Complete Payment
      </button>

    </div>
  );
}

PaymentModal.propTypes = {
  selectedCart: PropTypes.shape({
    totalPrice: PropTypes.number,
    discount: PropTypes.number,
    originalPrice: PropTypes.number,
  }),
  paymentType: PropTypes.string,
  setPaymentType: PropTypes.func.isRequired,
  onCompletePayment: PropTypes.func.isRequired,
}
