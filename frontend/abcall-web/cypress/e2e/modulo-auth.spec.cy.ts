import { faker } from '@faker-js/faker';

describe('Pruebas del módulo de autenticación', () => {
  beforeEach(() => {
    cy.visit('http://localhost:4200');
    cy.get('#username').clear();
    cy.get('#password').clear();
  });

  const users: { [key in 'admin' | 'agent' | 'user']: { username: string; password: string; redirect: string } } = {
    admin: { username: 'admin', password: 'admin', redirect: '/admin' },
    agent: { username: 'agent', password: 'agent', redirect: '/agent' },
    user: { username: 'user', password: 'user', redirect: '/user' },
  };

  for (const role in users) {
    it(`Inicia sesión correctamente como un ${role} y hace logout.`, () => {
      const { username, password, redirect } = users[role as 'admin' | 'agent' | 'user'];

      cy.get('#username').type(username);
      cy.get('#password').type(password);
      cy.get('#login-form').submit();
      cy.wait(5000); // Esperar para que se elimine el mensaje de bienvenida

      cy.url().should('include', redirect);


      // Validar que se redirige a la página de inicio de sesión
      cy.get('#btn-logout').click();
      cy.url().should('eq', 'http://localhost:4200/');
    });
  }

  it('Muestra un mensaje para un usuario no registrado', () => {
    const invalidUser = {
      username: 'usuarioInvalido',
      password: 'contraseñaIncorrecta'
    };

    cy.get('#username').type(invalidUser.username);
    cy.get('#password').type(invalidUser.password);
    cy.get('#login-form').submit();

    cy.contains('Verifique sus datos').should('be.visible');
  });

  it('Registra un nuevo usuario correctamente', () => {
    cy.wait(2000); // Esperar un poco para que cargue la página de administración
    cy.get('a').contains('¿Eres un usuario nuevo?').click();

    const newUser = {
      username: `Cypress_${faker.internet.userName()}`,
      password: faker.internet.password(),
      email: faker.internet.email(),
      dni: faker.number.int({ min: 10000000, max: 99999999 }).toString(),
      fullName: `Cypress ${faker.name.fullName()}`,
      phoneNumber: faker.phone.number()
    };

    // Completar el formulario de registro con los datos generados
    cy.get('#register-form #username').type(newUser.username);
    cy.get('#register-form #password').type(newUser.password);
    cy.get('#register-form #email').type(newUser.email);
    cy.get('#register-form #dni').type(newUser.dni);
    cy.get('#register-form #fullName').type(newUser.fullName);
    cy.get('#register-form #phoneNumber').type(newUser.phoneNumber);

    // Enviar el formulario de registro
    cy.get('#register-form #btn-create-agent').click();

    // Verificar que se haya creado el usuario correctamente
    cy.contains('fue creado!').should('be.visible');
  });
});
