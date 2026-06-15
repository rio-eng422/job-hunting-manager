// バックエンドのレスポンス DTO に対応する型定義

export type ApplicationStatus =
  | 'INTERESTED' | 'APPLIED' | 'DOCUMENT_SCREENING'
  | 'FIRST_INTERVIEW' | 'SECOND_INTERVIEW' | 'THIRD_INTERVIEW'
  | 'FINAL_INTERVIEW' | 'OFFER_RECEIVED' | 'ACCEPTED'
  | 'REJECTED' | 'WITHDRAWN'

export const STATUS_LABELS: Record<ApplicationStatus, string> = {
  INTERESTED: '気になる',
  APPLIED: '応募済み',
  DOCUMENT_SCREENING: '書類選考中',
  FIRST_INTERVIEW: '1次面接',
  SECOND_INTERVIEW: '2次面接',
  THIRD_INTERVIEW: '3次面接',
  FINAL_INTERVIEW: '最終面接',
  OFFER_RECEIVED: '内定',
  ACCEPTED: '承諾',
  REJECTED: '不合格',
  WITHDRAWN: '辞退',
}

export const STATUS_COLORS: Record<ApplicationStatus, string> = {
  INTERESTED:          'bg-gray-100 text-gray-600',
  APPLIED:             'bg-blue-100 text-blue-700',
  DOCUMENT_SCREENING:  'bg-yellow-100 text-yellow-700',
  FIRST_INTERVIEW:     'bg-orange-100 text-orange-700',
  SECOND_INTERVIEW:    'bg-orange-100 text-orange-700',
  THIRD_INTERVIEW:     'bg-orange-100 text-orange-700',
  FINAL_INTERVIEW:     'bg-purple-100 text-purple-700',
  OFFER_RECEIVED:      'bg-green-100 text-green-700',
  ACCEPTED:            'bg-green-200 text-green-800',
  REJECTED:            'bg-red-100 text-red-700',
  WITHDRAWN:           'bg-gray-100 text-gray-400',
}

export type StageType =
  | 'DOCUMENT_SCREENING' | 'APTITUDE_TEST' | 'GROUP_DISCUSSION'
  | 'FIRST_INTERVIEW' | 'SECOND_INTERVIEW' | 'THIRD_INTERVIEW'
  | 'FINAL_INTERVIEW' | 'OFFER'

export const STAGE_TYPE_LABELS: Record<StageType, string> = {
  DOCUMENT_SCREENING:  '書類選考',
  APTITUDE_TEST:       '適性検査',
  GROUP_DISCUSSION:    'グループディスカッション',
  FIRST_INTERVIEW:     '1次面接',
  SECOND_INTERVIEW:    '2次面接',
  THIRD_INTERVIEW:     '3次面接',
  FINAL_INTERVIEW:     '最終面接',
  OFFER:               '内定',
}

export type StageResult = 'PENDING' | 'PASSED' | 'FAILED' | 'CANCELLED'

export const STAGE_RESULT_LABELS: Record<StageResult, string> = {
  PENDING:   '結果待ち',
  PASSED:    '通過',
  FAILED:    '不合格',
  CANCELLED: '中止',
}

export const STAGE_RESULT_COLORS: Record<StageResult, string> = {
  PENDING:   'bg-gray-100 text-gray-600',
  PASSED:    'bg-green-100 text-green-700',
  FAILED:    'bg-red-100 text-red-700',
  CANCELLED: 'bg-gray-100 text-gray-400',
}

export interface Company {
  id: number
  name: string
  industry: string | null
  website: string | null
  notes: string | null
  createdAt: string
  updatedAt: string
}

export interface JobApplication {
  id: number
  companyId: number
  companyName: string
  jobTitle: string | null
  status: ApplicationStatus
  appliedDate: string | null
  createdAt: string
  updatedAt: string
}

export interface SelectionStage {
  id: number
  jobApplicationId: number
  companyName: string
  jobTitle: string | null
  stageType: StageType
  stageNumber: number | null
  scheduledAt: string | null
  location: string | null
  result: StageResult
  notes: string | null
  createdAt: string
}

export interface Memo {
  id: number
  jobApplicationId: number
  content: string
  createdAt: string
  updatedAt: string
}

export interface DashboardData {
  countByStatus: Partial<Record<ApplicationStatus, number>>
  totalApplications: number
  thisMonthApplications: number
  upcomingStages: SelectionStage[]
}
