def serialize_ventas_resumen(model) -> dict:
    return {
        "total_ordenes": model.total_ordenes,
        "ordenes_pagadas": model.ordenes_pagadas,
        "ordenes_pendientes": model.ordenes_pendientes,
        "revenue_usd": model.revenue_usd
    }
