def serialize_inventario_resumen(model) -> dict:
    return {
        "lotes_en_inventario_mongo": model.lotes_en_inventario_mongo,
        "lotes_produccion_sql": model.lotes_produccion_sql
    }
