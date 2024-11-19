import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private apiUrl: string = `https://api.bigdatacloud.net/data/reverse-geocode-client`;

  constructor(
    private http: HttpClient
  ) { }

  getUserLocation(): Observable<any> {
    return this.http.get<any>(this.apiUrl).pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

}
