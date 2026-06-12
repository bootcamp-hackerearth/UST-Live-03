function Footer() {
  return (
    <footer className="border-t bg-white px-6 py-4 text-sm text-slate-500">
      <div className="flex items-center justify-between">
        <span>© {new Date().getFullYear()} POS System</span>
        <span>Pod 1</span>
        <span>Made for your business.</span>
      </div>
    </footer>
  );
}

export default Footer;