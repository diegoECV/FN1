def serialize_clientes_resumen(model) -> dict:
    return {
        "total_clientes_registrados": model.total_clientes_registrados
    }
