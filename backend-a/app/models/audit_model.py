from dataclasses import dataclass
from typing import List, Dict, Any

@dataclass
class AuditResumenModel:
    total_logs: int
    logins_recientes: int
    acciones_por_recurso: List[Dict[str, Any]]
