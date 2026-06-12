function Footer() {
  return (
    <footer className="border-t border-slate-200/80 bg-white/90 px-6 py-4 text-sm font-medium text-slate-500">
      <div className="flex items-center justify-between">
        <span>{new Date().getFullYear()} POS System</span>
      </div>
    </footer>
  );
}

export default Footer;
