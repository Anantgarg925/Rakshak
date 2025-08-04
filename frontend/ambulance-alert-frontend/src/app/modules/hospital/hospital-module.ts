import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HospitalRoutingModule } from './hospital-routing-module';
import { BedStatus } from './bed-status/bed-status';
import { HospitalReadiness } from './hospital-readiness/hospital-readiness';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HospitalRoutingModule,BedStatus,HospitalReadiness
  ]
})
export class HospitalModule { }
