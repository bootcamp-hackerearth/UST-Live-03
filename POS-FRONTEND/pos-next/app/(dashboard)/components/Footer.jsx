export default function Footer() {
  return (
    <footer
      className="border-t border-slate-200 bg-white px-8 py-4 flex items-center justify-between text-sm text-slate-500"
    >
      <span>
        © {new Date().getFullYear()} UST GLOBAL
      </span>
      <span>
        Pos Application
      </span>
    </footer>
  );
}