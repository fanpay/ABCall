/**
 * Pre-production & Development environment configuration
 */

const backendUser = 'http://localhost:9876/users';
const backendIncidents = 'http://localhost:9877/incidents';
const backendChatbot = 'http://localhost:9878/chat';
const backendPlan = 'http://localhost:9877/plan';

export const environment = {
 production: false,
 backendUser,
 backendIncidents,
 backendChatbot,
 backendPlan
};
