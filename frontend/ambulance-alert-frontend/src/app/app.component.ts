import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './modules/shared/navbar/navbar.component';
import { Footer } from './modules/shared/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,Navbar,Footer],
  templateUrl: "./app.component.html",        
  styleUrls: ['./app.component.css']           
})
export class App {
  protected title = 'ambulance-alert-frontend';
}
