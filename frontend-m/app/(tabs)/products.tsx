import { useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, ActivityIndicator,
  TouchableOpacity, RefreshControl, Modal, TextInput,
  ScrollView, Alert, KeyboardAvoidingView, Platform,
} from 'react-native';
import { api } from '../../services/api';
import { storage } from '../../utils/storage';

interface Product {
  id?: string;
  code: string; name: string; scientificName: string;
  categoryId: string; variety: string; caliber: string;
  unitMeasure: string; hsCode: string; storageTemperature: string;
  shelfLifeDays: string; packagingType: string; originRegion: string;
  qualityGrade: string; organicCertification: string;
  isActive?: boolean;
}

const EMPTY: Product = {
  code: '', name: '', scientificName: '', categoryId: '', variety: '', caliber: '',
  unitMeasure: 'KG', hsCode: '', storageTemperature: '', shelfLifeDays: '',
  packagingType: '', originRegion: '', qualityGrade: '', organicCertification: '',
};

export default function ProductsScreen() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading]   = useState(true);
  const [refreshing, setRefresh]= useState(false);
  const [error, setError]       = useState('');
  const [showModal, setModal]   = useState(false);
  const [saving, setSaving]     = useState(false);
  const [form, setForm]         = useState<Product>(EMPTY);
  const [editId, setEditId]     = useState<string | null>(null);

  const load = async (isRefresh = false) => {
    isRefresh ? setRefresh(true) : setLoading(true);
    setError('');
    try {
      const data = await api.get<Product[]>('/products?active=true');
      setProducts(data);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Error al cargar');
    } finally { setLoading(false); setRefresh(false); }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(EMPTY); setEditId(null); setModal(true); };
  const openEdit   = (p: Product) => {
    setForm({ ...p, shelfLifeDays: String(p.shelfLifeDays ?? '') });
    setEditId(p.id!); setModal(true);
  };

  const handleSave = async () => {
    if (!form.name.trim()) { Alert.alert('Error', 'El nombre del producto es obligatorio'); return; }
    setSaving(true);
    try {
      const user = await storage.getUser();
      const now  = new Date().toISOString();
      const payload = { ...form, shelfLifeDays: Number(form.shelfLifeDays) || 0 };
      if (editId) {
        await api.put(`/products/${editId}`, { ...payload, updatedAt: now, updatedBy: user?.username });
      } else {
        await api.post('/products', { ...payload, createdAt: now, createdBy: user?.username, updatedAt: now, updatedBy: user?.username, isActive: true });
      }
      setModal(false);
      await load();
    } catch (e: unknown) {
      Alert.alert('Error', e instanceof Error ? e.message : 'No se pudo guardar');
    } finally { setSaving(false); }
  };

  const handleDelete = (p: Product) => {
    Alert.alert('Eliminar producto', `¿Eliminar "${p.name}"?`, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Eliminar', style: 'destructive', onPress: async () => {
        try { await api.delete(`/products/${p.id}`); await load(); }
        catch (e: unknown) { Alert.alert('Error', e instanceof Error ? e.message : 'Error'); }
      }},
    ]);
  };

  const f = (field: keyof Product) => (v: string) => setForm(prev => ({ ...prev, [field]: v }));

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#7C3AED" /></View>;

  return (
    <View style={s.bg}>
      <View style={s.header}>
        <View>
          <Text style={s.title}>📦 Productos</Text>
          <Text style={s.sub}>{products.length} activos</Text>
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
          data={products}
          keyExtractor={i => i.id!}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => load(true)} colors={['#7C3AED']} />}
          contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: 32 }}
          ListEmptyComponent={<Text style={s.empty}>Sin productos. Toca "+ Nuevo" para agregar.</Text>}
          renderItem={({ item }) => {
            // Cubre tanto camelCase (dev-2.0) como snake_case (seed viejo)
            const name     = item.name || '';
            const code     = item.code || '';
            const sci      = item.scientificName || '';
            const variety  = item.variety || '';
            const caliber  = item.caliber || (item as unknown as Record<string,string>).caliber || '';
            const unit     = item.unitMeasure || (item as unknown as Record<string,string>).unit_measure || '';
            const region   = item.originRegion || (item as unknown as Record<string,string>).attributes?.origin || '';
            const grade    = item.qualityGrade || '';
            const temp     = item.storageTemperature || '';
            const shelf    = item.shelfLifeDays || '';
            const cert     = item.organicCertification || '';
            return (
            <View style={s.card}>
              <View style={s.cardTop}>
                <View style={{ flex: 1 }}>
                  <Text style={s.name}>{name}</Text>
                  {code ? <Text style={s.code}>{code}</Text> : null}
                </View>
                <View style={s.actions}>
                  <TouchableOpacity onPress={() => openEdit(item)} style={s.editBtn}><Text>✏️</Text></TouchableOpacity>
                  <TouchableOpacity onPress={() => handleDelete(item)} style={s.delBtn}><Text>🗑️</Text></TouchableOpacity>
                </View>
              </View>
              {sci     ? <Text style={s.sci}>🔬 {sci}</Text> : null}
              <View style={s.row}>
                {variety ? <Text style={s.det}>🌿 {variety}</Text> : null}
                {caliber ? <Text style={s.det}>📏 {caliber}</Text> : null}
                {unit    ? <Text style={s.det}>⚖️ {unit}</Text>    : null}
              </View>
              <View style={s.row}>
                {region  ? <Text style={s.det}>📍 {region}</Text>  : null}
                {grade   ? <Text style={s.det}>⭐ {grade}</Text>   : null}
              </View>
              <View style={s.row}>
                {temp    ? <Text style={s.det}>🌡️ {temp}</Text>   : null}
                {shelf   ? <Text style={s.det}>📅 {shelf} días</Text> : null}
              </View>
              {cert    ? <Text style={s.cert}>🏅 {cert}</Text>    : null}
            </View>
          );}}
        />
      )}

      <Modal visible={showModal} animationType="slide" onRequestClose={() => setModal(false)}>
        <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <View style={s.modalHeader}>
            <TouchableOpacity onPress={() => setModal(false)}><Text style={s.cancelTxt}>Cancelar</Text></TouchableOpacity>
            <Text style={s.modalTitle}>{editId ? 'Editar Producto' : 'Nuevo Producto'}</Text>
            <TouchableOpacity onPress={handleSave} disabled={saving}>
              {saving ? <ActivityIndicator color="#7C3AED" /> : <Text style={s.saveTxt}>Guardar</Text>}
            </TouchableOpacity>
          </View>
          <ScrollView contentContainerStyle={s.form}>
            {([
              { label: 'Nombre *',             field: 'name',                 placeholder: 'Palta Hass' },
              { label: 'Código',               field: 'code',                 placeholder: 'PROD-001' },
              { label: 'Nombre científico',    field: 'scientificName',       placeholder: 'Persea americana' },
              { label: 'Variedad',             field: 'variety',              placeholder: 'Hass / Fuerte' },
              { label: 'Calibre',              field: 'caliber',              placeholder: '16 / 20 / 24' },
              { label: 'Unidad de medida',     field: 'unitMeasure',          placeholder: 'KG / TON' },
              { label: 'Partida arancelaria',  field: 'hsCode',               placeholder: '0804.40.00', keyboard: 'numeric' },
              { label: 'Temperatura almacén',  field: 'storageTemperature',   placeholder: '4-8°C' },
              { label: 'Vida útil (días)',      field: 'shelfLifeDays',        placeholder: '30', keyboard: 'numeric' },
              { label: 'Tipo empaque',         field: 'packagingType',        placeholder: 'Caja 4kg / Pallet' },
              { label: 'Región de origen',     field: 'originRegion',         placeholder: 'La Libertad' },
              { label: 'Grado de calidad',     field: 'qualityGrade',         placeholder: 'Extra / Primera' },
              { label: 'Certificación orgánica', field: 'organicCertification', placeholder: 'Biolatina / USDA' },
            ] as Array<{ label: string; field: keyof Product; placeholder: string; keyboard?: string }>).map(({ label, field, placeholder, keyboard }) => (
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
  bg: { flex: 1, backgroundColor: '#FAF5FF' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 12 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: '#7C3AED', padding: 16, paddingTop: 52 },
  title: { fontSize: 20, fontWeight: '800', color: '#fff' },
  sub: { fontSize: 12, color: '#DDD6FE', marginTop: 2 },
  addBtn: { backgroundColor: '#fff', borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  addTxt: { color: '#7C3AED', fontWeight: '700', fontSize: 14 },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 16, elevation: 3 },
  cardTop: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 6 },
  name: { fontSize: 16, fontWeight: '700', color: '#0F172A' },
  code: { fontSize: 12, color: '#7C3AED', fontWeight: '600', marginTop: 2 },
  actions: { flexDirection: 'row', gap: 8 },
  editBtn: { backgroundColor: '#EFF6FF', borderRadius: 6, padding: 6 },
  delBtn: { backgroundColor: '#FEF2F2', borderRadius: 6, padding: 6 },
  sci: { fontSize: 13, color: '#64748B', fontStyle: 'italic', marginBottom: 6 },
  row: { flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginBottom: 2 },
  det: { fontSize: 13, color: '#64748B' },
  cert: { fontSize: 12, color: '#7C3AED', fontWeight: '600', marginTop: 4 },
  empty: { textAlign: 'center', color: '#94A3B8', marginTop: 60, fontSize: 15 },
  err: { color: '#DC2626', fontSize: 15, textAlign: 'center', paddingHorizontal: 24 },
  retryBtn: { backgroundColor: '#7C3AED', borderRadius: 8, paddingHorizontal: 20, paddingVertical: 10 },
  retryTxt: { color: '#fff', fontWeight: '700' },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    padding: 16, paddingTop: 52, backgroundColor: '#fff', borderBottomWidth: 1, borderBottomColor: '#E2E8F0' },
  modalTitle: { fontSize: 17, fontWeight: '700', color: '#0F172A' },
  cancelTxt: { color: '#64748B', fontSize: 15 },
  saveTxt: { color: '#7C3AED', fontSize: 15, fontWeight: '700' },
  form: { padding: 20, gap: 16, paddingBottom: 60 },
  inputGroup: { gap: 4 },
  label: { fontSize: 13, fontWeight: '600', color: '#374151' },
  input: { borderWidth: 1, borderColor: '#D1D5DB', borderRadius: 10, padding: 12,
    fontSize: 15, color: '#111827', backgroundColor: '#F9FAFB' },
});
