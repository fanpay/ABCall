import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  showPlan: boolean = false;
  showCreateAgent: boolean = false;
  showBack: boolean = false;
  showMenu: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  showPlanComponent() {
    this.showPlan = true;
    this.showBack = true;
    this.showCreateAgent = false;
    this.showMenu = false
  }

  showCreateAgentComponent() {
    this.showPlan = false;
    this.showBack = true;
    this.showCreateAgent = true;
    this.showMenu = false;
  }

  showBackOption() {
    this.showPlan = false;
    this.showCreateAgent = false;
    this.showBack = false;
    this.showMenu = true;
  }

}
