from ..extensions import cache

def clear_incident_cache(incident_id):
    cache.delete(f'incident_{incident_id}')
