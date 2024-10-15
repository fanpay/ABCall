/* tslint:disable:no-unused-variable */
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AgentService } from './agent.service';
import { Agent } from './agent';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';

describe('AgentService', () => {
  let service: AgentService;
  let httpMock: HttpTestingController;

  const baseUrl = environment.backend;
  const mockAgent: Agent = {
    username: 'JohnDoe',
    password: 'Password123',
    email: 'john.doe@example.com',
    dni: '12345678',
    fullName: 'John Doe',
    phoneNumber: '1234567890',
    role: 'agent'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],  // Importamos el módulo de testing HTTP
      providers: [AgentService]
    });
    service = TestBed.inject(AgentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();  // Verifica que no haya solicitudes pendientes
  });

  // 1. **Verificar que el método createAgent hace una solicitud POST correctamente**
  it('should create an agent and return the agent data', () => {
    const expectedResponse = { ...mockAgent };

    service.createAgent(mockAgent).subscribe((res) => {
      expect(res).toEqual(expectedResponse);  // Verifica que la respuesta sea igual a la mock
    });

    const req = httpMock.expectOne(`${baseUrl}`);
    expect(req.request.method).toBe('POST');  // Verifica que la solicitud es un POST
    expect(req.request.body).toEqual(mockAgent);  // Verifica que el cuerpo de la solicitud sea el agente correcto
    req.flush(expectedResponse);  // Simula una respuesta del servidor
  });

  // 2. **Verificar el manejo de errores en createAgent**
  it('should handle error when the request fails', () => {
    const errorMessage = 'Http failure response for http://localhost:9878/users: 400 Bad Request';
  
    service.createAgent(mockAgent).subscribe({
      next: () => fail('should have failed with a 400 error'),
      error: (error) => {
        expect(error.message).toContain(errorMessage);
      }
    });
  
    const req = httpMock.expectOne(`${baseUrl}`);
    expect(req.request.method).toBe('POST');
    req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });  // Simula un error con código 400
  });  

});
