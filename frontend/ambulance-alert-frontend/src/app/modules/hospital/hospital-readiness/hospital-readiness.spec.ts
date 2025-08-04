import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HospitalReadiness } from './hospital-readiness';

describe('HospitalReadiness', () => {
  let component: HospitalReadiness;
  let fixture: ComponentFixture<HospitalReadiness>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HospitalReadiness]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HospitalReadiness);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
