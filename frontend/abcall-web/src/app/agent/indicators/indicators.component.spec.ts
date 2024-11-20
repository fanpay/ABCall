import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Importar el módulo
import { AgentService } from '../agent.service'; // Asegúrate de que este servicio esté importado
import { IndicatorsComponent } from './indicators.component';

describe('IndicatorsComponent', () => {
  let component: IndicatorsComponent;
  let fixture: ComponentFixture<IndicatorsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ IndicatorsComponent ],
      imports: [ HttpClientTestingModule ],  // Agregar el módulo HttpClientTestingModule
      providers: [ AgentService ]            // Asegúrate de que AgentService esté en providers
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicatorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Puedes agregar más pruebas aquí
});
