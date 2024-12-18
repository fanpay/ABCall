import { Injectable } from '@angular/core';
import { Incident } from './incident';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private incidentApiUrl: string = environment.backendIncidents;
  private chatbotApiUrl: string = environment.backendChatbot;

  constructor(
    private http: HttpClient
  ) { }

  createIncident(newIncident: Incident): Observable<Incident> {
    return this.http.post<Incident>(this.incidentApiUrl, newIncident).pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

  getIncidentByUser(userId: string): Observable<Incident[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<Incident[]>(this.incidentApiUrl , { params }).pipe(
      catchError(err => throwError(() => new Error("Error con el servicio:" + err.message)))
    );
  }

  sendMessageToChatbot(message: string, originType: string, userId: string, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const body = { message, originType, userId };
    return this.http.post<any>(this.chatbotApiUrl, body, { headers }).pipe(
      catchError(err => throwError(() => new Error("No se puede comunicar con el servicio del chatbot:" + err.message)))
    );
  }

}
