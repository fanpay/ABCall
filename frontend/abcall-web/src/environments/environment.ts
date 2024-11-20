/**
 * Production environment configuration
 */
const backendUser = 'https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/users';
const backendIncidents = 'https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/incidents';
const backendChatbot = 'https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/chat';
const backendPlan = 'https://misw-2024-api-gateway-pf-96b62bvm.uc.gateway.dev/plan';

export const environment = {
 production: true,
 backendUser,
 backendIncidents,
 backendChatbot,
 backendPlan
};
