// ============================================================
// api.ts — Cliente HTTP centralizado
// Todas las llamadas al backend pasan por aquí.
// Lee la URL base desde constants/config.ts (que la lee del .env)
// ============================================================

import { API_URL, BA_URL, REQUEST_TIMEOUT } from '../constants/config';
import { storage } from '../utils/storage';

// ── Función base de fetch con timeout y auth ─────────────────

async function request<T>(
  baseUrl: string,
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = await storage.getToken();

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT);

  try {
    const response = await fetch(`${baseUrl}${path}`, {
      ...options,
      headers,
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorBody}`);
    }

    // 204 No Content — no intentes parsear JSON
    if (response.status === 204) return undefined as unknown as T;

    return response.json() as Promise<T>;
  } catch (err: unknown) {
    clearTimeout(timeoutId);
    if (err instanceof Error && err.name === 'AbortError') {
      throw new Error('La petición tardó demasiado. Revisa tu conexión.');
    }
    throw err;
  }
}

// ── API del BE (Spring Boot — puerto 9090) ───────────────────

export const api = {
  get:    <T>(path: string)                    => request<T>(API_URL, path),
  post:   <T>(path: string, body: unknown)     => request<T>(API_URL, path, { method: 'POST',  body: JSON.stringify(body) }),
  put:    <T>(path: string, body: unknown)     => request<T>(API_URL, path, { method: 'PUT',   body: JSON.stringify(body) }),
  patch:  <T>(path: string, body?: unknown)    => request<T>(API_URL, path, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }),
  delete: <T>(path: string)                    => request<T>(API_URL, path, { method: 'DELETE' }),
};

// ── API del BA (Flask Analítico — puerto 5000) ───────────────

export const baApi = {
  get: <T>(path: string) => request<T>(BA_URL, path),
};
