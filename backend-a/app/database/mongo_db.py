from pymongo import MongoClient
from app.config.settings import settings
import logging

logger = logging.getLogger(__name__)

class MongoDB:
    client = None
    db = None

    @classmethod
    def connect(cls):
        try:
            cls.client = MongoClient(settings.MONGO_URI, serverSelectionTimeoutMS=5000)
            cls.db = cls.client[settings.MONGO_DB_NAME]
            logger.info("Conectado a MongoDB Exitosamente")
        except Exception as e:
            logger.error(f"Error conectando a MongoDB: {e}")

    @classmethod
    def get_db(cls):
        if cls.db is None:
            cls.connect()
        return cls.db

mongo_db = MongoDB()
