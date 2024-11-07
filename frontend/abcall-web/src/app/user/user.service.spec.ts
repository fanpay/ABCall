import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { Incident } from './incident';
import { environment } from '../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create an incident and return the incident data', () => {
    const mockIncident: Incident = {
      id: 1,
      subject: 'Test Incident',
      description: 'Test Description',
      userId: '123',
      originType: 'web',
      status: 'open',
      solution: '',
      creationDate: new Date(),
      updateDate: new Date()
    };

    service.createIncident(mockIncident).subscribe((res) => {
      expect(res).toEqual(mockIncident);
    });

    const req = httpMock.expectOne(environment.backendIncidents);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockIncident);
    req.flush(mockIncident);
  });

  it('should handle error when creating an incident', () => {
    const mockIncident: Incident = {
      id: 1,
      subject: 'Test Incident',
      description: 'Test Description',
      userId: '123',
      originType: 'web',
      status: 'open',
      solution: '',
      creationDate: new Date(),
      updateDate: new Date()
    };

    service.createIncident(mockIncident).subscribe({
      next: () => fail('should have failed with a 400 error'),
      error: (error) => {
        expect(error.message).toContain('Error con el servicio');
      }
    });

    const req = httpMock.expectOne(environment.backendIncidents);
    expect(req.request.method).toBe('POST');
    req.flush('Error con el servicio', { status: 400, statusText: 'Bad Request' });
  });

  it('should get incidents by user and return the incident list', () => {
    const mockIncidents: Incident[] = [
      {
        id: 1,
        subject: 'Incident 1',
        description: 'Description 1',
        userId: '123',
        originType: 'web',
        status: 'open',
        solution: '',
        creationDate: new Date(),
        updateDate: new Date()
      },
      {
        id: 2,
        subject: 'Incident 2',
        description: 'Description 2',
        userId: '123',
        originType: 'web',
        status: 'closed',
        solution: 'Solution 2',
        creationDate: new Date(),
        updateDate: new Date()
      }
    ];

    service.getIncidentByUser('123').subscribe((res) => {
      expect(res).toEqual(mockIncidents);
    });

    const req = httpMock.expectOne(`${environment.backendIncidents}?userId=123`);
    expect(req.request.method).toBe('GET');
    req.flush(mockIncidents);
  });

  it('should handle error when getting incidents by user', () => {
    service.getIncidentByUser('123').subscribe({
      next: () => fail('should have failed with a 400 error'),
      error: (error) => {
        expect(error.message).toContain('Error con el servicio');
      }
    });

    const req = httpMock.expectOne(`${environment.backendIncidents}?userId=123`);
    expect(req.request.method).toBe('GET');
    req.flush('Error con el servicio', { status: 400, statusText: 'Bad Request' });
  });

  it('should send a message to the chatbot and return the response', () => {
    const mockResponse = { message: 'Chatbot response' };
    const message = 'Hello';
    const originType = 'web';
    const userId = '123';
    const token = 'test-token';

    service.sendMessageToChatbot(message, originType, userId, token).subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(environment.backendChatbot);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    expect(req.request.body).toEqual({ message, originType, userId });
    req.flush(mockResponse);
  });

  it('should handle error when sending a message to the chatbot', () => {
    const message = 'Hello';
    const originType = 'web';
    const userId = '123';
    const token = 'test-token';

    service.sendMessageToChatbot(message, originType, userId, token).subscribe({
      next: () => fail('should have failed with a 400 error'),
      error: (error) => {
        expect(error.message).toContain('No se puede comunicar con el servicio del chatbot');
      }
    });

    const req = httpMock.expectOne(environment.backendChatbot);
    expect(req.request.method).toBe('POST');
    req.flush('No se puede comunicar con el servicio del chatbot', { status: 400, statusText: 'Bad Request' });
  });
});
