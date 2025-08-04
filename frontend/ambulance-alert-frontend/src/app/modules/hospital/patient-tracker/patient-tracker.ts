import { Component } from '@angular/core';
import { Navbar } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-patient-tracker',
  imports: [Navbar],
  templateUrl: './patient-tracker.html',
  styleUrl: './patient-tracker.css'
})
export class PatientTracker {
  patients = [
    {
      name: 'John Doe',
      ambulanceId: 'AMB-001',
      status: 'In Transit',
      eta: 5,
      heartRate: 80,
      oxygen: 98
    },
    {
      name: 'Jane Smith',
      ambulanceId: 'AMB-002',
      status: 'In Transit',
      eta: 12,
      heartRate: 76,
      oxygen: 97
    }
  ];

}
