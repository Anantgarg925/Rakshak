import { Component } from '@angular/core';
import { HospitalModule } from '../hospital-module';
import { BedStatus } from '../bed-status/bed-status';

@Component({
  selector: 'app-hospital-readiness',
  imports: [HospitalModule,BedStatus],
  standalone:true,
  templateUrl: './hospital-readiness.html',
  styleUrl: './hospital-readiness.css'
})
export class HospitalReadiness {
  patients=[
    {name:'Unknown Male',age:34,injury:'Head Trauma',ETA:'3 min',staus:'Incoming'},
    {name:'Female - Unconsious',age:28,injury:'Chest Injury',ETA:'8 min',staus:'Incoming'},
  ];

  
}
