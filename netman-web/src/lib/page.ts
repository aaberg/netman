export interface Page<T> {
    page: number,
    pageSize: number,
    total: number,
    items: T[]
}