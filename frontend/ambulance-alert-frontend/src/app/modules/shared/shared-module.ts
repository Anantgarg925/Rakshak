import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SosAlertService } from './sos-alert.service';
import { Navbar } from './navbar/navbar.component';
import {Footer } from './footer/footer.component';


@NgModule({
  imports: [
    CommonModule,Navbar,Footer
  ],
  providers:[SosAlertService],
  exports:[Navbar,Footer]
})
export class SharedModule { }
