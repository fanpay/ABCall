import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public isAuthenticated = false;
  private apiUrl: string = environment.backendUser;
  private authInfo: any;
  public meInfo!: any;

  constructor(
    private router: Router,
    private http: HttpClient
  ) {
    const token = localStorage.getItem('token');
    if (token) {
      this.authInfo = { token };
      this.isAuthenticated = true;
    }
  }

  async validateLogin(dataLogin: any) {
    try {
      const res: any = await this.http.post(this.apiUrl + "/auth", dataLogin).toPromise();
      this.authInfo = res;
      console.log(this.authInfo);
      localStorage.setItem('token', this.authInfo.token);
      this.isAuthenticated = true;
      return this.authInfo;
    } catch (error) {
      console.error('Error during login:', error);
      throw error;
    }
  }

  getAuthInfo() {
    return this.authInfo;
  }

  setAuthInfo(authInfo: any) {
    this.authInfo = authInfo;
  }

  async getMeInfo(token: string) {
    try {
      const res: any = await this.http.get(this.apiUrl + "/me", {
        headers: {'Authorization': 'Bearer ' + token}
      }).toPromise();
      this.meInfo = res;
      console.log(this.meInfo);
      localStorage.setItem('meInfo', JSON.stringify(this.meInfo));
      return this.meInfo;
    } catch (error) {
      console.error('Error fetching user info:', error);
      throw error;
    }
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

  getLoggedUser() {
    return JSON.parse(localStorage.getItem('meInfo') || '{}');
  }

  getToken(): string {
  if (!this.authInfo?.token) {
    throw new Error('Token no disponible. El usuario no está autenticado.');
  }
  return this.authInfo.token;
}

}
