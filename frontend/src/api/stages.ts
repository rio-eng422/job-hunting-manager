import client from './client'
import type { SelectionStage, StageResult, StageType } from '../types'

interface StageInput {
  stageType: StageType
  stageNumber?: number
  scheduledAt?: string
  location?: string
  notes?: string
}

export const getStagesByApplication = (applicationId: number) =>
  client.get<SelectionStage[]>(`/api/applications/${applicationId}/stages`).then(r => r.data)
export const getUpcomingStages      = () =>
  client.get<SelectionStage[]>('/api/stages/upcoming').then(r => r.data)
export const createStage            = (applicationId: number, data: StageInput) =>
  client.post<SelectionStage>(`/api/applications/${applicationId}/stages`, data).then(r => r.data)
export const updateStageResult      = (id: number, result: StageResult) =>
  client.patch<SelectionStage>(`/api/stages/${id}/result`, { result }).then(r => r.data)
export const deleteStage            = (id: number) =>
  client.delete(`/api/stages/${id}`)
