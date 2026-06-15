import client from './client'
import type { Memo } from '../types'

export const getMemosByApplication = (applicationId: number) =>
  client.get<Memo[]>(`/api/applications/${applicationId}/memos`).then(r => r.data)
export const createMemo = (applicationId: number, content: string) =>
  client.post<Memo>(`/api/applications/${applicationId}/memos`, { content }).then(r => r.data)
export const updateMemo = (id: number, content: string) =>
  client.put<Memo>(`/api/memos/${id}`, { content }).then(r => r.data)
export const deleteMemo = (id: number) =>
  client.delete(`/api/memos/${id}`)
