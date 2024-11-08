import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: routerMock }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should validate login and store token', async () => {
    const mockResponse = { token: 'test-token' };
    const dataLogin = { username: 'test', password: 'test' };

    const loginPromise = service.validateLogin(dataLogin);

    const req = httpMock.expectOne(`${environment.backendUser}/auth`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    const res = await loginPromise;
    expect(res).toEqual(mockResponse);
    expect(service.isAuthenticated).toBeTrue();
    expect(service.getAuthInfo().token).toBe('test-token');
    expect(localStorage.getItem('token')).toBe('test-token');
  });

  it('should handle login error', async () => {
    const dataLogin = { username: 'test', password: 'test' };

    const loginPromise = service.validateLogin(dataLogin).catch(error => {
      expect(error).toBeTruthy();
      expect(service.isAuthenticated).toBeFalse();
    });

    const req = httpMock.expectOne(`${environment.backendUser}/auth`);
    expect(req.request.method).toBe('POST');
    req.error(new ErrorEvent('Network error'));

    await loginPromise;
  });

  it('should logout and clear localStorage', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.setItem('meInfo', JSON.stringify({ id: '123' }));
    service.isAuthenticated = true;

    service.logout();

    expect(service.isAuthenticated).toBeFalse();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('meInfo')).toBeNull();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should get logged user from localStorage', () => {
    const mockMeInfo = { id: '123', name: 'Test User' };
    localStorage.setItem('meInfo', JSON.stringify(mockMeInfo));

    const loggedUser = service.getLoggedUser();
    expect(loggedUser).toEqual(mockMeInfo);
  });

  it('should throw error if token is not available', () => {
    service.setAuthInfo(null);
    expect(() => service.getToken()).toThrow(new Error('Token no disponible. El usuario no está autenticado.'));
  });

  it('should return token if available', () => {
    service.setAuthInfo({ token: 'test-token' });
    expect(service.getToken()).toBe('test-token');
  });

  it('should navigate to home if not authenticated', () => {
    service.isAuthenticated = false;
    service.isActive();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('should not navigate if authenticated', () => {
    service.isAuthenticated = true;
    service.isActive();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
