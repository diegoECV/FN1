import { useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, ActivityIndicator,
  TouchableOpacity, RefreshControl, Modal, TextInput,
  ScrollView, Alert, KeyboardAvoidingView, Platform,
} from 'react-native';
import { api } from '../../services/api';
import { storage } from '../../utils/storage';

interface Provider {
  id?: string;
  companyName: string;
  contactName?: string;
  documentType?: string;
  documentNumber?: string;
  taxId?: string;           // campo alternativo en documentos viejos
  country?: string;
  department?: string;
  province?: string;
  district?: string;
  email?: string;
  contactEmail?: string;    // campo alternativo en documentos viejos
  phone?: string;
  contactPhone?: string;    // campo alternativo en documentos viejos
  address?: string;
  mainCrop?: string;
  productType?: string;     // campo alternativo en documentos viejos
  certificationStatus?: string;
  bankAccountCci?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

const EMPTY: Provider = {
  companyName: '', contactName: '', documentType: 'RUC', documentNumber: '',
  country: 'Perú', department: '', province: '', district: '',
  email: '', phone: '', address: '', mainCrop: '', certificationStatus: '', bankAccountCci: '',
};

export default function ProvidersScreen() {
  const [providers, setProviders] = useState<Provider[]>([]);
  const [loading, setLoading]     = useState(true);
  const [refreshing, setRefresh]  = useState(false);
  const [error, setError]         = useState('');
  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving]       = useState(false);
  const [form, setForm]           = useState<Provider>(EMPTY);
  const [editId, setEditId]       = useState<string | null>(null);

  const load = async (isRefresh = false) => {
    isRefresh ? setRefresh(true) : setLoading(true);
    setError('');
    try {
      const data = await api.get<Provider[]>('/providers?active=true');
      setProviders(data);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Error al cargar');
    } finally { setLoading(false); setRefresh(false); }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(EMPTY); setEditId(null); setShowModal(true); };
  const openEdit   = (p: Provider) => { setForm({ ...p }); setEditId(p.id!); setShowModal(true); };

  const handleSave = async () => {
    if (!form.companyName.trim()) { Alert.alert('Error', 'El nombre de empresa es obligatorio'); return; }
    setSaving(true);
    try {
      const user = await storage.getUser();
      const now  = new Date().toISOString();
      if (editId) {
        await api.put(`/providers/${editId}`, { ...form, updatedAt: now, updatedBy: user?.username });
      } else {
        await api.post('/providers', { ...form, createdAt: now, createdBy: user?.username, updatedAt: now, updatedBy: user?.username, isActive: true });
      }
      setShowModal(false);
      await load();
    } catch (e: unknown) {
      Alert.alert('Error', e instanceof Error ? e.message : 'No se pudo guardar');
    } finally { setSaving(false); }
  };

