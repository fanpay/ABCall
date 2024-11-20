import { faker } from '@faker-js/faker';
import { users } from './test-users';

describe('Pruebas del módulo de agente', () => {
  const { username, password } = users.agent;

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
    cy.get('#card-manage-incident').should('be.visible');
    cy.get('#card-control-plane').should('be.visible');
    cy.get('#card-indicators').should('be.visible');
  });

  it('Verifica que cada tarjeta tenga el contenido correcto', () => {
    cy.get('#card-manage-incident')
      .find('.card-title')
      .should('contain', 'Gestionar incidentes');

    cy.get('#card-control-plane')
      .find('.card-title')
      .should('contain', 'Tablero de control');

    cy.get('#card-indicators')
      .find('.card-title')
      .should('contain', 'Indicadores');
  });

  it('Verifica que al hacer clic en la tarjeta "Gestionar incidentes" se muestre el componente correspondiente', () => {
    cy.get('#card-manage-incident').click();
    cy.get('app-manage-incident').should('be.visible');
  });

  it('Verifica que al hacer clic en la tarjeta "Tablero de control" se muestre el componente correspondiente', () => {
    cy.get('#card-control-plane').click();
    cy.get('app-control-panel').should('be.visible');
  });

  it('Verifica que al hacer clic en la tarjeta "Indicadores" se muestre el componente correspondiente', () => {
    cy.get('#card-indicators').click();
    cy.get('app-indicators').should('be.visible');
  });
});
