from dataclasses import dataclass
from typing import List, Dict, Any

@dataclass
class DiegoIndicadoresModel:
    estudiante: str
    modulo: str
    total_ordenes: int
    facturacion_total_usd: float
    ordenes_pendientes_pago: int
    ticket_promedio_usd: float
    ventas_historico_mensual: List[Dict[str, Any]]
    total_despachos_transito: int

@dataclass
class CamilaIndicadoresModel:
    estudiante: str
    modulo: str
    total_proveedores_activos: int
    proveedores_por_certificacion: List[Dict[str, Any]]
    volumen_acopio_kg: float
    total_eventos_trazabilidad: int
    alertas_cadena_frio: int
    cumplimiento_entregas_pct: float

@dataclass
class LizethIndicadoresModel:
    estudiante: str
    modulo: str
    total_inspecciones_calidad: int
    tasa_aprobacion_pct: float
    tasa_rechazo_pct: float
    promedio_grados_brix: float
    stock_lotes_camara_frio: int
    distribucion_por_calibre: List[Dict[str, Any]]
