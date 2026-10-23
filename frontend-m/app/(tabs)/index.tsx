import { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { router } from 'expo-router';
import { storage } from '../../utils/storage';
import { api } from '../../services/api';

interface Stats { clients: number; providers: number; products: number; }

export default function HomeScreen() {
  const [user, setUser]   = useState<{ username: string; role: string } | null>(null);
  const [stats, setStats] = useState<Stats>({ clients: 0, providers: 0, products: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      const u = await storage.getUser();
      setUser(u);
      try {
        const [c, p, pr] = await Promise.all([
          api.get<unknown[]>('/clients?active=true'),
          api.get<unknown[]>('/providers?active=true'),
          api.get<unknown[]>('/products?active=true'),
        ]);
        setStats({ clients: c.length, providers: p.length, products: pr.length });
      } catch { /* vacío si sin conexión */ }
      setLoading(false);
    })();
  }, []);

  const handleLogout = async () => { await storage.clear(); router.replace('/login'); };

  return (
    <ScrollView style={styles.bg} contentContainerStyle={styles.content}>
      <View style={styles.header}>
        <View>
          <Text style={styles.greeting}>Hola, {user?.username ?? '...'} 👋</Text>
          <Text style={styles.role}>{user?.role}</Text>
        </View>
        <TouchableOpacity onPress={handleLogout} style={styles.logoutBtn}>
          <Text style={styles.logoutTxt}>Salir</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.section}>Maestros — resumen</Text>

      {loading ? <ActivityIndicator color="#0369A1" size="large" style={{ marginTop: 32 }} /> : (
        <View style={styles.grid}>
          {[
            { label: 'Clientes',    value: stats.clients,    icon: '👥', color: '#0369A1', tab: 'clients' },
            { label: 'Proveedores', value: stats.providers,  icon: '🏭', color: '#047857', tab: 'providers' },
            { label: 'Productos',   value: stats.products,   icon: '📦', color: '#7C3AED', tab: 'products' },
          ].map(c => (
            <TouchableOpacity key={c.label} style={[styles.card, { borderTopColor: c.color }]}
              onPress={() => router.push(`/(tabs)/${c.tab}` as never)}>
              <Text style={styles.cardIcon}>{c.icon}</Text>
              <Text style={[styles.cardVal, { color: c.color }]}>{c.value}</Text>
              <Text style={styles.cardLbl}>{c.label}</Text>
            </TouchableOpacity>
          ))}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  bg: { flex: 1, backgroundColor: '#F0F9FF' },
  content: { padding: 20, paddingBottom: 40 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: '#fff', borderRadius: 14, padding: 16, marginBottom: 20, elevation: 3 },
  greeting: { fontSize: 18, fontWeight: '700', color: '#0F172A' },
  role: { fontSize: 12, color: '#64748B', marginTop: 2 },
  logoutBtn: { backgroundColor: '#FEE2E2', borderRadius: 8, paddingHorizontal: 12, paddingVertical: 6 },
  logoutTxt: { color: '#DC2626', fontWeight: '600', fontSize: 13 },
  section: { fontSize: 15, fontWeight: '700', color: '#0F172A', marginBottom: 12 },
  grid: { flexDirection: 'row', gap: 12 },
  card: { flex: 1, backgroundColor: '#fff', borderRadius: 12, padding: 16,
    borderTopWidth: 4, elevation: 2, alignItems: 'center' },
  cardIcon: { fontSize: 26, marginBottom: 4 },
  cardVal: { fontSize: 28, fontWeight: '800' },
  cardLbl: { fontSize: 12, color: '#64748B', marginTop: 2 },
});
