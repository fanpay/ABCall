import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlPanelComponent } from './control-panel.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AgentService } from '../agent.service';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ControlPanelComponent', () => {
  let component: ControlPanelComponent;
  let fixture: ComponentFixture<ControlPanelComponent>;
  let agentService: AgentService;
  let httpMock: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ControlPanelComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [FormBuilder, AgentService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ControlPanelComponent);
    component = fixture.componentInstance;
    agentService = TestBed.inject(AgentService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty dates', () => {
    expect(component.rangeForm).toBeDefined();
    expect(component.rangeForm.get('startDate')?.value).toBe('');
    expect(component.rangeForm.get('endDate')?.value).toBe('');
  });

  it('should set today as the maximum date', () => {
    const todayDate = new Date().toISOString().split('T')[0];
    expect(component.today).toBe(todayDate);
  });

  it('should validate that start date is not after end date', () => {
    component.rangeForm.setValue({
      startDate: '2023-12-31',
      endDate: '2023-12-01'
    });
    component.validateDates();
    expect(component.dateError).toBeTrue();
  });

  it('should not set dateError if start date is before end date', () => {
    component.rangeForm.setValue({
      startDate: '2023-12-01',
      endDate: '2023-12-31'
    });
    component.validateDates();
    expect(component.dateError).toBeFalse();
  });

  it('should call getIncidentsByRange and reset form on valid search', () => {
    const mockIncidents = [{ id: 1, userId: 'user1', subject: 'Test', description: 'Desc', status: 'Open', originType: 'Email', solution: '', creationDate: new Date(), updateDate: new Date() }];
    spyOn(agentService, 'getIncidentsByRange').and.returnValue(of(mockIncidents));
  
    component.rangeForm.setValue({
      startDate: '2023-01-01',
      endDate: '2023-12-31'
    });
  
    component.searchIncidents();
  
    // Asegúrate de que las fechas pasadas a la función incluyan las horas correctas
    expect(agentService.getIncidentsByRange).toHaveBeenCalledWith(
      '2023-01-01T00:00:00', 
      '2023-12-31T23:59:59'
    );
    expect(component.incidentsList).toEqual(mockIncidents);
    expect(component.rangeForm.get('startDate')?.value).toBeNull();
    expect(component.rangeForm.get('endDate')?.value).toBeNull();
  });
  

  it('should not call getIncidentsByRange if form is invalid', () => {
    spyOn(agentService, 'getIncidentsByRange');

    component.rangeForm.setValue({
      startDate: '',
      endDate: ''
    });

    component.searchIncidents();

    expect(agentService.getIncidentsByRange).not.toHaveBeenCalled();
  });
});
