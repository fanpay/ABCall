/**
 * Pre-production environment configuration
 */

const backendUser = 'http://localhost:9876/users';
const backendIncidents = 'http://localhost:9878/users';

export const environment = {
 production: false,
 backendUser,
 backendIncidents
};