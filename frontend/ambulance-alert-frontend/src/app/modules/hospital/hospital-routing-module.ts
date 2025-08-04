import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BedStatus } from './bed-status/bed-status';
import { PatientTracker } from './patient-tracker/patient-tracker';

const routes: Routes = [
  {path:'bed-status',component: BedStatus},
  {path:'patient-tracker',component:PatientTracker}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HospitalRoutingModule { }
