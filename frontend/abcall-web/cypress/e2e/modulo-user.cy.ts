import { users } from './test-users';

describe('Pruebas del módulo de usuario', () => {
  const { username, password } = users.user;

  beforeEach(() => {
    cy.visit('http://localhost:4200');
    cy.get('#username').clear();
    cy.get('#password').clear();
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login-form').submit();
    cy.wait(5000);
  });

  it('Validación de las tres opciones para un agente', () => {
    // Verificar que las tres tarjetas estén presentes en la página
    cy.get('#card-create-incident').should('be.visible');
    cy.get('#card-search-incidents').should('be.visible');
    cy.get('#card-chat-bot').should('be.visible');
  });

  it('Verifica que cada tarjeta tenga el contenido correcto', () => {
    cy.get('#card-create-incident')
      .find('.card-title')
      .should('contain', 'Crear incidente');

    cy.get('#card-search-incidents')
      .find('.card-title')
      .should('contain', 'Consultar incidentes');

    cy.get('#card-chat-bot')
      .find('.card-title')
      .should('contain', 'Chatbot');
  });

  it('Verifica que al hacer clic en la tarjeta "Crear incidente" se muestre el componente correspondiente', () => {
    cy.get('#card-create-incident').click();
    cy.get('app-incident-create').should('be.visible');
  });

  it('Verifica que al hacer clic en la tarjeta "Consultar incidentes" se muestre el componente correspondiente', () => {
    cy.get('#card-search-incidents').click();
    cy.get('app-incident-search').should('be.visible');
  });

  it('Verifica que al hacer clic en la tarjeta "Chatbot" se muestre el componente correspondiente', () => {
    cy.get('#card-chat-bot').click();
    cy.get('app-chatbot').should('be.visible');
  });
});
