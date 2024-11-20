import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Incident } from '../user/incident';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgentService {

  private incidentApiUrl: string = environment.backendIncidents;

  constructor(
    private http: HttpClient
  ) { }

  getIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.incidentApiUrl).pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

  getMetrics(): Observable<any> {
    return this.http.get<any>(this.incidentApiUrl + '/metrics').pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

  getIncidentsByRange(start: string, end: string): Observable<Incident[]> {
    const params = { startDate: start, endDate: end };
    return this.http.get<Incident[]>(this.incidentApiUrl + '/range', { params }).pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

}
