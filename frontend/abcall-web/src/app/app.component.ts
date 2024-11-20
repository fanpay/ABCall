import { Component, OnInit } from '@angular/core';
import { AuthService } from './login/auth.service';
import { AppService } from './app.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{

  title = 'abcall-web';
  locationInfo!: any;

  constructor(
    public authService: AuthService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.locationInfo = "";
    this.getUserLocation();
  }

  logout() {
    this.authService.logout();
  }

  getUserLocation(){
    this.appService.getUserLocation().subscribe(
      (data) => {
        console.log(data.countryName);
        console.log(JSON.stringify(data, null, 2));
        this.locationInfo = data;
      },
      (err) => {
        console.log(err);
      }
    );
  }


}
