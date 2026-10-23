from app.database.sql_db import get_sql_connection
from app.models.audit_model import AuditResumenModel
import logging

logger = logging.getLogger(__name__)

class AuditRepository:
    def get_audit_resumen(self) -> AuditResumenModel:
        conn = get_sql_connection()
        if not conn:
            return None
        
        try:
            cursor = conn.cursor()
            
            # Total de logs y logins recientes
            cursor.execute("""
                SELECT 
                    COUNT(id) as total_logs,
                    SUM(CASE WHEN action = 'LOGIN' THEN 1 ELSE 0 END) as total_logins
                FROM AUDIT_LOGS
            """)
            row = cursor.fetchone()
            
            # Acciones agrupadas por recurso
            cursor.execute("""
                SELECT resource, COUNT(id) as cantidad
                FROM AUDIT_LOGS
                WHERE resource IS NOT NULL
                GROUP BY resource
                ORDER BY cantidad DESC
            """)
            recursos = [{"recurso": r[0], "cantidad": r[1]} for r in cursor.fetchall()]
            
            return AuditResumenModel(
                total_logs=row[0] if row else 0,
                logins_recientes=row[1] if row else 0,
                acciones_por_recurso=recursos
            )
            
        except Exception as e:
            logger.error(f"Error en AuditRepository: {e}")
            return None
        finally:
            conn.close()

audit_repo = AuditRepository()
