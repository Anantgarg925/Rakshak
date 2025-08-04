import { Routes } from '@angular/router';
import { DashboardComponent } from './modules/shared/dashboard/dashboard';

export const routes: Routes = [
  {
    path: 'admin',
    loadChildren: () =>
      import('./modules/admin/admin-module').then((m) => m.AdminModule),
  },
  {
    path: 'hospital',
    loadChildren: () =>
      import('./modules/hospital/hospital-module').then((m) => m.HospitalModule),
  },
  {
    path: '',
    redirectTo: 'admin/ambulance-control',
    pathMatch: 'full',
  },
  { path:'dashboard',component:DashboardComponent},
  {
    path:'driver-alert',
    loadComponent:() => 
      import('./modules/driver-alert/driver-alert/driver-alert.component').then(m=>m.DriverAlertComponent)
        
    },
  
];
