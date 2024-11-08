import { Component, OnInit } from '@angular/core';
import { AuthService } from '../login/auth.service';

@Component({
  selector: 'app-agent',
  templateUrl: './agent.component.html',
  styleUrls: ['./agent.component.css']
})
export class AgentComponent implements OnInit {
  subMenuText = 'Agente';
  subMenuImage = 'https://cdn-icons-png.freepik.com/512/5759/5759940.png?ga=GA1.1.378508795.1727907287';

  showManageIncident = false;
  showControlPanel = false;
  showIndicators = false;
  showMenu = true;
  showBack = false;

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.isActive();
  }

  showManageIncidentComponent() {
    this.subMenuText = 'Gestionar incidentes';
    this.subMenuImage = './assets/images/agent/submenu_management_incidents.png';

    this.showManageIncident = true;
    this.showControlPanel = false;
    this.showIndicators = false;
    this.showMenu = false;
    this.showBack = true;
  }

  showControlPanelComponent() {
    this.subMenuText = 'Tablero de control';
    this.subMenuImage = './assets/images/agent/submenu_control_panel.png';

    this.showManageIncident = false;
    this.showControlPanel = true;
    this.showIndicators = false;
    this.showMenu = false;
    this.showBack = true;
  }

  showIndicatorsComponent() {
    this.subMenuText = 'Indicadores';
    this.subMenuImage = './assets/images/agent/submenu_indicators.png';

    this.showManageIncident = false;
    this.showControlPanel = false;
    this.showIndicators = true;
    this.showMenu = false;
    this.showBack = true;
  }

  showBackOption() {
    this.subMenuText = 'Agente';
    this.subMenuImage = 'https://cdn-icons-png.freepik.com/512/5759/5759940.png?ga=GA1.1.378508795.1727907287';

    this.showManageIncident = false;
    this.showControlPanel = false;
    this.showIndicators = false;
    this.showMenu = true;
    this.showBack = false;
  }

}
