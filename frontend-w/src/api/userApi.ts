import { api } from './axiosConfig';

export interface User {
  id?: number;
  name: string;
  email: string;
}

export const getAll = () => api.get<User[]>('/users');
export const getById = (id: number) => api.get<User>(`/users/${id}`);
export const create = (data: User) => api.post<User>('/users', data);
export const update = (id: number, data: User) => api.put<User>(`/users/${id}`, data);
export const remove = (id: number) => api.delete(`/users/${id}`);
