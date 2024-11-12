/* tslint:disable:no-unused-variable */

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PlanService } from './plan.service';
import { Plan } from './plan';

describe('PlanService', () => {
  let service: PlanService;
  let httpMock: HttpTestingController;
  const mockPlan: Plan = {id: 1, plan: 'Default Plan'};

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PlanService]
    });
    service = TestBed.inject(PlanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the service', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch the actual plan', () => {
    service.getActualPlan().subscribe((plan) => {
      expect(plan).toEqual(mockPlan);
    });

    const req = httpMock.expectOne(service['apiUrl']);
    expect(req.request.method).toBe('GET');
    req.flush(mockPlan);
  });

  it('should update the plan', () => {
    service.updatePlan(mockPlan).subscribe((updatedPlan) => {
      expect(updatedPlan).toEqual(mockPlan);
    });

    const req = httpMock.expectOne(service['apiUrl']);
    expect(req.request.method).toBe('PUT');
    req.flush(mockPlan);
  });
});