  const handleDelete = (p: Provider) => {
    Alert.alert('Eliminar proveedor', `¿Eliminar "${p.companyName}"?`, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Eliminar', style: 'destructive', onPress: async () => {
        try {
          await api.delete(`/providers/${p.id}`);
          await load();
        } catch (e: unknown) {
          Alert.alert('Error', e instanceof Error ? e.message : 'No se pudo eliminar');
        }
      }},
    ]);
  };

  const f = (field: keyof Provider) => (v: string) => setForm(prev => ({ ...prev, [field]: v }));

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#047857" /></View>;

  return (
    <View style={s.bg}>
      {/* Header */}
      <View style={s.header}>
        <View>
          <Text style={s.title}>🏭 Proveedores</Text>
          <Text style={s.sub}>{providers.length} activos</Text>
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
          data={providers}
          keyExtractor={i => i.id!}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => load(true)} colors={['#047857']} />}
          contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: 32 }}
          ListEmptyComponent={<Text style={s.empty}>Sin proveedores. Toca "+ Nuevo" para agregar.</Text>}
          renderItem={({ item }) => {
            const email   = item.email   || item.contactEmail  || '';
            const phone   = item.phone   || item.contactPhone  || '';
            const crop    = item.mainCrop || item.productType  || '';
            const contact = item.contactName || '';
            const docNum  = item.documentNumber || item.taxId  || '';
            const loc     = [item.department, item.country].filter(Boolean).join(' · ');
            return (
            <View style={s.card}>
              <View style={s.cardTop}>
                <Text style={s.company}>{item.companyName}</Text>
                <View style={s.actions}>
                  <TouchableOpacity onPress={() => openEdit(item)} style={s.editBtn}>
                    <Text style={s.editTxt}>✏️</Text>
                  </TouchableOpacity>
                  <TouchableOpacity onPress={() => handleDelete(item)} style={s.delBtn}>
                    <Text style={s.delTxt}>🗑️</Text>
                  </TouchableOpacity>
                </View>
              </View>
              {contact ? <Text style={s.contact}>👤 {contact}</Text> : null}
              {docNum  ? <Text style={s.det}>🪪 {docNum}</Text> : null}
              {loc     ? <Text style={s.det}>📍 {loc}</Text> : null}
              {item.address ? <Text style={s.det}>🏠 {item.address}</Text> : null}
              {crop    ? <Text style={s.det}>🌱 {crop}</Text> : null}
              {email   ? <Text style={s.det}>✉️ {email}</Text> : null}
              {phone   ? <Text style={s.det}>📞 {phone}</Text> : null}
              {item.certificationStatus ? <Text style={s.cert}>🏅 {item.certificationStatus}</Text> : null}
            </View>
          );}}
        />
      )}

      {/* Modal Crear / Editar */}
      <Modal visible={showModal} animationType="slide" onRequestClose={() => setShowModal(false)}>
        <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <View style={s.modalHeader}>
            <TouchableOpacity onPress={() => setShowModal(false)}>
              <Text style={s.cancelTxt}>Cancelar</Text>
            </TouchableOpacity>
            <Text style={s.modalTitle}>{editId ? 'Editar Proveedor' : 'Nuevo Proveedor'}</Text>
            <TouchableOpacity onPress={handleSave} disabled={saving}>
              {saving ? <ActivityIndicator color="#047857" /> : <Text style={s.saveTxt}>Guardar</Text>}
            </TouchableOpacity>
          </View>
          <ScrollView contentContainerStyle={s.form}>
            {([
              { label: 'Empresa *',          field: 'companyName',         placeholder: 'Agro SAC' },
              { label: 'Contacto',           field: 'contactName',         placeholder: 'Juan Pérez' },
              { label: 'Tipo documento',     field: 'documentType',        placeholder: 'RUC / DNI' },
              { label: 'Nro. documento',     field: 'documentNumber',      placeholder: '20123456789' },
              { label: 'País',               field: 'country',             placeholder: 'Perú' },
              { label: 'Departamento',       field: 'department',          placeholder: 'La Libertad' },
              { label: 'Provincia',          field: 'province',            placeholder: 'Trujillo' },
              { label: 'Distrito',           field: 'district',            placeholder: 'Moche' },
              { label: 'Email',              field: 'email',               placeholder: 'contacto@empresa.pe', keyboard: 'email-address' },
              { label: 'Teléfono',           field: 'phone',               placeholder: '999 888 777', keyboard: 'phone-pad' },
              { label: 'Dirección',          field: 'address',             placeholder: 'Av. Los Campos 123' },
              { label: 'Cultivo principal',  field: 'mainCrop',            placeholder: 'Palta Hass' },
              { label: 'Certificación',      field: 'certificationStatus', placeholder: 'Global G.A.P. / Orgánico' },
              { label: 'CCI bancario',       field: 'bankAccountCci',      placeholder: '00219123456789012345' },
            ] as Array<{ label: string; field: keyof Provider; placeholder: string; keyboard?: string }>).map(({ label, field, placeholder, keyboard }) => (
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
  bg: { flex: 1, backgroundColor: '#F0FFF4' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 12 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: '#047857', padding: 16, paddingTop: 52 },
  title: { fontSize: 20, fontWeight: '800', color: '#fff' },
  sub: { fontSize: 12, color: '#A7F3D0', marginTop: 2 },
  addBtn: { backgroundColor: '#fff', borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  addTxt: { color: '#047857', fontWeight: '700', fontSize: 14 },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 16, elevation: 3 },
  cardTop: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 },
  company: { fontSize: 16, fontWeight: '700', color: '#0F172A', flex: 1 },
  actions: { flexDirection: 'row', gap: 8 },
  editBtn: { backgroundColor: '#EFF6FF', borderRadius: 6, padding: 6 },
  editTxt: { fontSize: 16 },
  delBtn: { backgroundColor: '#FEF2F2', borderRadius: 6, padding: 6 },
  delTxt: { fontSize: 16 },
  contact: { fontSize: 14, color: '#334155', marginBottom: 6 },
  row: { flexDirection: 'row', gap: 12, marginBottom: 2 },
  det: { fontSize: 13, color: '#64748B', marginBottom: 2 },
  cert: { fontSize: 12, color: '#047857', fontWeight: '600', marginTop: 4 },
  empty: { textAlign: 'center', color: '#94A3B8', marginTop: 60, fontSize: 15 },
  err: { color: '#DC2626', fontSize: 15, textAlign: 'center', paddingHorizontal: 24 },
  retryBtn: { backgroundColor: '#047857', borderRadius: 8, paddingHorizontal: 20, paddingVertical: 10 },
  retryTxt: { color: '#fff', fontWeight: '700' },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    padding: 16, paddingTop: 52, backgroundColor: '#fff', borderBottomWidth: 1, borderBottomColor: '#E2E8F0' },
  modalTitle: { fontSize: 17, fontWeight: '700', color: '#0F172A' },
  cancelTxt: { color: '#64748B', fontSize: 15 },
  saveTxt: { color: '#047857', fontSize: 15, fontWeight: '700' },
  form: { padding: 20, gap: 16, paddingBottom: 60 },
  inputGroup: { gap: 4 },
  label: { fontSize: 13, fontWeight: '600', color: '#374151' },
  input: { borderWidth: 1, borderColor: '#D1D5DB', borderRadius: 10, padding: 12,
    fontSize: 15, color: '#111827', backgroundColor: '#F9FAFB' },
});
