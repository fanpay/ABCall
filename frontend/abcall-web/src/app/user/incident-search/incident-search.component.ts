import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { Incident } from '../incident';

@Component({
  selector: 'app-incident-search',
  templateUrl: './incident-search.component.html',
  styleUrls: ['./incident-search.component.css']
})
export class IncidentSearchComponent implements OnInit {

  incidentsList !: Incident[];
  selectedIncident !: Incident;
  showDetail = false;

  constructor(
    private userService: UserService
  ) { }

  ngOnInit() {
    this.incidentsList = this.userService.getIncidents();
  }

  onSelect(incident: Incident): void {
    this.selectedIncident = incident;
    this.showDetail = true;
  }

}
