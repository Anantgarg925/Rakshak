import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficSignal } from './traffic-signal';

describe('TrafficSignal', () => {
  let component: TrafficSignal;
  let fixture: ComponentFixture<TrafficSignal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrafficSignal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrafficSignal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
