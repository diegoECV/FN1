import urllib.parse
from sqlalchemy import create_engine
from app.config.settings import settings
import logging

logger = logging.getLogger(__name__)

class SQLDatabase:
    engine = None

    @classmethod
    def init_pool(cls):
        # Dialecto de SQLAlchemy para MSSQL usando pymssql
        # Formato: mssql+pymssql://<username>:<password>@<freetds_name>/?charset=utf8
        conn_str = f"mssql+pymssql://{settings.SQL_USER}:{settings.SQL_PASSWORD}@{settings.SQL_SERVER}:{settings.SQL_PORT}/{settings.SQL_DATABASE}"
        
        # Crear engine con un Pool de Conexiones
        cls.engine = create_engine(
            conn_str, 
            connect_args={"timeout": 3, "login_timeout": 3},
            pool_size=10,        # Mantiene 10 conexiones permanentemente abiertas
            max_overflow=20,     # Puede abrir hasta 20 más en picos de tráfico extremo
            pool_timeout=5,      # Espera hasta 5s por una conexión libre si el pool está lleno
            pool_recycle=1800    # Renueva la conexión cada 30 min (evita caídas por firewall)
        )
        logger.info("Connection Pool de SQL Server inicializado con éxito")

    @classmethod
    def get_connection(cls):
        if cls.engine is None:
            cls.init_pool()
        try:
            # En lugar de crear una conexión, toma "prestada" una conexión del pool
            return cls.engine.raw_connection()
        except Exception as e:
            logger.error(f"Error obteniendo conexión del pool: {e}")
            return None

def get_sql_connection():
    """
    Función helper que mantiene compatibilidad con el resto del proyecto.
    Retorna una conexión cruda de pyodbc sacada directamente del pool.
    """
    return SQLDatabase.get_connection()
