import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getApplications, deleteApplication } from '../api/applications'
import { getCompanies } from '../api/companies'
import { createApplication } from '../api/applications'
import StatusBadge from '../components/StatusBadge'
import type { ApplicationStatus } from '../types'
import { STATUS_LABELS } from '../types'

const FILTERS: { label: string; value: ApplicationStatus | undefined }[] = [
  { label: 'すべて',     value: undefined },
  { label: '書類選考',   value: 'DOCUMENT_SCREENING' },
  { label: '面接中',     value: 'FIRST_INTERVIEW' },
  { label: '内定',       value: 'OFFER_RECEIVED' },
  { label: '不合格',     value: 'REJECTED' },
]

export default function ApplicationsPage() {
  const [filter, setFilter]       = useState<ApplicationStatus | undefined>(undefined)
  const [showForm, setShowForm]   = useState(false)
  const [companyId, setCompanyId] = useState('')
  const [jobTitle, setJobTitle]   = useState('')
  const [appliedDate, setAppliedDate] = useState('')
  const qc = useQueryClient()

  const { data: apps = [], isLoading } = useQuery({
    queryKey: ['applications', filter],
    queryFn: () => getApplications(filter),
  })
  const { data: companies = [] } = useQuery({
    queryKey: ['companies'],
    queryFn: getCompanies,
  })

  const createMut = useMutation({
    mutationFn: createApplication,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['applications'] })
      setShowForm(false); setCompanyId(''); setJobTitle(''); setAppliedDate('')
    },
  })
  const deleteMut = useMutation({
    mutationFn: deleteApplication,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['applications'] }),
  })

  const handleCreate = () => {
    if (!companyId) return
    createMut.mutate({ companyId: Number(companyId), jobTitle: jobTitle || undefined, appliedDate: appliedDate || undefined })
  }

  return (
    <div className="p-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">応募一覧</h1>
        <button
          onClick={() => setShowForm(v => !v)}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + 新規応募
        </button>
      </div>

      {/* 新規応募フォーム */}
      {showForm && (
        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4 space-y-3">
          <p className="text-sm font-semibold text-blue-800">新規応募登録</p>
          <div className="grid grid-cols-3 gap-3">
            <select
              value={companyId}
              onChange={e => setCompanyId(e.target.value)}
              className="rounded border border-gray-300 px-3 py-1.5 text-sm"
            >
              <option value="">企業を選択 *</option>
              {companies.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
            <input
              type="text"
              placeholder="職種名"
              value={jobTitle}
              onChange={e => setJobTitle(e.target.value)}
              className="rounded border border-gray-300 px-3 py-1.5 text-sm"
            />
            <input
              type="date"
              value={appliedDate}
              onChange={e => setAppliedDate(e.target.value)}
              className="rounded border border-gray-300 px-3 py-1.5 text-sm"
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleCreate}
              disabled={!companyId || createMut.isPending}
              className="rounded bg-blue-600 px-4 py-1.5 text-sm text-white disabled:opacity-50 hover:bg-blue-700"
            >
              登録
            </button>
            <button onClick={() => setShowForm(false)} className="rounded px-4 py-1.5 text-sm text-gray-600 hover:bg-gray-200">
              キャンセル
            </button>
          </div>
        </div>
      )}

      {/* ステータスフィルタ */}
      <div className="flex flex-wrap gap-2">
        {FILTERS.map(f => (
          <button
            key={f.label}
            onClick={() => setFilter(f.value)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              filter === f.value ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {f.label}
          </button>
        ))}
      </div>

      {/* 一覧テーブル */}
      {isLoading ? (
        <div className="py-10 text-center text-gray-400 text-sm">読み込み中...</div>
      ) : apps.length === 0 ? (
        <div className="py-10 text-center text-gray-400 text-sm">応募データがありません</div>
      ) : (
        <div className="rounded-lg border border-gray-200 overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="px-4 py-3 text-left">企業名</th>
                <th className="px-4 py-3 text-left">職種</th>
                <th className="px-4 py-3 text-left">ステータス</th>
                <th className="px-4 py-3 text-left">応募日</th>
                <th className="px-4 py-3 text-left"></th>
              </tr>
            </thead>
            <tbody>
              {apps.map(app => (
                <tr key={app.id} className="border-t border-gray-100 hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium">
                    <Link to={`/applications/${app.id}`} className="text-blue-600 hover:underline">
                      {app.companyName}
                    </Link>
                  </td>
                  <td className="px-4 py-3 text-gray-600">{app.jobTitle ?? '—'}</td>
                  <td className="px-4 py-3"><StatusBadge status={app.status} /></td>
                  <td className="px-4 py-3 text-gray-600">{app.appliedDate ?? '—'}</td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => {
                        if (confirm(`「${app.companyName}」への応募を削除しますか？`))
                          deleteMut.mutate(app.id)
                      }}
                      className="text-xs text-red-400 hover:text-red-600"
                    >
                      削除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
