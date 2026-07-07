export const MODAL_ERROR_STATUSES = [403, 404, 500];

export function extractErrorInfo(
  error,
  fallbackMessage = "Something went wrong. Please try again.",
) {
  const status = error?.response?.status || null;
  const message = error?.response?.data?.message || fallbackMessage;
  return { status, message };
}

export function isModalErrorStatus(status) {
  return MODAL_ERROR_STATUSES.includes(status);
}
