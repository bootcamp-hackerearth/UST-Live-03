export default function TableSkeleton() {
  return (
    <div className="animate-pulse">
      <div className="bg-white rounded-[30px] border border-gray-200 overflow-hidden">
        <div className="h-16 bg-gray-100 border-b"></div>

        {[
          "skeleton-1",
          "skeleton-2",
          "skeleton-3",
          "skeleton-4",
          "skeleton-5",
        ].map((id) => (
          <div
            key={id}
            className="h-20 border-b bg-white px-6 flex items-center"
          >
            <div className="h-5 w-full bg-gray-100 rounded"></div>
          </div>
        ))}
      </div>
    </div>
  );
}
