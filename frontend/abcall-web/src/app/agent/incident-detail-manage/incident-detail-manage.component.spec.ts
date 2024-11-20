import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { IncidentDetailManageComponent } from './incident-detail-manage.component';

describe('IncidentDetailManageComponent', () => {
  let component: IncidentDetailManageComponent;
  let fixture: ComponentFixture<IncidentDetailManageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ IncidentDetailManageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IncidentDetailManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
