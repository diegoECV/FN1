from dataclasses import dataclass

@dataclass
class InventarioResumenModel:
    lotes_en_inventario_mongo: int
    lotes_produccion_sql: int
