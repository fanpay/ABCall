import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

// Mock del Router
class RouterMock {
  navigate = jasmine.createSpy('navigate');
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock: RouterMock;

  // Configuración inicial del TestBed
  beforeEach(() => {
    routerMock = new RouterMock();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: routerMock }  // Mock del Router
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verifica que no queden peticiones HTTP pendientes
    localStorage.clear(); // Limpiar el localStorage después de cada prueba
  });

  // 1. **Prueba para el método validateLogin**
  it('should call validateLogin and store the token', async () => {
    const mockLoginData = { username: 'test', password: 'password' };
    const mockResponse = { token: 'test-token' };

    // Ejecutar el método
    await service.validateLogin(mockLoginData);

    // Verificamos la respuesta
    expect(localStorage.getItem('token')).toBe('test-token');
    expect(service.isAuthenticated).toBeTrue();

    // Simulamos la petición HTTP
    const req = httpMock.expectOne(`${service['apiUrl']}/auth`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  // 2. **Prueba para el método getMeInfo**
  it('should call getMeInfo and store user data', async () => {
    const mockToken = 'test-token';
    const mockUserData = { username: 'test', email: 'test@example.com' };

    // Ejecutar el método
    await service.getMeInfo(mockToken);

    // Verificamos la respuesta
    expect(localStorage.getItem('meInfo')).toBe(JSON.stringify(mockUserData));

    // Simulamos la petición HTTP
    const req = httpMock.expectOne(`${service['apiUrl']}/me`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(mockUserData);
  });

  // 3. **Prueba para el método logout**
  it('should call logout and clear localStorage', () => {
    // Configuramos el estado inicial
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('meInfo', JSON.stringify({ username: 'test' }));
    service.isAuthenticated = true;

    // Ejecutamos el método logout
    service.logout();

    // Verificamos el resultado
    expect(service.isAuthenticated).toBeFalse();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('meInfo')).toBeNull();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  // 4. **Prueba para el método isActive**
  it('should call isActive and navigate if not authenticated', () => {
    // Simulamos que no estamos autenticados
    service.isAuthenticated = false;

    // Ejecutamos el método
    service.isActive();

    // Verificamos que router.navigate fue llamado
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should not call navigate if authenticated', () => {
    // Simulamos que estamos autenticados
    service.isAuthenticated = true;

    // Ejecutamos el método
    service.isActive();

    // Verificamos que router.navigate no fue llamado
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
