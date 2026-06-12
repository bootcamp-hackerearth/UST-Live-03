import { Poppins, Orbitron } from "next/font/google";

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["400", "600", "700", "800"],
});

const orbitron = Orbitron({
  subsets: ["latin"],
  weight: ["400", "600", "700", "800"],
});

export default function DashboardPage() {

  return (
    <div className={`${orbitron.className} min-h-screen flex items-center justify-center px-6 bg-linear-to-br from-white via-violet-50 to-violet-200`}>
      <div className="text-center max-w-xl mb-30">
        <h1 className="text-5xl md:text-6xl font-extrabold 
              bg-linear-to-r from-violet-600 to-indigo-500 
              bg-clip-text text-transparent mb-6 tracking-tight">
              Welcome
        </h1>
        <p className="text-gray-600 text-lg md:text-xl leading-relaxed mb-6">
          We're glad to have you here. This is your space to explore,
          manage, and grow with ease and clarity.
        </p>
        <div className="w-16 h-1 bg-linear-to-r from-violet-500 to-indigo-400 mx-auto rounded-full mb-6"></div>
      </div>
    </div>
  );
}