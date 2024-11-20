import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PlanComponent } from './plan.component';
import { PlanService } from './plan.service';
import { of } from 'rxjs';
import { Plan } from './plan';

describe('PlanComponent', () => {
  let component: PlanComponent;
  let fixture: ComponentFixture<PlanComponent>;
  let httpMock: HttpTestingController;
  let planService: PlanService;
  let toastrService: ToastrService;

  const mockPlan: Plan = new Plan(1, "Mock Plan");

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ PlanComponent ],
      imports: [
        ToastrModule.forRoot(),
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [ FormBuilder, PlanService ]
    })
    .compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    planService = TestBed.inject(PlanService);
    toastrService = TestBed.inject(ToastrService);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form and fetch the actual plan on ngOnInit', () => {
    spyOn(planService, 'getActualPlan').and.returnValue(of(mockPlan));
    component.ngOnInit();

    expect(component.planForm).toBeTruthy();
    expect(planService.getActualPlan).toHaveBeenCalled();
    expect(component.actualPlan).toEqual(mockPlan);
  });

  it('should fetch the actual plan in getActualPlan method', () => {
    spyOn(planService, 'getActualPlan').and.returnValue(of(mockPlan));
    component.getActualPlan();

    expect(planService.getActualPlan).toHaveBeenCalled();
    expect(component.actualPlan).toEqual(mockPlan);
  });

  it('should update the plan and show success message in savePlan', () => {
    spyOn(planService, 'updatePlan').and.returnValue(of(mockPlan));
    spyOn(toastrService, 'success');
    component.savePlan(mockPlan);

    expect(planService.updatePlan).toHaveBeenCalledWith(mockPlan);
    expect(toastrService.success).toHaveBeenCalledWith("Confirmation", "El plan fue actualizado!");
    expect(component.actualPlan).toEqual(mockPlan);
  });
});
