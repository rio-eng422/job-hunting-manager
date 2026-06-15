import { useQuery } from '@tanstack/react-query'
import { getDashboard } from '../api/dashboard'
import StatusBadge from '../components/StatusBadge'
import { STAGE_TYPE_LABELS, STAGE_RESULT_COLORS, STAGE_RESULT_LABELS, type ApplicationStatus } from '../types'

export default function DashboardPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['dashboard'],
    queryFn: getDashboard,
  })

  if (isLoading) return <Loading />
  if (isError || !data) return <div className="p-6 text-red-500">データの取得に失敗しました</div>

  const activeCount = Object.entries(data.countByStatus)
    .filter(([s]) => !['ACCEPTED', 'REJECTED', 'WITHDRAWN', 'OFFER_RECEIVED'].includes(s))
    .reduce((sum, [, c]) => sum + (c ?? 0), 0)

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">ダッシュボード</h1>

      {/* サマリーカード */}
      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        <SummaryCard label="総応募数"      value={data.totalApplications}              color="blue" />
        <SummaryCard label="今月の応募"    value={data.thisMonthApplications}          color="green" />
        <SummaryCard label="選考中"        value={activeCount}                         color="orange" />
        <SummaryCard label="内定"          value={data.countByStatus['OFFER_RECEIVED'] ?? 0} color="purple" />
      </div>

      {/* 直近の選考 */}
      <section>
        <h2 className="mb-3 text-lg font-semibold text-gray-700">直近の選考予定</h2>
        {data.upcomingStages.length === 0 ? (
          <p className="text-sm text-gray-400">予定されている選考はありません</p>
        ) : (
          <div className="rounded-lg border border-gray-200 overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-xs uppercase tracking-wide">
                <tr>
                  <th className="px-4 py-3 text-left">企業</th>
                  <th className="px-4 py-3 text-left">選考種別</th>
                  <th className="px-4 py-3 text-left">予定日時</th>
                  <th className="px-4 py-3 text-left">結果</th>
                </tr>
              </thead>
              <tbody>
                {data.upcomingStages.map(s => (
                  <tr key={s.id} className="border-t border-gray-100 hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-gray-800">{s.companyName}</td>
                    <td className="px-4 py-3 text-gray-600">{STAGE_TYPE_LABELS[s.stageType]}</td>
                    <td className="px-4 py-3 text-gray-600">
                      {s.scheduledAt ? new Date(s.scheduledAt).toLocaleString('ja-JP') : '—'}
                    </td>
                    <td className="px-4 py-3">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${STAGE_RESULT_COLORS[s.result]}`}>
                        {STAGE_RESULT_LABELS[s.result]}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* ステータス別件数 */}
      <section>
        <h2 className="mb-3 text-lg font-semibold text-gray-700">ステータス別件数</h2>
        <div className="flex flex-wrap gap-3">
          {(Object.entries(data.countByStatus) as [ApplicationStatus, number][]).map(([status, count]) => (
            <div key={status} className="flex items-center gap-2 rounded-lg bg-white border border-gray-200 px-3 py-2">
              <StatusBadge status={status} />
              <span className="text-sm font-semibold text-gray-700">{count}件</span>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

function SummaryCard({ label, value, color }: { label: string; value: number; color: string }) {
  const styles: Record<string, string> = {
    blue:   'border-blue-200   bg-blue-50   text-blue-700',
    green:  'border-green-200  bg-green-50  text-green-700',
    orange: 'border-orange-200 bg-orange-50 text-orange-700',
    purple: 'border-purple-200 bg-purple-50 text-purple-700',
  }
  return (
    <div className={`rounded-xl border p-5 ${styles[color]}`}>
      <p className="text-xs font-medium opacity-70 uppercase tracking-wide">{label}</p>
      <p className="mt-2 text-4xl font-bold">{value}</p>
    </div>
  )
}

function Loading() {
  return (
    <div className="p-6 space-y-4 animate-pulse">
      <div className="h-8 w-48 rounded bg-gray-200" />
      <div className="grid grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => <div key={i} className="h-24 rounded-xl bg-gray-200" />)}
      </div>
    </div>
  )
}
