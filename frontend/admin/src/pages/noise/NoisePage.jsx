export default function NoisePage({ title = "준비 중" }) {
  return (
    <div className="rounded-2xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 p-8">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{title}</h1>
      <p className="mt-2 text-gray-500 dark:text-gray-400">
        이 페이지는 담당 팀원이 추후 구현할 예정입니다.
      </p>
    </div>
  );
}
