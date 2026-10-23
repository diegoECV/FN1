from app.repositories.audit_repo import audit_repo

class AuditService:
    def get_resumen(self):
        data = audit_repo.get_audit_resumen()
        if not data:
            return None, "No se pudo obtener la información de auditoría o no hay conexión"
        
        # Convertimos el modelo a diccionario
        return {
            "total_logs": data.total_logs,
            "logins_recientes": data.logins_recientes,
            "acciones_por_recurso": data.acciones_por_recurso
        }, None

audit_service = AuditService()
