from dataclasses import dataclass

@dataclass
class CalidadResumenModel:
    total_inspecciones: int
    lotes_aprobados: int
    lotes_rechazados: int
