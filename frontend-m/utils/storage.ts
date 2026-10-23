// Usamos MMKV-style con un simple Map en memoria + fallback a global
// para Expo Go que no soporta AsyncStorage nativo en new architecture

export const TOKEN_KEY = 'visons_token';
export const USER_KEY  = 'visons_user';

// Almacenamiento en memoria (funciona en Expo Go sin módulos nativos)
const mem = new Map<string, string>();

// Intenta usar AsyncStorage nativo si está disponible, si no usa memoria
let _AsyncStorage: typeof import('@react-native-async-storage/async-storage').default | null = null;

try {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const mod = require('@react-native-async-storage/async-storage');
  _AsyncStorage = mod.default ?? mod;
} catch {
  _AsyncStorage = null;
}

async function setItem(key: string, value: string): Promise<void> {
  mem.set(key, value);
  try { await _AsyncStorage?.setItem(key, value); } catch { /* usa memoria */ }
}

async function getItem(key: string): Promise<string | null> {
  try {
    const v = await _AsyncStorage?.getItem(key);
    if (v !== undefined && v !== null) return v;
  } catch { /* usa memoria */ }
  return mem.get(key) ?? null;
}

async function removeItem(key: string): Promise<void> {
  mem.delete(key);
  try { await _AsyncStorage?.removeItem(key); } catch { /* usa memoria */ }
}

export const storage = {
  saveToken:   (token: string) => setItem(TOKEN_KEY, token),
  getToken:    ()              => getItem(TOKEN_KEY),
  removeToken: ()              => removeItem(TOKEN_KEY),

  saveUser: (user: object)  => setItem(USER_KEY, JSON.stringify(user)),
  getUser:  async ()        => {
    const raw = await getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },
  removeUser: () => removeItem(USER_KEY),

  clear: async () => {
    await removeItem(TOKEN_KEY);
    await removeItem(USER_KEY);
  },
};
