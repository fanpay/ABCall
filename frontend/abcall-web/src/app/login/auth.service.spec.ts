import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment.development';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    // Crear un mock para el router
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // Importamos el HttpClientTestingModule
      providers: [
        AuthService,
        { provide: Router, useValue: routerMock } // Proveemos el mock del Router
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Verificar que no hay peticiones abiertas después de cada prueba
    httpMock.verify();
  });

  it('should call validateLogin and store the token', async () => {
    const mockResponse = { token: 'fake-token' }; // Respuesta mockeada
    const loginData = { username: 'test', password: 'password' };

    // Llamamos al método validateLogin
    const loginPromise = service.validateLogin(loginData);

    // Interceptamos la solicitud y respondemos con la respuesta mockeada
    const req = httpMock.expectOne(`${environment.baseUrlUsers}/auth`);
    expect(req.request.method).toBe('POST'); // Verificamos que el método sea POST
    req.flush(mockResponse);  // Respondemos a la solicitud con la respuesta mockeada

    await loginPromise; // Esperamos que la promesa se resuelva

    // Verificamos que el token se ha almacenado en el localStorage
    expect(localStorage.getItem('token')).toBe('fake-token');
    expect(service.isAuthenticated).toBeTrue(); // Verificamos que el estado de autenticación sea correcto

    // Verificamos que no hay más peticiones abiertas
    httpMock.verify();
  });

  it('should call getMeInfo and store user data', async () => {
    const mockResponse = { user: { id: 1, name: 'John Doe' } }; // Respuesta mockeada
    const token = 'fake-token';

    // Llamamos al método getMeInfo
    const getMeInfoPromise = service.getMeInfo(token);

    // Interceptamos la solicitud GET y respondemos con la respuesta mockeada
    const req = httpMock.expectOne(`${environment.baseUrlUsers}/me`);
    expect(req.request.method).toBe('GET'); // Verificamos que el método sea GET
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`); // Verificamos que el token esté presente en los headers
    req.flush(mockResponse);  // Respondemos a la solicitud con la respuesta mockeada

    await getMeInfoPromise; // Esperamos que la promesa se resuelva

    // Verificamos que la información del usuario se ha almacenado en localStorage
    expect(localStorage.getItem('meInfo')).toBe(JSON.stringify(mockResponse));

    // Verificamos que no hay más peticiones abiertas
    httpMock.verify();
  });

  it('should call logout and clear localStorage', () => {
    // Simulamos el logout
    service.logout();

    // Verificamos que los valores de localStorage han sido eliminados
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('meInfo')).toBeNull();
    expect(service.isAuthenticated).toBeFalse();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });
});
