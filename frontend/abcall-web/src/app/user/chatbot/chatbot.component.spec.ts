import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChatbotComponent } from './chatbot.component';
import { UserService } from '../user.service';
import { AuthService } from '../../login/auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('ChatbotComponent', () => {
  let component: ChatbotComponent;
  let fixture: ComponentFixture<ChatbotComponent>;
  let userServiceMock: jasmine.SpyObj<UserService>;
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    userServiceMock = jasmine.createSpyObj('UserService', ['sendMessageToChatbot']);
    authServiceMock = jasmine.createSpyObj('AuthService', ['getToken', 'getLoggedUser']);

    await TestBed.configureTestingModule({
      declarations: [ChatbotComponent],
      imports: [HttpClientTestingModule, FormsModule],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatbotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should send a message and receive a response', () => {
    const mockResponse = { message: 'Respuesta del chatbot' };
    userServiceMock.sendMessageToChatbot.and.returnValue(of(mockResponse));
    authServiceMock.getToken.and.returnValue('test-token');
    authServiceMock.getLoggedUser.and.returnValue({ id: '123' });

    component.userMessage = 'Hola';
    component.sendMessage();

    expect(userServiceMock.sendMessageToChatbot).toHaveBeenCalledWith('Hola', 'web', '123', 'test-token');
    expect(component.messages.length).toBe(2);
    expect(component.messages[0].text).toBe('Hola');
    expect(component.messages[0].isUser).toBeTrue();
    expect(component.messages[1].text).toBe('Respuesta del chatbot');
    expect(component.messages[1].isUser).toBeFalse();
  });

  it('should handle error when sending a message', () => {
    userServiceMock.sendMessageToChatbot.and.returnValue(throwError(() => new Error('Error de red')));
    authServiceMock.getToken.and.returnValue('test-token');
    authServiceMock.getLoggedUser.and.returnValue({ id: '123' });

    component.userMessage = 'Hola';
    component.sendMessage();

    expect(userServiceMock.sendMessageToChatbot).toHaveBeenCalledWith('Hola', 'web', '123', 'test-token');
    expect(component.messages.length).toBe(2);
    expect(component.messages[0].text).toBe('Hola');
    expect(component.messages[0].isUser).toBeTrue();
    expect(component.messages[1].text).toBe('Tu mensaje no ha podido ser enviado. El servicio de chatbot se encuentra temporalmente fuera de servicio.');
    expect(component.messages[1].isUser).toBeFalse();
    expect(component.messages[1].isError).toBeTrue();
  });

  it('should not send a message if userMessage is empty', () => {
    component.userMessage = '   ';
    component.sendMessage();

    expect(userServiceMock.sendMessageToChatbot).not.toHaveBeenCalled();
    expect(component.messages.length).toBe(0);
  });
});
