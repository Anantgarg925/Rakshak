import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AmbulanceControl } from './ambulance-control';

describe('AmbulanceControl', () => {
  let component: AmbulanceControl;
  let fixture: ComponentFixture<AmbulanceControl>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AmbulanceControl]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AmbulanceControl);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
