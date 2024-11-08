import { Component, OnInit } from '@angular/core';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  subMenuText = 'Administrador';
  subMenuImage = 'https://cdn-icons-png.freepik.com/512/15762/15762640.png?ga=GA1.1.378508795.1727907287';

  showPlan = false;
  showCreateAgent = false;
  showBack = false;
  showMenu = true;

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.isActive();
  }

  showPlanComponent() {
    this.subMenuText = 'Plan';
    this.subMenuImage = 'https://cdn-icons-png.freepik.com/512/9577/9577437.png';

    this.showPlan = true;
    this.showBack = true;
    this.showCreateAgent = false;
    this.showMenu = false
  }

  showCreateAgentComponent() {
    this.subMenuText = 'Crear Agente';
    this.subMenuImage = 'https://cdn-icons-png.freepik.com/512/6144/6144457.png?ga=GA1.1.162831122.1727907937';

    this.showPlan = false;
    this.showBack = true;
    this.showCreateAgent = true;
    this.showMenu = false;
  }

  showBackOption() {
    this.subMenuText = 'Administrador';
    this.subMenuImage = 'https://cdn-icons-png.freepik.com/512/15762/15762640.png?ga=GA1.1.378508795.1727907287';

    this.showPlan = false;
    this.showCreateAgent = false;
    this.showBack = false;
    this.showMenu = true;
  }

}
