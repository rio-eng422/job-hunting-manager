import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getCompanies, createCompany, updateCompany, deleteCompany } from '../api/companies'
import type { Company } from '../types'

type Form = Pick<Company, 'name' | 'industry' | 'website' | 'notes'>
const emptyForm = (): Form => ({ name: '', industry: null, website: null, notes: null })

export default function CompaniesPage() {
  const qc = useQueryClient()
  const [form, setForm]         = useState<Form>(emptyForm())
  const [editId, setEditId]     = useState<number | null>(null)
  const [showForm, setShowForm] = useState(false)

  const { data: companies = [], isLoading } = useQuery({
    queryKey: ['companies'],
    queryFn: getCompanies,
  })

  const createMut = useMutation({
    mutationFn: createCompany,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['companies'] }); resetForm() },
  })
  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Form }) => updateCompany(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['companies'] }); resetForm() },
  })
  const deleteMut = useMutation({
    mutationFn: deleteCompany,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['companies'] }),
  })

  const resetForm = () => { setForm(emptyForm()); setEditId(null); setShowForm(false) }

  const startEdit = (c: Company) => {
    setForm({ name: c.name, industry: c.industry, website: c.website, notes: c.notes })
    setEditId(c.id)
    setShowForm(true)
  }

  const handleSubmit = () => {
    if (!form.name.trim()) return
    if (editId != null) updateMut.mutate({ id: editId, data: form })
    else createMut.mutate(form)
  }

  const field = (key: keyof Form) => ({
    value: form[key] ?? '',
    onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
      setForm(f => ({ ...f, [key]: e.target.value || null })),
  })

  return (
    <div className="p-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">企業マスタ</h1>
        <button
          onClick={() => { resetForm(); setShowForm(v => !v) }}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          + 企業追加
        </button>
      </div>

      {/* 登録・編集フォーム */}
      {showForm && (
        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4 space-y-3">
          <p className="text-sm font-semibold text-blue-800">{editId ? '企業情報を編集' : '新規企業登録'}</p>
          <div className="grid grid-cols-2 gap-3">
            <input
              type="text" placeholder="企業名 *"
              className="rounded border border-gray-300 px-3 py-1.5 text-sm col-span-2"
              {...field('name')}
            />
            <input
              type="text" placeholder="業種"
              className="rounded border border-gray-300 px-3 py-1.5 text-sm"
              {...field('industry')}
            />
            <input
              type="url" placeholder="WebサイトURL"
              className="rounded border border-gray-300 px-3 py-1.5 text-sm"
              {...field('website')}
            />
            <textarea
              placeholder="メモ"
              rows={2}
              className="rounded border border-gray-300 px-3 py-1.5 text-sm col-span-2 resize-none"
              {...field('notes')}
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleSubmit}
              disabled={!form.name.trim()}
              className="rounded bg-blue-600 px-4 py-1.5 text-sm text-white disabled:opacity-50 hover:bg-blue-700"
            >
              {editId ? '更新' : '登録'}
            </button>
            <button onClick={resetForm} className="rounded px-4 py-1.5 text-sm text-gray-600 hover:bg-gray-200">
              キャンセル
            </button>
          </div>
        </div>
      )}

      {/* 一覧 */}
      {isLoading ? (
        <div className="py-10 text-center text-gray-400 text-sm">読み込み中...</div>
      ) : companies.length === 0 ? (
        <div className="py-10 text-center text-gray-400 text-sm">企業データがありません</div>
      ) : (
        <div className="rounded-lg border border-gray-200 overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="px-4 py-3 text-left">企業名</th>
                <th className="px-4 py-3 text-left">業種</th>
                <th className="px-4 py-3 text-left">Webサイト</th>
                <th className="px-4 py-3 text-left">メモ</th>
                <th className="px-4 py-3 text-left"></th>
              </tr>
            </thead>
            <tbody>
              {companies.map(c => (
                <tr key={c.id} className="border-t border-gray-100 hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-800">{c.name}</td>
                  <td className="px-4 py-3 text-gray-600">{c.industry ?? '—'}</td>
                  <td className="px-4 py-3">
                    {c.website
                      ? <a href={c.website} target="_blank" rel="noreferrer" className="text-blue-600 hover:underline truncate block max-w-48">{c.website}</a>
                      : <span className="text-gray-400">—</span>
                    }
                  </td>
                  <td className="px-4 py-3 text-gray-500 truncate max-w-48">{c.notes ?? '—'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-3">
                      <button onClick={() => startEdit(c)} className="text-xs text-blue-400 hover:text-blue-600">編集</button>
                      <button
                        onClick={() => { if (confirm(`「${c.name}」を削除しますか？`)) deleteMut.mutate(c.id) }}
                        className="text-xs text-red-400 hover:text-red-600"
                      >
                        削除
                      </button>
                    </div>
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
