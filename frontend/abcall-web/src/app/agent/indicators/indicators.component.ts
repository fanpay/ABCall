import { Component, OnInit } from '@angular/core';
import { AgentService } from '../agent.service';

@Component({
  selector: 'app-indicators',
  templateUrl: './indicators.component.html',
  styleUrls: ['./indicators.component.css']
})
export class IndicatorsComponent implements OnInit {

  totalIncidentsOpen: number = 0;
  totalIncidentsClose: number = 0;

  constructor(
    private agentService: AgentService
  ) { }

  ngOnInit() {
    this.agentService.getMetrics().subscribe(metrics => {
      this.totalIncidentsOpen = metrics.open_incidents_count;
      this.totalIncidentsClose = metrics.closed_incidents_count;
    });
  }

}
