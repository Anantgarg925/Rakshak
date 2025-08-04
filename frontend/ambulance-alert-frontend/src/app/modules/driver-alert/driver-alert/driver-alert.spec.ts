import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverAlertComponent } from './driver-alert.component';

describe('DriverAlert', () => {
  let component: DriverAlertComponent;
  let fixture: ComponentFixture<DriverAlertComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverAlertComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
