import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SosAlertService {
  private alertSubject = new Subject<string>();
  alert$ = this.alertSubject.asObservable();

  triggerAlert(message: string) {
    this.alertSubject.next(message);
  }
}
