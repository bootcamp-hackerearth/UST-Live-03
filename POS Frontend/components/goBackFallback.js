export function safeGoBack(router, fallback = "/home") {
  let cameFromWithinApp = false;
  try {
    cameFromWithinApp = !!document.referrer && new URL(document.referrer).origin === globalThis.location.origin;
  } catch {
    cameFromWithinApp = false;
  }

  if (cameFromWithinApp && globalThis.history.length > 1) {
    router.back();
  } else {
    router.push(fallback);
  }
}
