import os
from dotenv import load_dotenv

# Cargar variables de entorno desde el archivo .env de backend-a
env_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), '.env')
load_dotenv(env_path)

class Settings:
    # MongoDB Config
    MONGO_URI = os.getenv("MONGO_URI", "mongodb://admin:admin123@127.0.0.1:27017/Visons?authSource=admin&directConnection=true")
    MONGO_DB_NAME = os.getenv("MONGO_DB_NAME", "Visons")

    # SQL Server Config
    SQL_SERVER = os.getenv("SQL_SERVER", "127.0.0.1")
    SQL_PORT = os.getenv("SQL_PORT", "1433")
    SQL_DATABASE = os.getenv("SQL_DATABASE", "Visons")
    SQL_USER = os.getenv("SQL_USER", "sa")
    SQL_PASSWORD = os.getenv("SQL_PASSWORD", "Password123!")

    # Flask Config
    FLASK_PORT = int(os.getenv("FLASK_PORT", 5000))
    FLASK_DEBUG = os.getenv("FLASK_DEBUG", "True").lower() in ("true", "1", "t")

settings = Settings()
