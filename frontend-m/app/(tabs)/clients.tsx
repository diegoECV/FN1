import { useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, ActivityIndicator,
  TouchableOpacity, RefreshControl, Modal, TextInput,
  ScrollView, Alert, KeyboardAvoidingView, Platform,
} from 'react-native';
import { api } from '../../services/api';
import { storage } from '../../utils/storage';

interface Client {
  id?: string;
  companyName: string; contactName: string;
  documentType: string; documentNumber: string;
  country: string; email: string; phone: string; address: string;
  preferredIncoterm: string; destinationPort: string;
  paymentTerms: string; preferredCurrency: string;
  creditLimit: string; language: string;
  isActive?: boolean;
}

const EMPTY: Client = {
  companyName: '', contactName: '', documentType: 'RUC', documentNumber: '',
  country: '', email: '', phone: '', address: '', preferredIncoterm: 'FOB',
  destinationPort: '', paymentTerms: '', preferredCurrency: 'USD', creditLimit: '', language: 'Español',
};

export default function ClientsScreen() {
  const [clients, setClients]   = useState<Client[]>([]);
  const [loading, setLoading]   = useState(true);
  const [refreshing, setRefresh]= useState(false);
  const [error, setError]       = useState('');
  const [showModal, setModal]   = useState(false);
  const [saving, setSaving]     = useState(false);
  const [form, setForm]         = useState<Client>(EMPTY);
  const [editId, setEditId]     = useState<string | null>(null);

  const load = async (isRefresh = false) => {
    isRefresh ? setRefresh(true) : setLoading(true);
    setError('');
    try {
      const data = await api.get<Client[]>('/clients?active=true');
      setClients(data);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Error al cargar');
    } finally { setLoading(false); setRefresh(false); }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(EMPTY); setEditId(null); setModal(true); };
  const openEdit   = (c: Client) => { setForm({ ...c, creditLimit: String(c.creditLimit ?? '') }); setEditId(c.id!); setModal(true); };

  const handleSave = async () => {
    if (!form.companyName.trim()) { Alert.alert('Error', 'El nombre de empresa es obligatorio'); return; }
    setSaving(true);
    try {
      const user = await storage.getUser();
      const now  = new Date().toISOString();
      const payload = { ...form, creditLimit: Number(form.creditLimit) || 0 };
      if (editId) {
        await api.put(`/clients/${editId}`, { ...payload, updatedAt: now, updatedBy: user?.username });
      } else {
        await api.post('/clients', { ...payload, createdAt: now, createdBy: user?.username, updatedAt: now, updatedBy: user?.username, isActive: true });
      }
      setModal(false);
      await load();
    } catch (e: unknown) {
      Alert.alert('Error', e instanceof Error ? e.message : 'No se pudo guardar');
    } finally { setSaving(false); }
  };

  const handleDelete = (c: Client) => {
    Alert.alert('Eliminar cliente', `¿Eliminar "${c.companyName}"?`, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Eliminar', style: 'destructive', onPress: async () => {
        try { await api.delete(`/clients/${c.id}`); await load(); }
        catch (e: unknown) { Alert.alert('Error', e instanceof Error ? e.message : 'Error'); }
      }},
    ]);
  };

  const f = (field: keyof Client) => (v: string) => setForm(prev => ({ ...prev, [field]: v }));

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#0369A1" /></View>;

  return (
    <View style={s.bg}>
      <View style={s.header}>
        <View>
          <Text style={s.title}>👥 Clientes</Text>
          <Text style={s.sub}>{clients.length} activos</Text>
        </View>
        <TouchableOpacity onPress={openCreate} style={s.addBtn}>
          <Text style={s.addTxt}>+ Nuevo</Text>
        </TouchableOpacity>
      </View>

      {error ? (
        <View style={s.center}>
          <Text style={s.err}>{error}</Text>
          <TouchableOpacity onPress={() => load()} style={s.retryBtn}><Text style={s.retryTxt}>Reintentar</Text></TouchableOpacity>
        </View>
      ) : (
        <FlatList
          data={clients}
          keyExtractor={i => i.id!}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => load(true)} colors={['#0369A1']} />}
          contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: 32 }}
          ListEmptyComponent={<Text style={s.empty}>Sin clientes. Toca "+ Nuevo" para agregar.</Text>}
          renderItem={({ item }) => (
            <View style={s.card}>
              <View style={s.cardTop}>
                <Text style={s.company}>{item.companyName}</Text>
                <View style={s.actions}>
                  <TouchableOpacity onPress={() => openEdit(item)} style={s.editBtn}><Text>✏️</Text></TouchableOpacity>
                  <TouchableOpacity onPress={() => handleDelete(item)} style={s.delBtn}><Text>🗑️</Text></TouchableOpacity>
                </View>
              </View>
              <Text style={s.contact}>👤 {item.contactName}</Text>
              <View style={s.row}>
                <Text style={s.det}>🌍 {item.country}</Text>
                <Text style={s.det}>📋 {item.preferredIncoterm}</Text>
                <Text style={s.det}>💵 {item.preferredCurrency}</Text>
              </View>
              <Text style={s.det}>✉️ {item.email}</Text>
              <Text style={s.det}>📞 {item.phone}</Text>
              {item.destinationPort ? <Text style={s.det}>🚢 {item.destinationPort}</Text> : null}
            </View>
          )}
        />
      )}

      <Modal visible={showModal} animationType="slide" onRequestClose={() => setModal(false)}>
        <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <View style={s.modalHeader}>
            <TouchableOpacity onPress={() => setModal(false)}><Text style={s.cancelTxt}>Cancelar</Text></TouchableOpacity>
            <Text style={s.modalTitle}>{editId ? 'Editar Cliente' : 'Nuevo Cliente'}</Text>
            <TouchableOpacity onPress={handleSave} disabled={saving}>
              {saving ? <ActivityIndicator color="#0369A1" /> : <Text style={s.saveTxt}>Guardar</Text>}
            </TouchableOpacity>
          </View>
          <ScrollView contentContainerStyle={s.form}>
            {([
              { label: 'Empresa *',          field: 'companyName',        placeholder: 'Fresh Exports Ltd' },
              { label: 'Contacto',           field: 'contactName',        placeholder: 'John Smith' },
              { label: 'Tipo documento',     field: 'documentType',       placeholder: 'RUC / TAX ID' },
              { label: 'Nro. documento',     field: 'documentNumber',     placeholder: '20987654321' },
              { label: 'País',               field: 'country',            placeholder: 'Estados Unidos' },
              { label: 'Email',              field: 'email',              placeholder: 'contact@fresh.com', keyboard: 'email-address' },
              { label: 'Teléfono',           field: 'phone',              placeholder: '+1 555 000 1234', keyboard: 'phone-pad' },
              { label: 'Dirección',          field: 'address',            placeholder: '123 Trade Blvd' },
              { label: 'Incoterm preferido', field: 'preferredIncoterm',  placeholder: 'FOB / CIF / EXW' },
              { label: 'Puerto destino',     field: 'destinationPort',    placeholder: 'Rotterdam / Los Ángeles' },
              { label: 'Condición de pago',  field: 'paymentTerms',       placeholder: '30 días / Carta crédito' },
              { label: 'Moneda preferida',   field: 'preferredCurrency',  placeholder: 'USD / EUR' },
              { label: 'Límite crédito',     field: 'creditLimit',        placeholder: '50000', keyboard: 'numeric' },
              { label: 'Idioma',             field: 'language',           placeholder: 'Inglés / Español' },
            ] as Array<{ label: string; field: keyof Client; placeholder: string; keyboard?: string }>).map(({ label, field, placeholder, keyboard }) => (
              <View key={field} style={s.inputGroup}>
                <Text style={s.label}>{label}</Text>
                <TextInput
                  style={s.input}
                  value={String(form[field] ?? '')}
                  onChangeText={f(field)}
                  placeholder={placeholder}
                  placeholderTextColor="#94A3B8"
                  keyboardType={(keyboard as never) ?? 'default'}
                  autoCapitalize="none"
                />
              </View>
            ))}
          </ScrollView>
        </KeyboardAvoidingView>
      </Modal>
    </View>
  );
}

