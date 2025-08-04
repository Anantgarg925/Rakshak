import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientTracker } from './patient-tracker';

describe('PatientTracker', () => {
  let component: PatientTracker;
  let fixture: ComponentFixture<PatientTracker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientTracker]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientTracker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
