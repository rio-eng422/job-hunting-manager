import client from './client'
import type { Company } from '../types'

type CompanyInput = Pick<Company, 'name' | 'industry' | 'website' | 'notes'>

export const getCompanies  = ()           => client.get<Company[]>('/api/companies').then(r => r.data)
export const searchCompanies = (name: string) => client.get<Company[]>('/api/companies/search', { params: { name } }).then(r => r.data)
export const createCompany = (data: CompanyInput) => client.post<Company>('/api/companies', data).then(r => r.data)
export const updateCompany = (id: number, data: CompanyInput) => client.put<Company>(`/api/companies/${id}`, data).then(r => r.data)
export const deleteCompany = (id: number) => client.delete(`/api/companies/${id}`)
