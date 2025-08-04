import { Component,OnInit } from '@angular/core';

@Component({
  selector: 'app-driver-alert',
  imports: [],
  standalone:true,
  templateUrl: './driver-alert.html',
  styleUrl: './driver-alert.css'
})
export class DriverAlertComponent implements OnInit {
    alertMessage:string ="No active ambulance alerts nearby .";

    constructor(){}

    ngOnInit(): void {
        setTimeout(()=>{
          this.alertMessage="Ambulance approaching nearby! Please give way.";
        },4000);
    }
}
