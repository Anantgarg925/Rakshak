import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing-module';
import { AmbulanceControl } from './ambulance-control/ambulance-control';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AdminRoutingModule,
    AmbulanceControl
  ]
})
export class AdminModule { }
