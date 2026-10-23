import logging
from flask import Flask, jsonify
from flask_cors import CORS
from flasgger import Swagger
from app.config.settings import settings
from app.config.cache import cache
from app.database.mongo_db import mongo_db
from app.routes.ventas_routes import ventas_bp
from app.routes.envios_routes import envios_bp
from app.routes.calidad_routes import calidad_bp
from app.routes.inventario_routes import inventario_bp
from app.routes.clientes_routes import clientes_bp
from app.routes.proveedores_routes import proveedores_bp
from app.routes.trazabilidad_routes import trazabilidad_bp
from app.routes.audit_routes import audit_bp
from app.routes.dashboard_routes import dashboard_bp
from app.routes.estudiantes_routes import estudiantes_bp

# Configurar logs
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s")

def create_app():
    app = Flask(__name__)
    CORS(app)
    
    # Swagger (Documentación OpenAPI)
    app.config['SWAGGER'] = {
        'title': 'Visons Analytics API',
        'uiversion': 3
    }
    Swagger(app)
    
    # Inicializar Caché
    cache.init_app(app)

    # Inicializar Base de Datos MongoDB
    mongo_db.connect()

    # Registrar rutas (Blueprints)
    app.register_blueprint(dashboard_bp, url_prefix='/api/v1/analytics/dashboard')
    app.register_blueprint(ventas_bp, url_prefix='/api/v1/analytics/ventas')
    app.register_blueprint(envios_bp, url_prefix='/api/v1/analytics/envios')
    app.register_blueprint(calidad_bp, url_prefix='/api/v1/analytics/calidad')
    app.register_blueprint(inventario_bp, url_prefix='/api/v1/analytics/inventario')
    app.register_blueprint(clientes_bp, url_prefix='/api/v1/analytics/clientes')
    app.register_blueprint(proveedores_bp, url_prefix='/api/v1/analytics/proveedores')
    app.register_blueprint(trazabilidad_bp, url_prefix='/api/v1/analytics/trazabilidad')
    app.register_blueprint(audit_bp, url_prefix='/api/v1/analytics/audit')
    app.register_blueprint(estudiantes_bp, url_prefix='/api/v1/analytics/estudiantes')

    # Manejo Global de Errores
    @app.errorhandler(Exception)
    def handle_global_error(error):
        logging.error(f"Excepción no manejada: {str(error)}", exc_info=True)
        # Ocultar el detalle del error en producción por seguridad
        detalle = str(error) if settings.FLASK_DEBUG else "Ocurrió un error inesperado."
        return jsonify({
            "success": False,
            "message": "Error Interno del Servidor",
            "error_detail": detalle
        }), 500

    @app.errorhandler(404)
    def handle_404_error(error):
        return jsonify({
            "success": False,
            "message": "El recurso solicitado no existe"
        }), 404

    from app.utils.response import success_response

    @app.route("/health")
    def health():
        """
        Endpoint de salud del backend analítico
        ---
        tags:
          - General
        responses:
          200:
            description: Estado del servidor
        """
        return success_response(data={"status": "ok"}, message="Backend analítico funcionando correctamente")

    return app

if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=settings.FLASK_PORT, debug=settings.FLASK_DEBUG)