const s = StyleSheet.create({
  bg: { flex: 1, backgroundColor: '#F0F9FF' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 12 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: '#0369A1', padding: 16, paddingTop: 52 },
  title: { fontSize: 20, fontWeight: '800', color: '#fff' },
  sub: { fontSize: 12, color: '#BAE6FD', marginTop: 2 },
  addBtn: { backgroundColor: '#fff', borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  addTxt: { color: '#0369A1', fontWeight: '700', fontSize: 14 },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 16, elevation: 3 },
  cardTop: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 },
  company: { fontSize: 16, fontWeight: '700', color: '#0F172A', flex: 1 },
  actions: { flexDirection: 'row', gap: 8 },
  editBtn: { backgroundColor: '#EFF6FF', borderRadius: 6, padding: 6 },
  delBtn: { backgroundColor: '#FEF2F2', borderRadius: 6, padding: 6 },
  contact: { fontSize: 14, color: '#334155', marginBottom: 6 },
  row: { flexDirection: 'row', gap: 12, marginBottom: 2 },
  det: { fontSize: 13, color: '#64748B', marginBottom: 2 },
  empty: { textAlign: 'center', color: '#94A3B8', marginTop: 60, fontSize: 15 },
  err: { color: '#DC2626', fontSize: 15, textAlign: 'center', paddingHorizontal: 24 },
  retryBtn: { backgroundColor: '#0369A1', borderRadius: 8, paddingHorizontal: 20, paddingVertical: 10 },
  retryTxt: { color: '#fff', fontWeight: '700' },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    padding: 16, paddingTop: 52, backgroundColor: '#fff', borderBottomWidth: 1, borderBottomColor: '#E2E8F0' },
  modalTitle: { fontSize: 17, fontWeight: '700', color: '#0F172A' },
  cancelTxt: { color: '#64748B', fontSize: 15 },
  saveTxt: { color: '#0369A1', fontSize: 15, fontWeight: '700' },
  form: { padding: 20, gap: 16, paddingBottom: 60 },
  inputGroup: { gap: 4 },
  label: { fontSize: 13, fontWeight: '600', color: '#374151' },
  input: { borderWidth: 1, borderColor: '#D1D5DB', borderRadius: 10, padding: 12,
    fontSize: 15, color: '#111827', backgroundColor: '#F9FAFB' },
});
