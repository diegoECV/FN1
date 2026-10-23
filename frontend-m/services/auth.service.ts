import { api } from './api';
import { storage } from '../utils/storage';
import { ENDPOINTS } from '../constants/config';
import type { AuthRequest, AuthResponse } from '../types/auth.types';

export const authService = {
  // Hace login y guarda el token + usuario en AsyncStorage
  login: async (credentials: AuthRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>(ENDPOINTS.LOGIN, credentials);
    await storage.saveToken(response.token);
    await storage.saveUser({ username: response.username, role: response.role });
    return response;
  },

  // Cierra sesión limpiando el storage
  logout: async (): Promise<void> => {
    try {
      await api.post(ENDPOINTS.LOGOUT, {});
    } finally {
      // Aunque falle el endpoint, limpiamos local igual
      await storage.clear();
    }
  },

  // Verifica si hay sesión activa
  isAuthenticated: async (): Promise<boolean> => {
    const token = await storage.getToken();
    return token !== null;
  },

  // Obtiene el usuario guardado
  getCurrentUser: () => storage.getUser(),
};
