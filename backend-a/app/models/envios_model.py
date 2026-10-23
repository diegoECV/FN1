from dataclasses import dataclass

@dataclass
class EnviosResumenModel:
    total_envios: int
    envios_en_transito: int
