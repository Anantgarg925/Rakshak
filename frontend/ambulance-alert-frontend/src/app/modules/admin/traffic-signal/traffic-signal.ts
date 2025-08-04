import { Component } from '@angular/core';

@Component({
  selector: 'app-traffic-signal',
  imports: [],
  standalone:true,
  templateUrl: './traffic-signal.html',
  styleUrl: './traffic-signal.css'
})
export class TrafficSignal {
  signals=[
    {intersection: 'Signal 1 -Sector 10',status:'RED'},
    {intersection: 'Signal 2 - MG Road',status:'GREEN'},
    {intersection: 'Signal 3 - Hospital Square',status:'RED'},
  ];

  toggleSignal(index :number): void{
    this.signals[index].status=this.signals[index].status === 'RED'?'GREEN': 'RED';
  }
}
