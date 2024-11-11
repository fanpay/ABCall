import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { Plan } from './plan';

@Injectable({
  providedIn: 'root'
})
export class PlanService {

  private apiUrl: string = environment.backendPlan;

  constructor(
    private http: HttpClient
  ) { }

  getActualPlan(): Observable<Plan> {
    return this.http.get<Plan>(this.apiUrl).pipe(
      catchError(err => throwError(() => new Error(JSON.stringify(err, null, 2))))
    );
  }  

  updatePlan(plan: Plan): Observable<Plan> {
    return this.http.put<Plan>(this.apiUrl, plan).pipe(
      catchError(err => throwError(() => new Error(JSON.stringify(err, null, 2))))
    );
  }


}
