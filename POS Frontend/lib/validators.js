function isValidLocal(local) {
  if (local.length === 0 || local.length > 64) return false;
  const allowed = new Set(
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%&'*+/=?^_`{|}~-",
  );
  let prevDot = false;
  let idx = 0;
  for (const ch of local) {
    if (ch === ".") {
      if (idx === 0 || idx === local.length - 1) return false;
      if (prevDot) return false;
      prevDot = true;
      idx += 1;
      continue;
    }
    prevDot = false;
    if (!allowed.has(ch)) return false;
    idx += 1;
  }
  return true;
}

function isValidDomain(domain) {
  if (domain.length === 0 || domain.length > 255) return false;
  const labels = domain.split(".");
  if (labels.length < 2) return false;
  for (const lab of labels) {
    if (!isValidLabel(lab)) return false;
  }
  const tld = labels.at(-1);
  if (!tld || tld.length < 2) return false;
  return true;
}

function isValidLabel(lab) {
  if (lab.length === 0 || lab.length > 63) return false;
  if (lab.startsWith("-") || lab.endsWith("-")) return false;
  for (const ch of lab) {
    const ok =
      (ch >= "a" && ch <= "z") ||
      (ch >= "A" && ch <= "Z") ||
      (ch >= "0" && ch <= "9") ||
      ch === "-";
    if (!ok) return false;
  }
  return true;
}

export function isValidEmail(email) {
  if (typeof email !== "string") return false;
  const s = email.trim();
  if (s.length === 0 || s.length > 254) return false;
  if (s.includes(" ")) return false;
  const at = s.indexOf("@");
  if (at <= 0 || at !== s.lastIndexOf("@")) return false;
  const local = s.slice(0, at);
  const domain = s.slice(at + 1);
  if (!isValidLocal(local)) return false;
  if (!isValidDomain(domain)) return false;
  return true;
}

export default { isValidEmail };
