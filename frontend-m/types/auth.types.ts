// Tipos que coinciden con los DTOs del BE (AuthRequest / AuthResponse)

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
  expiresIn: number; // segundos
}

export interface User {
  id?: string;
  username: string;
  email?: string;
  role: string;
  isActive?: boolean;
}
