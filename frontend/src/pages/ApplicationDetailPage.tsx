import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getApplication, updateApplicationStatus, deleteApplication } from '../api/applications'
import { getStagesByApplication, createStage, updateStageResult, deleteStage } from '../api/stages'
import { getMemosByApplication, createMemo, updateMemo, deleteMemo } from '../api/memos'
import StatusBadge from '../components/StatusBadge'
import {
  STATUS_LABELS, STAGE_TYPE_LABELS, STAGE_RESULT_LABELS, STAGE_RESULT_COLORS,
  type ApplicationStatus, type StageType, type StageResult,
} from '../types'

const ALL_STATUSES = Object.keys(STATUS_LABELS) as ApplicationStatus[]
const ALL_STAGE_TYPES = Object.keys(STAGE_TYPE_LABELS) as StageType[]
const ALL_RESULTS = Object.keys(STAGE_RESULT_LABELS) as StageResult[]

export default function ApplicationDetailPage() {
  const { id } = useParams<{ id: string }>()
  const appId = Number(id)
  const navigate = useNavigate()
  const qc = useQueryClient()

  // ── データ取得 ────────────────────────────────────────────────────
  const { data: app } = useQuery({ queryKey: ['application', appId], queryFn: () => getApplication(appId) })
  const { data: stages = [] } = useQuery({ queryKey: ['stages', appId], queryFn: () => getStagesByApplication(appId) })
  const { data: memos = [] } = useQuery({ queryKey: ['memos', appId], queryFn: () => getMemosByApplication(appId) })

  // ── ミューテーション ───────────────────────────────────────────────
  const statusMut = useMutation({
    mutationFn: ({ status }: { status: ApplicationStatus }) => updateApplicationStatus(appId, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['application', appId] }),
  })
  const delAppMut = useMutation({
    mutationFn: () => deleteApplication(appId),
    onSuccess: () => navigate('/applications'),
  })
  const createStageMut = useMutation({
    mutationFn: (data: { stageType: StageType; scheduledAt?: string; location?: string }) =>
      createStage(appId, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['stages', appId] })
      setStageForm({ stageType: 'FIRST_INTERVIEW' as StageType, scheduledAt: '', location: '' })
      setShowStageForm(false)
    },
  })
  const resultMut = useMutation({
    mutationFn: ({ stageId, result }: { stageId: number; result: StageResult }) =>
      updateStageResult(stageId, result),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['stages', appId] }),
  })
  const delStageMut = useMutation({
    mutationFn: (stageId: number) => deleteStage(stageId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['stages', appId] }),
  })
  const createMemoMut = useMutation({
    mutationFn: (content: string) => createMemo(appId, content),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['memos', appId] }); setMemoText('') },
  })
  const editMemoMut = useMutation({
    mutationFn: ({ id, content }: { id: number; content: string }) => updateMemo(id, content),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['memos', appId] }); setEditingMemo(null) },
  })
  const delMemoMut = useMutation({
    mutationFn: (memoId: number) => deleteMemo(memoId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['memos', appId] }),
  })

  // ── フォーム状態 ──────────────────────────────────────────────────
  const [showStageForm, setShowStageForm] = useState(false)
  const [stageForm, setStageForm] = useState<{ stageType: StageType; scheduledAt: string; location: string }>({
    stageType: 'FIRST_INTERVIEW', scheduledAt: '', location: '',
  })
  const [memoText, setMemoText] = useState('')
  const [editingMemo, setEditingMemo] = useState<{ id: number; content: string } | null>(null)

  if (!app) return <div className="p-6 text-gray-400">読み込み中...</div>

  return (
    <div className="p-6 space-y-6 max-w-3xl">
      {/* ヘッダー */}
      <div className="flex items-start justify-between">
        <div>
          <button onClick={() => navigate('/applications')} className="text-xs text-gray-400 hover:text-gray-600 mb-1">
            ← 応募一覧に戻る
          </button>
          <h1 className="text-2xl font-bold text-gray-800">{app.companyName}</h1>
          {app.jobTitle && <p className="text-sm text-gray-500 mt-0.5">{app.jobTitle}</p>}
          <div className="mt-2 flex items-center gap-3">
            <StatusBadge status={app.status} />
            <span className="text-xs text-gray-400">応募日: {app.appliedDate ?? '未設定'}</span>
          </div>
        </div>
        <button
          onClick={() => { if (confirm('この応募を削除しますか？')) delAppMut.mutate() }}
          className="text-xs text-red-400 hover:text-red-600"
        >
          応募を削除
        </button>
      </div>

      {/* ステータス変更 */}
      <section className="rounded-lg border border-gray-200 bg-white p-4">
        <p className="text-sm font-semibold text-gray-700 mb-2">ステータス変更</p>
        <div className="flex flex-wrap gap-2">
          {ALL_STATUSES.map(s => (
            <button
              key={s}
              onClick={() => statusMut.mutate({ status: s })}
              disabled={app.status === s}
              className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
                app.status === s
                  ? 'bg-blue-600 text-white cursor-default'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {STATUS_LABELS[s]}
            </button>
          ))}
        </div>
      </section>

      {/* 選考ステージ */}
      <section>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-base font-semibold text-gray-700">選考ステージ</h2>
          <button onClick={() => setShowStageForm(v => !v)} className="text-xs text-blue-600 hover:underline">
            + ステージ追加
          </button>
        </div>

        {showStageForm && (
          <div className="mb-3 rounded-lg border border-blue-200 bg-blue-50 p-3 space-y-2">
            <div className="grid grid-cols-3 gap-2">
              <select
                value={stageForm.stageType}
                onChange={e => setStageForm(f => ({ ...f, stageType: e.target.value as StageType }))}
                className="rounded border border-gray-300 px-2 py-1.5 text-sm"
              >
                {ALL_STAGE_TYPES.map(t => <option key={t} value={t}>{STAGE_TYPE_LABELS[t]}</option>)}
              </select>
              <input
                type="datetime-local"
                value={stageForm.scheduledAt}
                onChange={e => setStageForm(f => ({ ...f, scheduledAt: e.target.value }))}
                className="rounded border border-gray-300 px-2 py-1.5 text-sm"
              />
              <input
                type="text"
                placeholder="場所 (任意)"
                value={stageForm.location}
                onChange={e => setStageForm(f => ({ ...f, location: e.target.value }))}
                className="rounded border border-gray-300 px-2 py-1.5 text-sm"
              />
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => createStageMut.mutate({
                  stageType: stageForm.stageType,
                  scheduledAt: stageForm.scheduledAt || undefined,
                  location: stageForm.location || undefined,
                })}
                className="rounded bg-blue-600 px-3 py-1 text-xs text-white hover:bg-blue-700"
              >
                追加
              </button>
              <button onClick={() => setShowStageForm(false)} className="text-xs text-gray-500 hover:text-gray-700">
                キャンセル
              </button>
            </div>
          </div>
        )}

        {stages.length === 0 ? (
          <p className="text-sm text-gray-400">選考ステージはまだありません</p>
        ) : (
          <div className="space-y-2">
            {stages.map(stage => (
              <div key={stage.id} className="rounded-lg border border-gray-200 bg-white p-3 flex items-center justify-between">
                <div>
                  <span className="font-medium text-sm text-gray-800">{STAGE_TYPE_LABELS[stage.stageType]}</span>
                  {stage.scheduledAt && (
                    <span className="ml-3 text-xs text-gray-500">
                      {new Date(stage.scheduledAt).toLocaleString('ja-JP')}
                    </span>
                  )}
                  {stage.location && <span className="ml-2 text-xs text-gray-400">📍{stage.location}</span>}
                </div>
                <div className="flex items-center gap-2">
                  <select
                    value={stage.result}
                    onChange={e => resultMut.mutate({ stageId: stage.id, result: e.target.value as StageResult })}
                    className={`rounded-full px-2 py-0.5 text-xs font-medium border-0 ${STAGE_RESULT_COLORS[stage.result]}`}
                  >
                    {ALL_RESULTS.map(r => <option key={r} value={r}>{STAGE_RESULT_LABELS[r]}</option>)}
                  </select>
                  <button
                    onClick={() => { if (confirm('このステージを削除しますか？')) delStageMut.mutate(stage.id) }}
                    className="text-xs text-red-300 hover:text-red-500"
                  >
                    ✕
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* メモ */}
      <section>
        <h2 className="text-base font-semibold text-gray-700 mb-3">メモ</h2>

        {memos.map(memo => (
          <div key={memo.id} className="mb-2 rounded-lg border border-gray-200 bg-white p-3">
            {editingMemo?.id === memo.id ? (
              <div className="space-y-2">
                <textarea
                  value={editingMemo.content}
                  onChange={e => setEditingMemo(m => m ? { ...m, content: e.target.value } : null)}
                  className="w-full rounded border border-gray-300 p-2 text-sm"
                  rows={3}
                />
                <div className="flex gap-2">
                  <button
                    onClick={() => editMemoMut.mutate({ id: memo.id, content: editingMemo.content })}
                    className="rounded bg-blue-600 px-3 py-1 text-xs text-white hover:bg-blue-700"
                  >
                    保存
                  </button>
                  <button onClick={() => setEditingMemo(null)} className="text-xs text-gray-500">キャンセル</button>
                </div>
              </div>
            ) : (
              <div className="flex justify-between items-start">
                <p className="text-sm text-gray-700 whitespace-pre-wrap flex-1">{memo.content}</p>
                <div className="flex gap-2 ml-3 shrink-0">
                  <button onClick={() => setEditingMemo({ id: memo.id, content: memo.content })} className="text-xs text-gray-400 hover:text-gray-600">編集</button>
                  <button onClick={() => delMemoMut.mutate(memo.id)} className="text-xs text-red-300 hover:text-red-500">削除</button>
                </div>
              </div>
            )}
          </div>
        ))}

        <div className="mt-3 space-y-2">
          <textarea
            value={memoText}
            onChange={e => setMemoText(e.target.value)}
            placeholder="メモを入力..."
            rows={3}
            className="w-full rounded-lg border border-gray-300 p-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
          <button
            onClick={() => { if (memoText.trim()) createMemoMut.mutate(memoText.trim()) }}
            disabled={!memoText.trim()}
            className="rounded-lg bg-blue-600 px-4 py-1.5 text-sm text-white disabled:opacity-40 hover:bg-blue-700"
          >
            メモを追加
          </button>
        </div>
      </section>
    </div>
  )
}
