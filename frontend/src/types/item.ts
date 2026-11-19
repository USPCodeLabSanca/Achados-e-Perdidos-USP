export type ItemStatus = "perdido" | "encontrado" | "recuperado"

export type Item = {
  idItem: number
  nome: string
  foto: string | null
  local: string | null
  status: ItemStatus
  descricao: string | null
  tipo: string
  dataOcorrencia: string | null
  criadorNome: string
  criadorEmail: string
  criadorTelefone: string | null
  criadorFoto: string | null
}

export type PageResponse<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
