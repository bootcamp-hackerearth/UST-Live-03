export function showAlert(msg) {
  if (typeof globalThis.alert === 'function') globalThis.alert(msg);
}

export function showConfirm(msg) {
  if (typeof globalThis.confirm === 'function') return globalThis.confirm(msg);
  return false;
}