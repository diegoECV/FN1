def serialize_proveedores_resumen(model) -> dict:
    return {
        "total_proveedores_activos": model.total_proveedores_activos
    }
