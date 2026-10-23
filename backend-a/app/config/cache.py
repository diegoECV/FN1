from flask_caching import Cache

# Configuramos un caché simple en memoria.
# Los datos vivirán en caché por 300 segundos (5 minutos) por defecto.
cache = Cache(config={
    'CACHE_TYPE': 'SimpleCache',
    'CACHE_DEFAULT_TIMEOUT': 300 
})
