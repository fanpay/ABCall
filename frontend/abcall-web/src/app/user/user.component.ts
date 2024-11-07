import { Component, OnInit } from '@angular/core';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  subMenuText: string = 'Usuario';
  subMenuImage: string = 'https://cdn-icons-png.freepik.com/512/477/477804.png?ga=GA1.1.378508795.1727907287';

  showSearchIncident: boolean = false;
  showCreateIncident: boolean = false;
  showChatbot: boolean = false;
  showMenu: boolean = true;
  showBack: boolean = false;

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.isActive();
  }

  showCreateIncidentComponent() {
    this.subMenuText = 'Crear Incidente';
    this.subMenuImage = './assets/images/incidents/submenu_create_incident.png';

    this.showCreateIncident = true;
    this.showSearchIncident = false;
    this.showChatbot = false
    this.showMenu = false;
    this.showBack = true;
  }

  showSearchIncidentComponent() {
    this.subMenuText = 'Consultar Incidentes';
    this.subMenuImage = './assets/images/incidents/submenu_incidents.png';

    this.showCreateIncident = false;
    this.showSearchIncident = true;
    this.showChatbot = false
    this.showMenu = false;
    this.showBack = true;
  }

  showChatbotComponent(){
    this.subMenuText = 'Chatbot';
    this.subMenuImage = './assets/images/chatbot/submenu_chatbot.png';

    this.showCreateIncident = false;
    this.showSearchIncident = false;
    this.showChatbot = true
    this.showMenu = false;
    this.showBack = true;
  }

  showBackOption() {
    this.subMenuText = 'Usuario';
    this.subMenuImage = 'https://cdn-icons-png.freepik.com/512/477/477804.png?ga=GA1.1.378508795.1727907287';

    this.showCreateIncident = false;
    this.showSearchIncident = false;
    this.showChatbot = false
    this.showMenu = true;
    this.showBack = false;
  }

}
