import { Component, OnInit } from '@angular/core';
import { SosAlertService } from '../../shared/sos-alert.service';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { JsonPipe } from '@angular/common';
import { Navbar } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-ambulance-control',
  imports: [CommonModule,JsonPipe,Navbar],
  templateUrl: './ambulance-control.html',
  styleUrl: './ambulance-control.css',
  standalone:true,
})
export class AmbulanceControl implements OnInit{
  accidentReports :any[]=[];

  sosMessage :string | null=null;

  constructor(private sosAlertService:SosAlertService,
    private http:HttpClient
  ){}


  ngOnInit(): void {
    this.fetchAccidentReports();

     this.sosAlertService.alert$.subscribe((alert:any)=>{
      try{
        const parsed=typeof alert ==='string' ? JSON.parse(alert):alert;
        this.accidentReports.unshift(parsed);
        console.log("Real-time alert received",parsed);
      }catch(e){
        console.error("Invalid SOS alert received",alert);
      }
     });

      

  }

  fetchAccidentReports():void{
    this.http.get<any[]>("http://localhost:8080/api/accident/reports")
    .subscribe({
      next:(data)=>{
        this.accidentReports =data.reverse();
        console.log("Fetched accident reports : ",data);
      },
      error : (err)=>{
        console.error("Error fetching accident reports:",err);
      }
    })
  }
}
