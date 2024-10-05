import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  public isAuthenticated = false;
  private apiUrl: string = environment.baseUrlUsers;
  private authInfo: any;
  public meInfo!: any;

  constructor(
    private router: Router,
    private http: HttpClient
  ) { }

  async validateLogin(dataLogin: any) {
    this.http.post(this.apiUrl+"/auth", dataLogin)
    .subscribe(async (res: any) => {
      this.authInfo = res;
      console.log(this.authInfo);
      localStorage.setItem('token', this.authInfo.token);
      this.isAuthenticated = true;
      await this.getMeInfo(this.authInfo.token);
    });
  }

  async getMeInfo(token: string) {
    this.http.get(this.apiUrl+"/me", {
      headers: {'Authorization': 'Bearer ' + token}}
    ).subscribe((res: any) => {
      this.meInfo = res;
      console.log(this.meInfo);
      localStorage.setItem('meInfo', JSON.stringify(this.meInfo));
    });

    console.log(this.meInfo);
  }

  logout() {
    this.isAuthenticated = false;
    this.router.navigate(['']);
    localStorage.removeItem('token');
    localStorage.removeItem('meInfo');
  }

  isActive() { 
    if (!this.isAuthenticated) {
      this.router.navigate(['']);
    }
  }

}
