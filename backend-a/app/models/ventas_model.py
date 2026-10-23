from dataclasses import dataclass

@dataclass
class VentasResumenModel:
    total_ordenes: int
    ordenes_pagadas: int
    ordenes_pendientes: int
    revenue_usd: float
