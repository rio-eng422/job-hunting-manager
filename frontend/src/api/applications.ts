import client from './client'
import type { ApplicationStatus, JobApplication } from '../types'

interface AppInput {
  companyId: number
  jobTitle?: string
  status?: ApplicationStatus
  appliedDate?: string
}

export const getApplications       = (status?: ApplicationStatus) =>
  client.get<JobApplication[]>('/api/applications', { params: status ? { status } : {} }).then(r => r.data)
export const getApplication        = (id: number) =>
  client.get<JobApplication>(`/api/applications/${id}`).then(r => r.data)
export const createApplication     = (data: AppInput) =>
  client.post<JobApplication>('/api/applications', data).then(r => r.data)
export const updateApplication     = (id: number, data: AppInput) =>
  client.put<JobApplication>(`/api/applications/${id}`, data).then(r => r.data)
export const updateApplicationStatus = (id: number, status: ApplicationStatus) =>
  client.patch<JobApplication>(`/api/applications/${id}/status`, { status }).then(r => r.data)
export const deleteApplication     = (id: number) =>
  client.delete(`/api/applications/${id}`)
