import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from '../shared/dashboard/dashboard';
import { AmbulanceControl } from './ambulance-control/ambulance-control';

const routes: Routes = [
  {
    path: '',  // When /admin is loaded
    component: DashboardComponent,
    children: [
      { path: 'ambulance-control', component: AmbulanceControl },
      {
        path: 'traffic-control',
        loadComponent: () =>
          import('./traffic-signal/traffic-signal').then((m) => m.TrafficSignal)
      },
    
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
